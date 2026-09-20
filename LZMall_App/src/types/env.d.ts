/**
 * Vite 环境变量类型声明
 *
 * 规则：
 *  1. 只有以 `VITE_` 开头的变量才会被注入到 `import.meta.env`；
 *  2. 变量名必须与代码里读取的名字完全一致，写错不会报错，只会是 undefined；
 *  3. 值统一是字符串（数字也要写成 10000，读取后用 Number() 转换）；
 *  4. 修改 .env 后必须重启 dev server 才会生效。
 *
 * 这里声明接口后，`import.meta.env.VITE_API_BASE_URL` 就有类型提示和拼写校验了。
 */
interface ImportMetaEnv {
    /** 接口基础路径，如 "/api" */
    readonly VITE_API_BASE_URL: string
    /** 请求超时时间（毫秒），字符串形式 */
    readonly VITE_API_TIMEOUT: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}
