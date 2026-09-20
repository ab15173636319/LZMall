<template>
    <div class="page-center-bg bg-[url(/images/auth-bg.png)] bg-cover bg-bottom">
        <div class="w-[95vw] max-w-100 p-5 py-10 rounded-xl bg-white dark:bg-[#111]">
            <el-form ref="ruleFormRef" style="max-width: 600px" :model="formDate" status-icon :rules="rules"
                label-width="auto" class="demo-ruleForm" label-position="top">
                <el-form-item label="用户名" prop="username">
                    <el-input size="large" v-model="formDate.username" clearable type="text" autocomplete="off" />
                </el-form-item>
                <el-form-item label="密码" prop="username">
                    <el-input size="large" v-model="formDate.username" clearable type="password" autocomplete="off" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm(ruleFormRef)">
                        登录
                    </el-button>
                    <el-button @click="resetForm(ruleFormRef)">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'

import type { FormInstance, FormRules } from 'element-plus'

const ruleFormRef = ref<FormInstance>()

const validateUsername = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    }
    callback()
}
const validatePassword = (rule: any, value: any, callback: any) => {
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
    formEl.validate((valid) => {
        if (valid) {
            console.log('submit!')
        } else {
            console.log('error submit!')
        }
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
}
</script>
