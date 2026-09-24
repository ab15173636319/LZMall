<template>
    <div class="flex flex-col bg-white">
        <!-- 导航栏 -->
        <header class="shrink-0 border-b border-gray-100 shadow-sm">
            <div class="mx-auto max-w-7xl px-4 sm:px-6">
                <div class="flex h-16 items-center justify-between">
                    <router-link to="/" class="text-xl font-bold text-blue-600">
                        LZ Mall
                    </router-link>
                    <div class="hidden items-center gap-8 md:flex">
                        <router-link to="/" class="transition"
                            :class="isIndex ? 'font-medium text-blue-600' : 'text-gray-600 hover:text-blue-600'">
                            首页
                        </router-link>
                        <template v-if="isIndex">
                            <a class="text-gray-600 transition hover:text-blue-600">功能</a>
                            <a class="text-gray-600 transition hover:text-blue-600">关于</a>
                            <a class="text-gray-600 transition hover:text-blue-600">联系我们</a>
                        </template>
                        <router-link v-else to="/profile" class="font-medium text-blue-600">个人中心</router-link>
                    </div>
                    <template v-if="user.isLogin">
                        <user-box v-model="user.userInfo"></user-box>
                    </template>
                    <template v-else>
                        <el-button type="primary" size="small" @click="router.push({ name: 'login' })">登录</el-button>
                    </template>
                </div>
            </div>
        </header>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import UserBox from '@/components/auth/UserBox.vue';
import { useUser } from '@/store';

const user = useUser()
const route = useRoute()
const router = useRouter()

const isIndex = computed(() => route.name === 'index')

onMounted(async () => {
    await user.getUserInfo()
})
</script>
