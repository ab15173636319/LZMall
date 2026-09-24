interface IUserInfo {
    id: number
    username: string
    nickname: string
    email: string
    phone: string
    avatar: string
    role: string[]
    create_time: string
    update_time: string
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