interface IUserInfo {
    id: number
    username: string
    nickname: string
    email: string
    phone: string
    avatar: string
    role: string[]
    /** 后端 UserVo 为驼峰字段 */
    createTime: string
    updateTime: string
    sex?: "m" | "f" | "n"
}

export type UserInfo = IUserInfo

interface ILoginDto {
    username: string
    password: string
}

export type LoginDto = ILoginDto


interface IAuthData {
    accessToken: string
}

export type AuthData = IAuthData

interface IRegisterDto {
    username: string
    password: string
    validatePassword: string
}

export type RegisterDto = IRegisterDto

interface IUpdateNicknameDto {
    nickname: string
}

export type UpdateNicknameDto = IUpdateNicknameDto