import type { RouteObject } from "react-router";

import Auth from "../../views/auth/auth";
import Login from "../../views/auth/login";
import Register from "../../views/auth/register";

export const auth: RouteObject[] = [
    {
        path: "/auth",
        element: <Auth />,
        children: [
            {
                path: "login?",
                element: <Login />
            },
            {
                path: "register",
                element: <Register />
            }
        ]
    }
]