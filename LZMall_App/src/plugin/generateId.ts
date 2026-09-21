import { v4 } from "uuid";

export const generateId = () => v4().replaceAll("-", "")

/**
 * 生成多个id
 * @param amount 
 * @returns 
 */
export const generateIds = (amount: number): string[] => {
    if (amount <= 0 || !Number.isInteger(amount)) {
        throw new Error(`'amount'必须是大于0的整数`)
    }
    const ids: string[] = []
    for (let i = 0; i < amount; i++) {
        ids.push(generateId())
    }
    return ids
}

export const generateIdWithPrefix = (prefix: string) => `${prefix}_${generateId()}`