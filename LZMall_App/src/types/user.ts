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