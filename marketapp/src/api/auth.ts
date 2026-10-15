import type { LoginDto, RegisterDto } from "@type/auth";
import { get, post } from "./http";

export const login = (loginDto: LoginDto) => {
    return post<LoginDto, string>("/auth/login", loginDto)
}

export const register = (registerDto: RegisterDto) => {
    return post<RegisterDto, never>("/user/register", registerDto)
}