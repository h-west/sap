import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Candle from '../views/Candle.vue'
import Ulsearch from '../views/Ulsearch.vue'

Vue.use(VueRouter)

  const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/up',
    name: 'Up',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/Up.vue')
  },
  {
    path: '/candle/:symbol',
    component: Candle, 
    props: true
  },
  {
    path: '/ulsearch',
    component: Ulsearch, 
    props: true
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
