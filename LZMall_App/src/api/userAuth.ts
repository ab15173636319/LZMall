import type { AuthData, LoginDto, RegisterDto, UpdateNicknameDto, UserInfo } from "@/types/user";
import { get, post, put } from "../utils/http";
import type { Response } from "@/types/http";

export const login = async (loginDto: LoginDto): Promise<Response<AuthData>> => {
    return await post("/user/login", loginDto)
}
export const register = async (registerDto: RegisterDto): Promise<Response<null>> => {
    return await post("/user/register", registerDto)
}

export const getInfo = async (): Promise<Response<UserInfo>> => {
    return await get("/user/info", {})
}

export const refreshAccess = async (): Promise<Response<string>> => {
    return await post("/user/refreshAccess", {})
}

export const updateNickname = async (updateNicknameDto: UpdateNicknameDto): Promise<Response<string>> => {
    return await put("/user/updateNickname", updateNicknameDto)
}