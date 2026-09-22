// 临时调试脚本：模拟 http-proxy 的 Node http 客户端向后端发请求
// 用法: node test-proxy-debug.js
const http = require('http');

function post(port, path, body) {
  return new Promise((resolve) => {
    const data = JSON.stringify(body);
    const req = http.request({
      host: '127.0.0.1',
      port,
      path,
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(data),
      },
    }, (res) => {
      let raw = '';
      res.on('data', (d) => { raw += d.toString(); });
      res.on('end', () => resolve({ ok: true, statusCode: res.statusCode, statusMessage: res.statusMessage, headers: res.headers, body: raw }));
    });
    req.on('error', (e) => resolve({ ok: false, error: e.message, stack: e.stack }));
    req.write(data);
    req.end();
  });
}

(async () => {
  console.log('=== 1) 失败路径：未知用户登录（后端 :8080 直连） ===');
  const r1 = await post(8080, '/user/login', { username: 'test_unknown', password: '123456' });
  console.log(JSON.stringify(r1, null, 2));

  console.log('\n=== 2) 注册一个新用户（后端 :8080 直连） ===');
  const uname = 'debuguser' + Date.now();
  const r2 = await post(8080, '/user/register', { username: uname, password: '123456', validatePassword: '123456' });
  console.log(JSON.stringify(r2, null, 2));

  console.log('\n=== 3) 成功路径：用新用户登录（后端 :8080 直连，带 Set-Cookie） ===');
  const r3 = await post(8080, '/user/login', { username: uname, password: '123456' });
  console.log(JSON.stringify(r3, null, 2));
})();
