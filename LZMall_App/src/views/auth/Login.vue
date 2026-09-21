<template>
    <div class="page-center-bg bg-[url(/images/auth-bg.png)] bg-cover bg-bottom">
        <div
            class="w-[95vw] max-w-sm px-8 py-10 rounded-card bg-white/90 dark:bg-page-dark/90 backdrop-blur-md shadow-xl shadow-black/10 ring-1 ring-black/5 dark:ring-white/10">
            <!-- 品牌区 -->
            <div class="flex flex-col items-center gap-3 mb-8">
                <div
                    class="w-14 h-14 rounded-2xl bg-linear-to-br from-brand to-sky-500 flex items-center justify-center shadow-lg shadow-brand/40">
                    <font-awesome-icon icon="bag-shopping" class="text-2xl text-white" />
                </div>
                <div class="text-center">
                    <h1 class="text-xl font-bold text-gray-900 dark:text-gray-100">欢迎回来</h1>
                    <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">登录 LZMall，开启购物之旅</p>
                </div>
            </div>

            <el-form ref="ruleFormRef" :model="formDate" status-icon :rules="rules" label-position="top" size="large">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="formDate.username" clearable prefix-icon="user" placeholder="请输入用户名"
                        autocomplete="username" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="formDate.password" type="password" show-password prefix-icon="lock"
                        placeholder="请输入密码" autocomplete="current-password" />
                </el-form-item>
                <el-form-item>
                    <el-button class="w-full" type="primary" size="large" @click="submitForm(ruleFormRef)">
                        登 录
                    </el-button>
                </el-form-item>
                <div class="flex items-center justify-between text-sm">
                    <el-link type="info" :underline="false" @click="resetForm(ruleFormRef)">重置</el-link>
                    <el-link type="primary" :underline="false" @click="router.push({ name: 'register' })">
                        没有账号？免费注册
                    </el-link>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'

import type { FormInstance, FormRules } from 'element-plus'
import { login } from '@/api/userAuth'
import { useUser } from '@/store'
import router from '@/router'

const userAuth = useUser()
const ruleFormRef = ref<FormInstance>()

const validateUsername = (_rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    }
    callback()
}
const validatePassword = (_rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入密码'))
    }
    callback()
}

const formDate = reactive({
    username: '',
    password: ''
})

const rules = reactive<FormRules<typeof formDate>>({
    username: [{ validator: validateUsername, trigger: 'blur' }],
    password: [{ validator: validatePassword, trigger: 'blur' }],
})

const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate(async (valid) => {
        if (valid) {
            const res = await login(formDate)
            if (res.code === 200) {
                userAuth.refreshToken = res.data.refreshToken
                userAuth.accessToken = res.data.accessToken
                router.push({ name: 'index' })
            }
        }
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
}
</script>
