<template>
    <navecation>
        <div class="m-auto max-w-2xl px-4 text-center">
            <h2 class="text-[clamp(2rem,5vw,3rem)] font-bold leading-tight text-gray-800">
                简洁高效的<br />前端通用首页
            </h2>
            <p class="mt-5 text-lg text-gray-500">
                基于 TailwindCSS + Element Plus 快速搭建，适配手机、电脑端
            </p>
            <div class="mt-8 flex flex-wrap justify-center gap-4">
                <el-button type="primary" size="large" @click="router.push('/profile')">进入用户中心</el-button>
                <el-button size="large">了解更多</el-button>
            </div>
        </div>
    </navecation>
</template>

<script setup lang="ts">
import Navecation from '@/layout/Navecation.vue';
import { useRouter } from 'vue-router';

const router = useRouter()
</script>
