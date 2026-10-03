<script setup>
import { computed, onMounted, ref } from 'vue'
import router from './router'
import api from './api'

import GarmentView from './views/GarmentView.vue'
import AlterationView from './views/AlterationView.vue'
import FabricView from './views/FabricView.vue'
import AppointmentView from './views/AppointmentView.vue'

// ------------------------------------------------------------------
// 导航形态：左侧树形（按模块 + 状态/类别分层的 el-menu）。
// 四个业务模块各为一级树节点，其下挂状态/类别二级节点；
// 点击模块根进入「全部」，点击二级节点带过滤条件进入。
// 页面操作范式：行内编辑 + 抽屉（列表里直接改字段、点开抽屉看明细）。
// ------------------------------------------------------------------

const modules = [
  {
    path: '/garment', label: '衣物台账', comp: GarmentView,
    children: [
      { label: '待改', q: { status: '待改' } },
      { label: '修改中', q: { status: '修改中' } },
      { label: '待取', q: { status: '待取' } },
      { label: '已取', q: { status: '已取' } }
    ]
  },
  {
    path: '/alteration', label: '改衣工单', comp: AlterationView,
    children: [
      { label: '待开工', q: { status: '待开工' } },
      { label: '已完成', q: { status: '已完成' } }
    ]
  },
  {
    path: '/fabric', label: '面料库存', comp: FabricView,
    children: [
      { label: '里布', q: { category: '里布' } },
      { label: '衬布', q: { category: '衬布' } },
      { label: '拉链', q: { category: '拉链' } },
      { label: '纽扣', q: { category: '纽扣' } },
      { label: '花边', q: { category: '花边' } },
      { label: '配线', q: { category: '配线' } }
    ]
  },
  {
    path: '/appointment', label: '预约与取衣', comp: AppointmentView,
    children: [
      { label: '已预约', q: { status: '已预约' } },
      { label: '已取衣', q: { status: '已取衣' } }
    ]
  }
]

const currentPath = ref(router.currentRoute.value.path)
const counts = ref({})

const currentView = computed(() => {
  const m = modules.find((mm) => mm.path === currentPath.value)
  return m ? m.comp : GarmentView
})

function indexOfModule(m) { return m.path }

function onSelect(index) {
  // index 形如 "/garment" 或 "/garment|待改|status=待改"
  const [path, , query] = index.split('|')
  const q = {}
  if (query) {
    const [k, v] = query.split('=')
    q[k] = v
  }
  router.push({ path, query: q })
}

async function loadCounts() {
  try {
    const [g, a, f, p] = await Promise.all([
      api.listGarments(), api.listAlterations(), api.listFabrics(), api.listAppointments()
    ])
    counts.value = { '/garment': g.length, '/alteration': a.length, '/fabric': f.length, '/appointment': p.length }
  } catch (e) { /* 计数失败不影响主流程 */ }
}

function isActive(m) { return currentPath.value === m.path }

router.afterEach((to) => { currentPath.value = to.path })

onMounted(async () => {
  currentPath.value = router.currentRoute.value.path
  await loadCounts()
})
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">衣</span>
        <div class="brand-text">
          <div class="brand-title">裁缝改衣店</div>
          <div class="brand-sub">藕荷主题 · 左侧树形导航</div>
        </div>
      </div>
      <el-menu
        class="nav-tree"
        :default-active="currentPath"
        @select="onSelect"
      >
        <el-sub-menu v-for="m in modules" :key="m.path" :index="m.path">
          <template #title>
            <span class="mod-title">{{ m.label }}（{{ counts[m.path] ?? 0 }}）</span>
          </template>
          <el-menu-item :index="m.path + '|all|'">全部</el-menu-item>
          <el-menu-item
            v-for="c in m.children"
            :key="c.label"
            :index="`${m.path}|${c.label}|${Object.keys(c.q)[0]}=${Object.values(c.q)[0]}`"
          >{{ c.label }}</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <main class="content">
      <component :is="currentView" @changed="loadCounts" />
    </main>
  </div>
</template>

<style scoped>
.app-shell { min-height: 100vh; display: flex; }

.sidebar {
  width: 240px; flex-shrink: 0;
  background: #fff; border-right: 1px solid var(--line);
  display: flex; flex-direction: column;
}
.brand {
  display: flex; align-items: center; gap: 10px;
  padding: 16px 18px; height: 64px;
  background: linear-gradient(120deg, var(--primary-dark-2), var(--primary));
  color: #fff;
}
.brand-mark {
  width: 34px; height: 34px; border-radius: 9px;
  display: flex; align-items: center; justify-content: center;
  background: #fff; color: var(--primary-dark-2); font-weight: 700; font-size: 18px;
}
.brand-title { font-size: 16px; font-weight: 600; }
.brand-sub { font-size: 11px; opacity: 0.85; }

.nav-tree { flex: 1; border-right: none; }
.mod-title { font-weight: 500; }

.content { flex: 1; padding: 18px 22px 28px; overflow: auto; }
</style>
