<template>
    <div class="min-h-screen bg-gray-50 flex items-center justify-center px-4">
        <div class="max-w-md w-full text-center animate-fade-in">
            <el-icon size="120" class="text-gray-300 mb-6">
                <WifiOff />
            </el-icon>
            <h1 class="text-[clamp(1.5rem,4vw,2rem)] font-bold text-gray-800 mb-3">网络连接异常</h1>
            <p class="text-gray-500 mb-8">当前网络不可用，请检查你的网络设置后重试</p>
            <div class="flex justify-center gap-4 flex-wrap">
                <el-button type="primary" @click="handleRefresh">重新刷新</el-button>
                <el-button @click="goHome">返回首页</el-button>
            </div>
            <p class="mt-10 text-sm text-gray-400">错误代码：NETWORK_ERROR</p>
        </div>
    </div>
</template>


<script setup lang="ts">
import router from '@/router';


const handleRefresh = () => {
    location.reload()
}

const goHome = () => {
    router.push({ name: 'index' })
}

</script>

<style scoped>
.animate-fade-in {
    animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>
