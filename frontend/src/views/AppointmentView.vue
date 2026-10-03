<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const rows = ref([])
const loading = ref(false)
const drawer = ref(false)
const editing = ref(null)

const statuses = ['已预约', '已取衣']

const form = ref({ garmentId: '', customer: '', appointTime: '', paid: false })

async function load() {
  loading.value = true
  try {
    rows.value = await api.listAppointments()
  } catch (e) { ElMessage.error(e.message) } finally { loading.value = false }
}

async function inlineUpdate(row, field, value) {
  try {
    const payload = {}
    payload[field] = (field === 'paid') ? Boolean(value) : value
    await api.updateAppointment(row.id, payload)
    ElMessage.success('已保存')
    load()
  } catch (e) { ElMessage.error(e.message); load() }
}

async function pickup(row) {
  try {
    await api.pickupAppointment(row.id)
    ElMessage.success('取衣成功，衣物状态已置为已取')
    load()
  } catch (e) { ElMessage.error(e.message) }
}

function openDrawer(row) {
  editing.value = { ...row }
  drawer.value = true
}

async function saveDrawer() {
  try {
    await api.updateAppointment(editing.value.id, {
      customer: editing.value.customer,
      appointTime: editing.value.appointTime,
      paid: Boolean(editing.value.paid)
    })
    ElMessage.success('已保存')
    drawer.value = false
    load()
  } catch (e) { ElMessage.error(e.message) }
}

async function addRow() {
  try {
    await api.createAppointment({
      garmentId: Number(form.value.garmentId),
      customer: form.value.customer || undefined,
      appointTime: form.value.appointTime || undefined,
      paid: Boolean(form.value.paid)
    })
    ElMessage.success('已预约')
    form.value.garmentId = ''
    load()
  } catch (e) { ElMessage.error(e.message) }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="bar">
      <span class="title">预约与取衣</span>
      <span class="hint">行内编辑 + 抽屉 · 取衣须待取且工单完成 / 须付清</span>
    </div>

    <el-card class="add-card" shadow="never">
      <el-input v-model="form.garmentId" placeholder="衣物id" style="width:110px" />
      <el-input v-model="form.customer" placeholder="客户" style="width:120px" />
      <el-date-picker v-model="form.appointTime" type="date" value-format="YYYY-MM-DD" placeholder="预约时间" style="width:160px" />
      <el-checkbox v-model="form.paid">已付清</el-checkbox>
      <el-button type="primary" @click="addRow">新建预约</el-button>
    </el-card>

    <el-table :data="rows" v-loading="loading" border stripe @row-dblclick="openDrawer">
      <el-table-column prop="garmentId" label="衣物id" width="90" />
      <el-table-column prop="customer" label="客户" width="120">
        <template #default="{ row }">
          <el-input v-model="row.customer" size="small" @change="(v) => inlineUpdate(row, 'customer', v)" />
        </template>
      </el-table-column>
      <el-table-column prop="appointTime" label="预约时间" width="130" />
      <el-table-column prop="pickupTime" label="取衣时间" width="130" />
      <el-table-column prop="paid" label="付清" width="90">
        <template #default="{ row }">
          <el-switch v-model="row.paid" @change="(v) => inlineUpdate(row, 'paid', v)" />
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDrawer(row)">明细</el-button>
          <el-button v-if="row.status !== '已取衣'" link type="success" @click="pickup(row)">取衣</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="drawer" title="预约明细 / 编辑" size="380px">
      <el-form v-if="editing" label-width="72px">
        <el-form-item label="衣物id"><el-input v-model="editing.garmentId" disabled /></el-form-item>
        <el-form-item label="客户"><el-input v-model="editing.customer" /></el-form-item>
        <el-form-item label="预约时间"><el-input v-model="editing.appointTime" disabled /></el-form-item>
        <el-form-item label="取衣时间"><el-input v-model="editing.pickupTime" disabled /></el-form-item>
        <el-form-item label="付清"><el-switch v-model="editing.paid" /></el-form-item>
        <el-form-item label="状态"><el-input v-model="editing.status" disabled /></el-form-item>
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
