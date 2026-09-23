import { get } from "../utils/http"

export const checkLink = async () => {
    return get("/test/checkLink", {})
}