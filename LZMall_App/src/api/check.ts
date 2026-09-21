import { get } from "./http"

export const checkLink = async () => {
    return get("/test/checkLink", {})
}