import { createBrowserRouter } from "react-router";
import Home from "../views/home";
import NotFound from "../views/error/notFound";
import { auth } from "./module/auth";




export const router = createBrowserRouter([
    {
        path: "/",
        Component: Home
    },
    ...auth,
    {
        path: "*",
        Component: NotFound
    }
])