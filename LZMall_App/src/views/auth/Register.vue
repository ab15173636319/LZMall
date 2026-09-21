<template>
    <div class="page-center-bg bg-[url(/images/auth-bg.png)] bg-cover bg-bottom">
        <div
            class="w-[95vw] max-w-sm px-8 py-10 rounded-card bg-white/90 dark:bg-page-dark/90 backdrop-blur-md shadow-xl shadow-black/10 ring-1 ring-black/5 dark:ring-white/10">
            <!-- 品牌区 -->
            <div class="flex flex-col items-center gap-3 mb-8">
                <div
                    class="w-14 h-14 rounded-2xl bg-linear-to-br from-brand to-sky-500 flex items-center justify-center shadow-lg shadow-brand/40">
                    <font-awesome-icon icon="user-plus" class="text-2xl text-white" />
                </div>
                <div class="text-center">
                    <h1 class="text-xl font-bold text-gray-900 dark:text-gray-100">创建账号</h1>
                    <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">注册 LZMall，好物即刻拥有</p>
                </div>
            </div>

            <el-form ref="ruleFormRef" :model="formDate" status-icon :rules="rules" label-position="top" size="large">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="formDate.username" clearable prefix-icon="user" placeholder="4-16 位字母、数字或下划线"
                        autocomplete="username" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="formDate.password" type="password" show-password prefix-icon="lock"
                        placeholder="8-20 位，含字母和数字" autocomplete="new-password" />
                </el-form-item>
                <el-form-item label="确认密码" prop="validatePassword">
                    <el-input v-model="formDate.validatePassword" type="password" show-password prefix-icon="lock"
                        placeholder="请再次输入密码" autocomplete="new-password" />
                </el-form-item>
                <el-form-item>
                    <el-button class="w-full" type="primary" size="large" @click="submitForm(ruleFormRef)">
                        注 册
                    </el-button>
                </el-form-item>
                <div class="flex items-center justify-between text-sm">
                    <el-link type="info" :underline="false" @click="resetForm(ruleFormRef)">重置</el-link>
                    <el-link type="primary" :underline="false" @click="router.push({ name: 'login' })">
                        已有账号？直接登录
                    </el-link>
                </div>
            </el-form>
        </div>
    </div>
</template>


<script lang="ts" setup>
import { reactive, ref } from 'vue'

import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { RE } from '@/enum/regexp'
import { register } from '@/api/userAuth'
import router from '@/router'

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
    } else if (RE.password.test(value)) {
        callback(new Error('密码只能包含字母和数字'))
    }
    callback()
}
const validateValidatePassword = (_rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入确定密码'))
    } else if (value !== formDate.password) {
        callback(new Error('两次密码不一致'))
    }
    callback()
}

const formDate = reactive({
    username: '',
    password: '',
    validatePassword: ""
})

const rules = reactive<FormRules<typeof formDate>>({
    username: [
        { validator: validateUsername, trigger: 'blur' },
        { min: 6, max: 20, trigger: 'blur', message: "用户名不得超过20个字符，不得小于6个字符" }


    ],
    password: [
        { validator: validatePassword, trigger: 'blur' },
        { min: 6, max: 20, trigger: 'blur', message: "密码不得超过20个字符，不得小于6个字符" }
    ],
    validatePassword: [{ validator: validateValidatePassword, trigger: 'blur' }],
})

const submitForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate(async (valid) => {
        if (valid) {
            const res = await register(formDate)
            console.log(res);

            if (res.code === 200) {
                router.push({ name: 'login' })
            }
        } else {
            ElMessage.warning("请检查输入")
        }
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
}
</script>
