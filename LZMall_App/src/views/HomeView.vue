<template>
    <div class="min-h-screen bg-white flex flex-col">
        <!-- 导航栏 -->
        <header class="shadow-sm border-b border-gray-100">
            <div class="max-w-7xl mx-auto px-4 sm:px-6">
                <div class="flex justify-between items-center h-16">
                    <div class="text-xl font-bold text-blue-600">DemoHome</div>
                    <div class="hidden md:flex items-center gap-8">
                        <a class="text-blue-600 font-medium">首页</a>
                        <a class="text-gray-600 hover:text-blue-600 transition">功能</a>
                        <a class="text-gray-600 hover:text-blue-600 transition">关于</a>
                        <a class="text-gray-600 hover:text-blue-600 transition">联系我们</a>
                    </div>
                    <el-button type="primary" size="small">登录</el-button>
                </div>
            </div>
        </header>

        <!-- 主内容区 -->
        <main class="flex-1 flex items-center justify-center px-4">
            <div class="max-w-2xl text-center">
                <h2 class="text-[clamp(2rem,5vw,3rem)] font-bold text-gray-800 leading-tight">
                    简洁高效的<br />前端通用首页
                </h2>
                <p class="mt-5 text-gray-500 text-lg">
                    基于 TailwindCSS + Element Plus 快速搭建，适配手机、电脑端
                </p>
                <div class="mt-8 flex justify-center gap-4 flex-wrap">
                    <el-button type="primary" size="large">立即体验</el-button>
                    <el-button size="large">了解更多</el-button>
                </div>
            </div>
        </main>

        <!-- 页脚 -->
        <footer class="py-6 text-center text-sm text-gray-400 border-t border-gray-100">
            Copyright © 2026 DemoHome All Rights Reserved
        </footer>
    </div>
</template>

<script setup>



import { useUser } from '@/store';
import { onMounted } from 'vue';
const user = useUser()

onMounted(async () => {
    await user.getUserInfo()
})



</script>
