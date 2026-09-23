# LZMall 我的多商户商城项目

## 项目介绍

LZMall 是一个基于 **Spring Boot 3 + Vue 3** 的多商户商城系统，采用前后端分离架构。

- 后端 `LZMall-parent`：Maven 多模块工程，按「通用能力 / 数据模型 / 安全认证 / 业务逻辑 / 接口层 / 启动器」分层，模块间依赖清晰、职责单一。
- 前端 `LZMall_App`：Vite + Vue 3 + TypeScript 单页应用，配套 Element Plus 组件库与 TailwindCSS 原子化样式。

当前版本（`1.1.0-SNAPSHOT`）已完成**用户认证与用户信息管理**闭环：注册、登录、JWT 双 Token 无感续期、单设备登录互斥、路由鉴权、用户信息查询与昵称修改。商品、购物车、订单、支付、多商户等业务模块在规划中（见文末「计划」）。

### 核心特性

- **JWT 双 Token 机制**：Access Token（30 分钟，前端持有）+ Refresh Token（7 天，HttpOnly Cookie），Refresh Token 落 Redis 校验，支持无感刷新。
- **单设备登录互斥**：同一账号再次登录时，旧 Refresh Token 进入黑名单；旧设备续期时提示「账号已在其它设备登录」。
- **统一响应与全局异常**：`Result<T>` 统一返回结构 + 业务状态码枚举 `ResultCode` + 全局异常处理器。
- **自动填充与逻辑删除**：MyBatis-Plus 自动填充 `create_time / update_time`，`@TableLogic` 逻辑删除。
- **前端统一拦截**：Axios 请求拦截注入 Token，响应拦截统一处理业务码与 HTTP 状态码，401 时自动排队刷新 Token。
- **路由守卫**：基于登录状态与角色的导航守卫，自动设置页面标题与 favicon。
- **UI 规范**：TailwindCSS 原子化样式 + Element Plus 中文组件库，Sass 变量与 mixin 全局注入。

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
| - | - | - |
| Spring Boot | 3.5.15 | 基础框架（parent） |
| JDK | 17 | 编译与运行版本 |
| MyBatis-Plus | 3.5.17 | ORM（`mybatis-plus-spring-boot3-starter`） |
| MySQL Connector/J | 8.0.33 | 数据库驱动 |
| Spring Data Redis | — | 缓存 Refresh Token、黑名单与用户信息 |
| Spring Security | — | 认证授权框架 |
| Hutool | 5.8.32 | 工具库（本项目 JWT 由 `JwtUtils` 基于 Hutool `JWTUtil` 实现） |
| JJWT | 0.12.5 | JWT 依赖（已在 `lzm-security` 引入） |
| Spring AMQP | — | 消息队列（已引入，预留后续异步场景） |
| Lombok | — | 简化实体 / DTO 代码 |

> 以下依赖版本已在父 POM 的 `dependencyManagement` 统一管理，待业务接入后使用：Druid、springdoc-openapi、MinIO、阿里云 OSS、微信支付 Java SDK、支付宝 SDK。

### 前端

| 技术 | 版本 | 说明 |
| - | - | - |
| Vue | ^3.5.42 | 渐进式框架（`<script setup>` SFC） |
| TypeScript | ~6.0.2 | 类型系统 |
| Vite | ^8.3.0 | 构建工具 / 开发服务器 |
| Vue Router | ^5.3.1 | 路由与导航守卫 |
| Pinia | ^4.0.3 | 状态管理（含 `pinia-plugin-persistedstate` 持久化） |
| Element Plus | ^2.14.6 | UI 组件库（中文 locale） |
| TailwindCSS | ^4.3.3 | 原子化 CSS（`@tailwindcss/vite`） |
| FontAwesome | ^7.x | 图标库 |
| Axios | ^1.20.0 | HTTP 客户端 |
| Sass | ^1.104.1 | CSS 预处理器 |
| pnpm | — | 包管理器（见 `pnpm-lock.yaml`） |

