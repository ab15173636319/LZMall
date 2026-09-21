import { generateId } from "./generateId"

const STORAGE_TYPE = {
    LOCAL: localStorage,
    SESSION: sessionStorage,
} as const

export type StorageType = keyof typeof STORAGE_TYPE

const EXP = {
    "millisecond": 1,
    "seconds": 1000,
    "minutes": 60000,
    "hour": 3600000,
    "day": 86400000,
    "month": 604800000,
    "year": 31536000000,
} as const

export type TimeUnit = keyof typeof EXP


interface Save_Obj {
    id: string,
    value: any,
    exp: number | "Infinity"
}

export const useStorage = <T>(key: string, localeType: StorageType) => {
    const storage = STORAGE_TYPE[localeType]
    const set = (value: T, exp?: number, timeUnit?: TimeUnit) => {
        if (timeUnit && !exp) {
            throw new Error(`设置了时间单位后，必须设置过期时间`)
        }
        const save_obj: Save_Obj = {
            id: generateId(),
            value,
            exp: exp && timeUnit ? Date.now() + exp * EXP[timeUnit] : 'Infinity'
        }
        storage.setItem(key, JSON.stringify(save_obj))
    }
    const get = (): T | null => {
        const save_obj: Save_Obj = JSON.parse(storage.getItem(key) || "{}")
        if (save_obj.exp === 'Infinity') {
            return save_obj.value
        }
        if (save_obj.exp < Date.now()) {
            storage.removeItem(key)
            return null
        }
        return save_obj.value
    }
    const remove = () => {
        storage.removeItem(key)
    }
    return {
        set,
        get,
        remove
    }
}
