<template>
  <router-view />
  <ai-assistant v-if="showAssistant" />
</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'
import { handleThemeStyle } from '@/utils/theme'
import AiAssistant from '@/components/AiAssistant/index.vue'

const route = useRoute()
const showAssistant = computed(() => !['/login', '/register', '/lock', '/401', '/404'].includes(route.path))

onMounted(() => {
  nextTick(() => {
    // 初始化主题样式
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
