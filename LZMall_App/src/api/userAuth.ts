import type { AuthData, LoginDto, RegisterDto, UserInfo } from "@/types/user";
import { post } from "./http";
import type { Response } from "@/types/http";

export const login = async (loginDto: LoginDto): Promise<Response<AuthData>> => {
    return await post("/user/login", loginDto)
}
export const register = async (registerDto: RegisterDto): Promise<Response<null>> => {
    return await post("/user/register", registerDto)
}