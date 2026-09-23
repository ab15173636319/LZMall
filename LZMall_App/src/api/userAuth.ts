import type { AuthData, LoginDto, RegisterDto, UserInfo } from "@/types/user";
import { get, post } from "../utils/http";
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

export const refreshAccess = async (): Promise<Response<String>> => {
    return await post("/user/refresh", {})
}