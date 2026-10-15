interface IResult<T> {
    code: number,
    data: T,
    message: string
}

export type Result<T> = IResult<T>

