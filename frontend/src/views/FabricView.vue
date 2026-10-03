<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const rows = ref([])
const loading = ref(false)
const drawer = ref(false)
const editing = ref(null)

const categories = ['里布', '衬布', '拉链', '纽扣', '花边', '配线']

const form = ref({ name: '', category: '配线', stock: 0, unit: '件', threshold: 0 })

async function load() {
  loading.value = true
  try {
    const q = routerCat()
    rows.value = await api.listFabrics(q)
  } catch (e) { ElMessage.error(e.message) } finally { loading.value = false }
}

function routerCat() {
  const c = new URLSearchParams(location.hash.split('?')[1] || '').get('category')
  return c || ''
}

async function inlineUpdate(row, field, value) {
  try {
    const payload = {}
    payload[field] = (field === 'stock' || field === 'threshold') ? Number(value) : value
    await api.updateFabric(row.id, payload)
    ElMessage.success('已保存')
    load()
  } catch (e) { ElMessage.error(e.message); load() }
}

async function consume(row) {
  const v = window.prompt('本次领用数量', '1')
  if (v == null) return
  try {
    await api.consumeFabric(row.id, Number(v))
    ElMessage.success('领用成功，库存已扣减')
    load()
  } catch (e) { ElMessage.error(e.message) }
}

function openDrawer(row) {
  editing.value = { ...row }
  drawer.value = true
}

async function saveDrawer() {
  try {
    await api.updateFabric(editing.value.id, {
      name: editing.value.name,
      category: editing.value.category,
      stock: Number(editing.value.stock),
      unit: editing.value.unit,
      threshold: Number(editing.value.threshold)
    })
    ElMessage.success('已保存')
    drawer.value = false
    load()
  } catch (e) { ElMessage.error(e.message) }
}

async function addRow() {
  try {
    await api.createFabric({ ...form.value, stock: Number(form.value.stock), threshold: Number(form.value.threshold) })
    ElMessage.success('已入库')
    form.value.name = ''
    load()
  } catch (e) { ElMessage.error(e.message) }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="bar">
      <span class="title">面料库存</span>
      <span class="hint">行内编辑 + 抽屉 · 库存为负 / 低于下限拦截领用</span>
    </div>

    <el-card class="add-card" shadow="never">
      <el-input v-model="form.name" placeholder="面料名" style="width:140px" />
      <el-select v-model="form.category" style="width:110px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-input v-model="form.stock" type="number" placeholder="库存" style="width:100px" />
      <el-input v-model="form.unit" placeholder="单位" style="width:90px" />
      <el-input v-model="form.threshold" type="number" placeholder="下限" style="width:90px" />
      <el-button type="primary" @click="addRow">入库</el-button>
    </el-card>

    <el-table :data="rows" v-loading="loading" border stripe @row-dblclick="openDrawer">
      <el-table-column prop="name" label="面料名" min-width="140" />
      <el-table-column prop="category" label="类别" width="100">
        <template #default="{ row }">
          <el-select v-model="row.category" size="small" @change="(v) => inlineUpdate(row, 'category', v)">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100">
        <template #default="{ row }">
          <el-input v-model="row.stock" size="small" type="number" @change="(v) => inlineUpdate(row, 'stock', v)" />
        </template>
      </el-table-column>
      <el-table-column prop="unit" label="单位" width="90">
        <template #default="{ row }">
          <el-input v-model="row.unit" size="small" @change="(v) => inlineUpdate(row, 'unit', v)" />
        </template>
      </el-table-column>
      <el-table-column prop="threshold" label="下限" width="90">
        <template #default="{ row }">
          <el-input v-model="row.threshold" size="small" type="number" @change="(v) => inlineUpdate(row, 'threshold', v)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDrawer(row)">明细</el-button>
          <el-button link type="warning" @click="consume(row)">领用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="drawer" title="面料明细 / 编辑" size="380px">
      <el-form v-if="editing" label-width="64px">
        <el-form-item label="面料名"><el-input v-model="editing.name" /></el-form-item>
        <el-form-item label="类别">
          <el-select v-model="editing.category">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存"><el-input v-model="editing.stock" type="number" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="editing.unit" /></el-form-item>
        <el-form-item label="下限"><el-input v-model="editing.threshold" type="number" /></el-form-item>
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
.add-card :deep(.el-card__body) { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
</style>
