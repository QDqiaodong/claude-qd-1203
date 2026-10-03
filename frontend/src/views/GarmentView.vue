<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const rows = ref([])
const loading = ref(false)
const drawer = ref(false)
const editing = ref(null)

const categories = ['西装', '裙', '裤', '大衣']
const statuses = ['待改', '修改中', '待取', '已取']

const form = ref({ code: '', customer: '', category: '西装', material: '未注明', status: '待改' })

async function load() {
  loading.value = true
  try {
    const q = routerQueryStatus()
    rows.value = await api.listGarments(q)
  } catch (e) { ElMessage.error(e.message) } finally { loading.value = false }
}

function routerQueryStatus() {
  const s = new URLSearchParams(location.hash.split('?')[1] || '').get('status')
  return s || ''
}

async function inlineUpdate(row, field, value) {
  try {
    await api.updateGarment(row.id, { [field]: value })
    ElMessage.success('已保存')
    load()
  } catch (e) { ElMessage.error(e.message); load() }
}

async function changeStatus(row, value) {
  try {
    await api.changeGarmentStatus(row.id, value)
    ElMessage.success('状态已流转')
    load()
  } catch (e) { ElMessage.error(e.message); load() }
}

function openDrawer(row) {
  editing.value = { ...row }
  drawer.value = true
}

async function saveDrawer() {
  try {
    await api.updateGarment(editing.value.id, {
      customer: editing.value.customer,
      category: editing.value.category,
      material: editing.value.material,
      status: editing.value.status
    })
    ElMessage.success('已保存')
    drawer.value = false
    load()
  } catch (e) { ElMessage.error(e.message) }
}

async function addRow() {
  try {
    await api.createGarment({ ...form.value })
    ElMessage.success('已登记')
    form.value.code = ''
    load()
  } catch (e) { ElMessage.error(e.message) }
}

function emitChanged() { emit('changed') }
const emit = defineEmits(['changed'])

onMounted(load)
</script>

<template>
  <div>
    <div class="bar">
      <span class="title">衣物台账</span>
      <span class="hint">行内编辑 + 抽屉明细 · 状态已取不可回退</span>
    </div>

    <el-card class="add-card" shadow="never">
      <el-input v-model="form.code" placeholder="衣物编号" style="width:140px" />
      <el-input v-model="form.customer" placeholder="客户" style="width:120px" />
      <el-select v-model="form.category" style="width:110px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-input v-model="form.material" placeholder="材质" style="width:110px" />
      <el-button type="primary" @click="addRow">登记衣物</el-button>
    </el-card>

    <el-table :data="rows" v-loading="loading" border stripe @row-dblclick="openDrawer">
      <el-table-column prop="code" label="编号" width="110" />
      <el-table-column prop="customer" label="客户" width="120">
        <template #default="{ row }">
          <el-input v-model="row.customer" size="small" @change="(v) => inlineUpdate(row, 'customer', v)" />
        </template>
      </el-table-column>
      <el-table-column prop="category" label="品类" width="110">
        <template #default="{ row }">
          <el-select v-model="row.category" size="small" @change="(v) => inlineUpdate(row, 'category', v)">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="material" label="材质" width="110">
        <template #default="{ row }">
          <el-input v-model="row.material" size="small" @change="(v) => inlineUpdate(row, 'material', v)" />
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="130">
        <template #default="{ row }">
          <el-select v-model="row.status" size="small" @change="(v) => changeStatus(row, v)">
            <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDrawer(row)">明细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="drawer" title="衣物明细 / 编辑" size="380px">
      <el-form v-if="editing" label-width="64px">
        <el-form-item label="编号"><el-input v-model="editing.code" disabled /></el-form-item>
        <el-form-item label="客户"><el-input v-model="editing.customer" /></el-form-item>
        <el-form-item label="品类">
          <el-select v-model="editing.category">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="材质"><el-input v-model="editing.material" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editing.status">
            <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="saveDrawer">保存</el-button>
      </el-form>
    </el-drawer>
  </div>
</template>

<style scoped>
.bar { display: flex; align-items: baseline; gap: 12px; margin-bottom: 12px; }
.title { font-size: 18px; font-weight: 600; }
.hint { font-size: 12px; color: var(--muted); }
.add-card { margin-bottom: 12px; }
.add-card :deep(.el-card__body) { display: flex; gap: 10px; flex-wrap: wrap; }
</style>
