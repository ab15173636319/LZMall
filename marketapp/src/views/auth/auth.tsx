import { Outlet } from "react-router";


export default function Auth() {
    return (
        <div className=" full-screen center-xy bg-gray-800">
            <Outlet />
        </div>
    );
}