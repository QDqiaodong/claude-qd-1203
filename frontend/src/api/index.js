import axios from 'axios'

const http = axios.create({ baseURL: '/api', timeout: 20000 })

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error?.response?.data?.message || error?.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

async function call(promise) {
  const response = await promise
  const body = response.data
  if (!body || body.ok === false) {
    throw new Error((body && body.message) || '请求失败')
  }
  return body.data
}

const params = (o) => ({ params: o })

export default {
  // ---------------- 衣物台账 ----------------
  listGarments: (status) => call(http.get('/garments', params(status ? { status } : {}))),
  createGarment: (d) => call(http.post('/garments', d)),
  updateGarment: (id, d) => call(http.put(`/garments/${id}`, d)),
  changeGarmentStatus: (id, status) => call(http.post(`/garments/${id}/status`, null, { params: { status } })),
  deleteGarment: (id) => call(http.delete(`/garments/${id}`)),

  // ---------------- 改衣工单 ----------------
  listAlterations: (garmentId) => call(http.get('/alterations', params(garmentId ? { garmentId } : {}))),
  createAlteration: (d) => call(http.post('/alterations', d)),
  updateAlteration: (id, d) => call(http.put(`/alterations/${id}`, d)),
  completeAlteration: (id) => call(http.post(`/alterations/${id}/complete`)),
  deleteAlteration: (id) => call(http.delete(`/alterations/${id}`)),

  // ---------------- 面料库存 ----------------
  listFabrics: (category) => call(http.get('/fabrics', params(category ? { category } : {}))),
  createFabric: (d) => call(http.post('/fabrics', d)),
  updateFabric: (id, d) => call(http.put(`/fabrics/${id}`, d)),
  consumeFabric: (id, qty) => call(http.post(`/fabrics/${id}/consume`, null, { params: { qty } })),
  deleteFabric: (id) => call(http.delete(`/fabrics/${id}`)),

  // ---------------- 预约与取衣 ----------------
  listAppointments: () => call(http.get('/appointments')),
  createAppointment: (d) => call(http.post('/appointments', d)),
  updateAppointment: (id, d) => call(http.put(`/appointments/${id}`, d)),
  pickupAppointment: (id) => call(http.post(`/appointments/${id}/pickup`)),
  deleteAppointment: (id) => call(http.delete(`/appointments/${id}`))
}
