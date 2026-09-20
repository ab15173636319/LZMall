import type { LoginDto, UserInfo } from "@/types/user";
import { post } from "./http";
import type { Response } from "@/types/http";

export const login = async (loginDto: LoginDto): Promise<Response<UserInfo>> => {
    return await post("/user/login", loginDto)
}