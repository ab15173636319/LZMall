import BaseManAvatar from "/images/default_man_avatar.png";
import BaseFemaleAvatar from "/images/default_female_avatar.png";
import BaseMysteriousAvatar from "/images/default_mysterious_avatar.png";
import type { UserInfo } from "@/types/user";

/**
 * 头像兜底策略：
 * 1. 用户已设置头像 → 用用户的；
 * 2. 未设置但知道性别 → 用对应性别的默认头像；
 * 3. 其它情况 → 神秘头像。
 *
 * 后端目前不返回 sex 字段，实际会走到第 3 步，保留判断便于后续扩展。
 */
export const resolveAvatar = (userInfo?: UserInfo | null) => {
    if (userInfo?.avatar) return userInfo.avatar
    if (userInfo?.sex === "f") return BaseFemaleAvatar
    if (userInfo?.sex === "m") return BaseManAvatar
    return BaseMysteriousAvatar
}
