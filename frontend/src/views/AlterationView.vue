<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const rows = ref([])
const loading = ref(false)
const drawer = ref(false)
const editing = ref(null)
const filterGarment = ref('')

const items = ['长短', '宽窄', '换拉链', '换扣']
const statuses = ['待开工', '已完成']

const form = ref({ garmentId: '', item: '长短', price: 50 })

async function load() {
  loading.value = true
  try {
    const g = filterGarment.value ? Number(filterGarment.value) : null
    rows.value = await api.listAlterations(g)
  } catch (e) { ElMessage.error(e.message) } finally { loading.value = false }
}

async function inlineUpdate(row, field, value) {
  try {
    const payload = {}
    payload[field] = (field === 'price') ? Number(value) : value
    await api.updateAlteration(row.id, payload)
    ElMessage.success('已保存')
    load()
  } catch (e) { ElMessage.error(e.message); load() }
}

async function complete(row) {
  try {
    await api.completeAlteration(row.id)
    ElMessage.success('工单已完成')
    load()
  } catch (e) { ElMessage.error(e.message) }
}

function openDrawer(row) {
  editing.value = { ...row }
  drawer.value = true
}

async function saveDrawer() {
  try {
    await api.updateAlteration(editing.value.id, {
      item: editing.value.item,
      price: Number(editing.value.price),
      status: editing.value.status
    })
    ElMessage.success('已保存')
    drawer.value = false
    load()
  } catch (e) { ElMessage.error(e.message) }
}

async function addRow() {
  try {
    await api.createAlteration({
      garmentId: Number(form.value.garmentId),
      item: form.value.item,
      price: Number(form.value.price)
    })
    ElMessage.success('已建档')
    form.value.garmentId = ''
    load()
  } catch (e) { ElMessage.error(e.message) }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="bar">
      <span class="title">改衣工单</span>
      <span class="hint">行内编辑 + 抽屉 · 须关联衣物 / 报价&gt;0 / 未填项目禁完成</span>
    </div>

    <el-card class="add-card" shadow="never">
      <el-input v-model="form.garmentId" placeholder="衣物id" style="width:110px" />
      <el-select v-model="form.item" style="width:110px">
        <el-option v-for="c in items" :key="c" :label="c" :value="c" />
      </el-select>
      <el-input v-model="form.price" type="number" placeholder="报价" style="width:110px" />
      <el-button type="primary" @click="addRow">新建工单</el-button>
      <el-input v-model="filterGarment" placeholder="按衣物id筛选" style="width:140px" @change="load" />
    </el-card>

    <el-table :data="rows" v-loading="loading" border stripe @row-dblclick="openDrawer">
      <el-table-column prop="garmentId" label="衣物id" width="90" />
      <el-table-column prop="item" label="项目" width="120">
        <template #default="{ row }">
          <el-select v-model="row.item" size="small" @change="(v) => inlineUpdate(row, 'item', v)">
            <el-option v-for="c in items" :key="c" :label="c" :value="c" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="报价" width="110">
        <template #default="{ row }">
          <el-input v-model="row.price" size="small" type="number" @change="(v) => inlineUpdate(row, 'price', v)" />
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDrawer(row)">明细</el-button>
          <el-button v-if="row.status !== '已完成'" link type="success" @click="complete(row)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="drawer" title="工单明细 / 编辑" size="380px">
      <el-form v-if="editing" label-width="64px">
        <el-form-item label="衣物id"><el-input v-model="editing.garmentId" disabled /></el-form-item>
        <el-form-item label="项目">
          <el-select v-model="editing.item">
            <el-option v-for="c in items" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="报价"><el-input v-model="editing.price" type="number" /></el-form-item>
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
.add-card :deep(.el-card__body) { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
</style>