## 项目结构

```
LZMall/
├── LZMall-parent/                 # 后端 Maven 多模块工程
│   ├── lzm-common/                # 通用模块：统一返回、全局异常、Redis、实体基类、工具类
│   ├── lzm-model/                 # 数据模型：entity / dto / vo
│   ├── lzm-security/              # 安全模块：Spring Security 配置、JWT 生成与校验、认证过滤器
│   ├── lzm-service/               # 业务模块：service 接口与实现、mapper
│   ├── lzm-web/                   # 接口模块：controller
│   ├── lzm-start/                 # 启动模块：主类、配置文件、日志（唯一可执行模块）
│   └── pom.xml                    # 父 POM：统一版本与依赖管理
└── LZMall_App/                    # 前端 Vue 3 应用
    ├── src/
    │   ├── api/                   # 接口封装（userAuth / check）
    │   ├── router/                # 路由与守卫（modules/authRouter）
    │   ├── store/                 # Pinia 状态（userStore / useGlobalLoad）
    │   ├── types/                 # TS 类型（user / http / env）
    │   ├── enum/                  # 枚举（resultCode / icons / regexp）
    │   ├── utils/                 # Axios 封装与请求队列
    │   ├── views/                 # 页面（auth / error，mall、management 预留）
    │   ├── plugin/                # 插件化工具（storage / generateId）
    │   ├── assets/style/          # 全局样式、Sass 变量与 mixin
    │   └── main.ts                # 应用入口
    ├── vite.config.ts             # Vite 配置（路径别名、开发代理、SCSS 注入）
    └── .env                       # 环境变量（VITE_ 前缀）
```

### 后端模块依赖关系

```
lzm-start  ->  lzm-web  ->  lzm-service  ->  lzm-security  ->  lzm-common
                                 |                |
                                 +-- lzm-model ---+
```

| 模块 | 依赖 | 职责 |
| - | - | - |
| `lzm-common` | 无 | 最底层，被所有模块依赖；统一返回、异常、Redis 工具、实体基类 |
| `lzm-model` | `lzm-common`、spring-security | 实体、DTO、VO |
| `lzm-security` | `lzm-common`、spring-security、jjwt | 安全过滤链、JWT 生成校验 |
| `lzm-service` | `lzm-common`、`lzm-security`、`lzm-model`、mybatis-plus、mysql | 业务实现与持久层 |
| `lzm-web` | `lzm-service` | REST 接口层 |
| `lzm-start` | `lzm-web`、spring-boot-starter-web | 启动入口（主类 `org.lzmstart.LzmStartApplication`），唯一打包为可执行 jar |

## 依赖需求

### 环境要求

| 依赖 | 最低版本 | 说明 |
| - | - | - |
| JDK | 17 | 后端编译 / 运行 |
| Maven | 3.9+ | 后端构建 |
| MySQL | 8.0+ | 业务数据库，库名 `lzmall` |
| Redis | 6.0+ | Token 与用户信息缓存 |
| Node.js | 20.19+ | 前端构建（Vite 8 要求） |
| pnpm | 9+ | 前端包管理器（也可使用 npm） |

> 可选：RabbitMQ（`lzm-common` 已引入 `spring-boot-starter-amqp`）；MinIO / 阿里云 OSS（对象存储，版本已管理，待接入）。

### 数据库

启动前需创建数据库 `lzmall` 并初始化 `user` 表，核心字段：

| 字段 | 类型 | 说明 |
| - | - | - |
| `id` | BIGINT | 主键，自增 |
| `username` | VARCHAR | 用户名，唯一 |
| `password` | VARCHAR | BCrypt 加密后的密码 |
| `nickname` | VARCHAR | 昵称 |
| `email` | VARCHAR | 邮箱 |
| `phone` | VARCHAR | 手机号 |
| `avatar` | VARCHAR | 头像地址 |
| `create_time` / `update_time` | TIMESTAMP | 自动填充 |
| `is_delete` | TINYINT | 逻辑删除标记 |

