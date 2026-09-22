// 临时调试脚本2：成功路径 + keep-alive 复用测试
const http = require('http');

const agent = new http.Agent({ keepAlive: true, maxSockets: 1 });

function post(port, path, body, useAgent) {
  return new Promise((resolve) => {
    const data = JSON.stringify(body);
    const opts = {
      host: '127.0.0.1',
      port,
      path,
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(data),
        'Connection': useAgent ? 'keep-alive' : 'close',
      },
    };
    if (useAgent) opts.agent = agent;
    const req = http.request(opts, (res) => {
      let raw = '';
      res.on('data', (d) => { raw += d.toString(); });
      res.on('end', () => resolve({ ok: true, statusCode: res.statusCode, statusMessage: JSON.stringify(res.statusMessage), setCookie: res.headers['set-cookie'] || null, body: raw }));
    });
    req.on('error', (e) => resolve({ ok: false, error: e.message }));
    req.write(data);
    req.end();
  });
}

(async () => {
  const users = [
    { username: '111111', passwords: ['111111', '123456'] },
    { username: 'example', passwords: ['123456', 'example'] },
    { username: 'example2', passwords: ['123456', 'example2'] },
  ];

  for (const u of users) {
    for (const pw of u.passwords) {
      const r = await post(8080, '/user/login', { username: u.username, password: pw });
      const okLogin = r.ok && r.body && r.body.includes('accessToken');
      console.log(`login ${u.username}/${pw} => status=${r.statusCode} setCookie=${r.setCookie ? 'YES' : 'NO'} success=${okLogin} body=${r.body ? r.body.slice(0, 120) : r.error}`);
      if (okLogin) break;
    }
  }

  console.log('\n=== 成功路径拿到后，用同一 keep-alive 连接连续发 10 次（模拟 http-proxy 连接池） ===');
  // 先找到能登录的账号
  let good = null;
  for (const u of users) {
    const r = await post(8080, '/user/login', { username: u.username, password: u.passwords[0] });
    if (r.ok && r.body && r.body.includes('accessToken')) { good = { username: u.username, password: u.passwords[0] }; break; }
  }
  if (!good) { console.log('没有找到能成功登录的账号，改用未知账号测试连接复用（失败路径同样验证报文完整性）'); good = { username: 'test_unknown', password: '123456' }; }
  console.log('使用账号:', JSON.stringify(good));

  for (let i = 0; i < 10; i++) {
    const r = await post(8080, '/user/login', { username: good.username, password: good.password }, true);
    console.log(`req#${i + 1}: ok=${r.ok} status=${r.statusCode || '-'} err=${r.error || '-'} setCookie=${r.setCookie ? 'YES' : 'NO'} body=${r.body ? r.body.slice(0, 80) : ''}`);
  }
})();
