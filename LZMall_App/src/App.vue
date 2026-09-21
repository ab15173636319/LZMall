<script setup lang="ts">
import { RouterView } from 'vue-router';
import { useGlobalLoading } from '@/store';

const globalLoading = useGlobalLoading()

</script>

<template>
  <div class=" page-full" v-loading="globalLoading.loading">
    <RouterView />
  </div>
</template>