### 关键配置

后端 `lzm-start/src/main/resources/application.yaml`：

| 配置项 | 默认值 | 说明 |
| - | - | - |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/lzmall` | MySQL 连接 |
| `spring.datasource.username / password` | `root / 123456` | 数据库账号 |
| `spring.data.redis.host / port` | `localhost / 6379` | Redis 连接（密码 `123456`） |
| `spring.datasource.hikari.*` | — | HikariCP 连接池参数 |
| `jwt.secret` | 内置示例密钥 | JWT 签名密钥（**生产环境务必替换**） |
| `jwt.access-expiration` | `1800000`（30 分钟） | Access Token 有效期 |
| `jwt.refresh-expiration` | `604800000`（7 天） | Refresh Token 有效期 |
| `jwt.header / prefix` | `Authorization` / `Bearer` | 令牌请求头与前缀 |
| `security.ignore.urls` | 见配置 | 免认证白名单 |

前端 `LZMall_App/.env`：

```
VITE_API_BASE_URL="/api"
VITE_API_TIMEOUT=10000
```

> 开发环境下前端请求 `/api/**` 由 Vite 代理到 `http://127.0.0.1:8080` 并去掉 `/api` 前缀（见 `vite.config.ts` 的 `server.proxy`）。

## 快速开始

### 1. 启动后端

```bash
# 在 LZMall-parent 目录下
mvn clean install -DskipTests

# 运行启动模块
cd lzm-start
mvn spring-boot:run
```

后端默认监听 `http://localhost:8080`。

### 2. 启动前端

```bash
# 在 LZMall_App 目录下
pnpm install
pnpm dev
```

前端开发服务器监听 `http://localhost:8081`，通过开发代理访问后端。

### 3. 打包构建

```bash
# 后端打包（产物：lzm-start/target/*.jar）
mvn clean package -DskipTests

# 前端构建（产物：LZMall_App/dist）
cd LZMall_App && pnpm build
```

## 接口一览

| 方法 | 路径 | 说明 | 免认证 |
| - | - | - | - |
| POST | `/user/register` | 用户注册 | 是 |
| POST | `/user/login` | 用户登录，返回 Access Token 并写入 Refresh Token Cookie | 是 |
| POST | `/user/refreshAccess` | 使用 Refresh Token 刷新 Access Token | 是（依赖 Cookie） |
| GET | `/user/info` | 获取当前登录用户信息 | 否 |
| PUT | `/user/updateNickname` | 修改当前用户昵称 | 否 |
| GET | `/test/checkLink` | 服务连通性检查 | 是 |

统一响应结构：

```json
{ "code": 200, "message": "操作成功", "data": {} }
```

## 计划

| feature | desc | version |
| - | - | - |
| 用户认证 | 注册、登录、JWT 双 Token 刷新、单设备登录互斥、路由鉴权 | 1.1.0 已完成 |
| 用户中心 | 用户信息查询、昵称修改、头像上传 | 1.1.0 进行中 |
| 商品模块 | 商品列表 / 详情、分类、搜索、SKU 规格 | 1.2.0 规划中 |
| 购物车与订单 | 购物车、下单、订单状态流转、库存扣减 | 1.2.0 规划中 |
| 支付模块 | 微信支付、支付宝支付、支付回调与对账 | 1.3.0 规划中 |
| 文件存储 | MinIO / 阿里云 OSS 接入，统一文件上传 | 1.3.0 规划中 |
| 多商户 | 商户入驻、店铺管理、商户结算与分账 | 1.4.0 规划中 |
| 后台管理 | 管理端登录、商品 / 订单 / 商户管理、数据看板 | 1.4.0 规划中 |
