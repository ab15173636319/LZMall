
export const ICONS = {
    ERROR: { code: "error", path: "/icons/error.svg" },
    INDEX: { code: "index", path: "/icons/index.svg" },
} as const


export type IconKey = keyof typeof ICONS


export type IconItem = (typeof ICONS)[IconKey]


export const ICON_KEYS = Object.keys(ICONS) as IconKey[]

export default ICONS
