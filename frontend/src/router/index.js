import { createRouter, createWebHashHistory } from 'vue-router'

// 注意：main.js 里没有 app.use(router)，路由实例由 App.vue 直接驱动，
// 所以这里全部用静态 import —— <component :is> 拿到的是组件对象本身。
import GarmentView from '../views/GarmentView.vue'
import AlterationView from '../views/AlterationView.vue'
import FabricView from '../views/FabricView.vue'
import AppointmentView from '../views/AppointmentView.vue'

const routes = [
  { path: '/', redirect: '/garment' },
  { path: '/garment', name: 'garment', component: GarmentView },
  { path: '/alteration', name: 'alteration', component: AlterationView },
  { path: '/fabric', name: 'fabric', component: FabricView },
  { path: '/appointment', name: 'appointment', component: AppointmentView },
  { path: '/:pathMatch(.*)*', redirect: '/garment' }
]

export default createRouter({
  history: createWebHashHistory(),
  routes
})
