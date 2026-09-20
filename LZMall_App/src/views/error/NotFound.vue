<script setup lang="ts">
import { ElButton, ElSpace } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter();

const toIndex = () => {
    router.push("/");
}
</script>

<template>


    <div class=" page-center-bg flex-col">
        <ElSpace :size="100" direction="vertical">
            <h1 class=" text-2xl dark:text-white">世上总会有猝不及防的再见，和毫不留情的散场</h1>
            <el-button type="danger" size="large" dashed @click="toIndex">返回首页</el-button>
        </ElSpace>
    </div>

</template>

<style scoped lang="scss"></style>