<template>
  <teleport to="body">
    <!-- 引导中：聚光遮罩 + 步骤气泡（遮罩不拦截点击，用户可正常操作页面） -->
    <div v-if="guide.active" class="ai-guide" @keydown.esc="stopGuide">
      <div v-for="(s, i) in shades" :key="i" class="ai-guide-shade" :style="s" />
      <div v-if="guide.rect" class="ai-guide-ring" :style="ringStyle" />
      <div class="ai-guide-tip" :style="tipStyle">
        <div class="tip-head">
          <span class="tip-count">步骤 {{ guide.index + 1 }} / {{ guide.steps.length }}</span>
          <el-icon class="tip-close" @click="stopGuide"><Close /></el-icon>
        </div>
        <div class="tip-title">{{ currentStep && currentStep.title }}</div>
        <div class="tip-body">{{ currentStep && currentStep.content }}</div>
        <div v-if="guide.notFound && !guide.resolving" class="tip-warn">未在页面上找到「{{ currentStep && currentStep.target }}」，可跳过此步骤</div>
        <div v-if="currentStep && currentStep.type === 'menu'" class="tip-warn">菜单已定位，稍候自动进入…</div>
        <div class="tip-foot">
          <el-button size="small" text :disabled="guide.index === 0" @click="prevStep">上一步</el-button>
          <el-button size="small" text @click="stopGuide">结束</el-button>
          <el-button size="small" type="primary" @click="goNext">{{ isLast ? '完成' : '下一步' }}</el-button>
        </div>
      </div>
    </div>

    <!-- 引导完成卡片 -->
    <div v-else-if="guide.done" class="ai-guide ai-guide-dim">
      <div class="ai-guide-done">
        <el-icon :size="40" color="#67c23a"><CircleCheckFilled /></el-icon>
        <div class="done-title">引导完成</div>
        <div class="done-body">已为您演示完整操作路径，可以开始亲自尝试了</div>
        <el-button type="primary" @click="closeDone">完成</el-button>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { guide, goNext, prevStep, stopGuide, closeDone } from './guide'

const TIP_W = 320
const TIP_H = 190

const currentStep = computed(() => guide.steps[guide.index])
const isLast = computed(() => guide.index >= guide.steps.length - 1)

const shades = computed(() => {
  const w = window.innerWidth
  const h = window.innerHeight
  const r = guide.rect
  if (!r) {
    return [{ top: '0px', left: '0px', width: w + 'px', height: h + 'px' }]
  }
  return [
    { top: '0px', left: '0px', width: w + 'px', height: r.top + 'px' },
    { top: r.top + r.height + 'px', left: '0px', width: w + 'px', height: Math.max(0, h - r.top - r.height) + 'px' },
    { top: r.top + 'px', left: '0px', width: r.left + 'px', height: r.height + 'px' },
    { top: r.top + 'px', left: r.left + r.width + 'px', width: Math.max(0, w - r.left - r.width) + 'px', height: r.height + 'px' }
  ]
})

const ringStyle = computed(() => {
  const r = guide.rect
  return r ? { top: r.top + 'px', left: r.left + 'px', width: r.width + 'px', height: r.height + 'px' } : {}
})

const tipStyle = computed(() => {
  const w = window.innerWidth
  const h = window.innerHeight
  const r = guide.rect
  if (!r) {
    return { top: Math.max(80, h / 2 - TIP_H / 2) + 'px', left: w / 2 - TIP_W / 2 + 'px', width: TIP_W + 'px' }
  }
  let top = r.top + r.height + 14
  if (top + TIP_H > h - 12) {
    top = Math.max(12, r.top - TIP_H - 14)
  }
  const left = Math.min(Math.max(12, r.left + r.width / 2 - TIP_W / 2), w - TIP_W - 12)
  return { top: top + 'px', left: left + 'px', width: TIP_W + 'px' }
})

function onKeydown(e) {
  if (!guide.active) return
  if (e.key === 'Escape') stopGuide()
  else if (e.key === 'ArrowRight' || e.key === 'Enter') goNext()
  else if (e.key === 'ArrowLeft') prevStep()
}

onMounted(() => document.addEventListener('keydown', onKeydown))
onBeforeUnmount(() => document.removeEventListener('keydown', onKeydown))
</script>

<style scoped>
.ai-guide {
  position: fixed;
  inset: 0;
  z-index: 4000;
  pointer-events: none;
}

.ai-guide-shade {
  position: absolute;
  background: rgba(0, 0, 0, 0.55);
  transition: all 0.25s ease;
}

.ai-guide-ring {
  position: absolute;
  border: 2px solid var(--current-color, #409eff);
  border-radius: 6px;
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.35), 0 0 18px rgba(64, 158, 255, 0.55);
  transition: all 0.25s ease;
  animation: ai-ring-pulse 1.6s ease-in-out infinite;
}

@keyframes ai-ring-pulse {
  0%, 100% { box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.35), 0 0 18px rgba(64, 158, 255, 0.55); }
  50% { box-shadow: 0 0 0 7px rgba(64, 158, 255, 0.2), 0 0 26px rgba(64, 158, 255, 0.8); }
}

.ai-guide-tip {
  position: absolute;
  pointer-events: auto;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.28);
  padding: 14px 16px 12px;
  transition: top 0.25s ease, left 0.25s ease;
}

.tip-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.tip-count {
  font-size: 12px;
  color: #909399;
}

.tip-close {
  cursor: pointer;
  color: #909399;
}
.tip-close:hover { color: #303133; }

.tip-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.tip-body {
  font-size: 13px;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
}

.tip-warn {
  margin-top: 6px;
  font-size: 12px;
  color: #e6a23c;
}

.tip-foot {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 4px;
}

.ai-guide-dim {
  background: rgba(0, 0, 0, 0.55);
  pointer-events: auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-guide-done {
  background: #fff;
  border-radius: 12px;
  padding: 28px 36px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.28);
}

.done-title {
  font-size: 18px;
  font-weight: 600;
  margin: 10px 0 6px;
}

.done-body {
  font-size: 13px;
  color: #606266;
  margin-bottom: 16px;
}
</style>
