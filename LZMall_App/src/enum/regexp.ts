export const RE = {
    username: new RegExp("^[a-zA-Z0-9_]{4,16}$"),
    password: new RegExp("^(?=.*[A-Za-z])(?=.*\d)\S{8,20}$"),
    phoneCN: new RegExp("^1[3-9]\d{9}$"),
    email: new RegExp("^[\w.%+-]+@[\w.-]+\.[A-Za-z]{2,}$"),
    idCard: new RegExp("^\d{17}[\dXx]$"),
} as const



