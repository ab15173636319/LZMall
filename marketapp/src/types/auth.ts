interface ILoginDto {
    username: string;
    password: string;
}

export type LoginDto = ILoginDto

interface IRegisterDto {
    username: string;
    password: string;
    validatePassword: string;
}

export type RegisterDto = IRegisterDto