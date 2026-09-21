import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from 'axios'
import ui from '@nuxt/ui/vue-plugin'

import App from './App.vue'
import router from './router'

// Extra imports
// leafleti css kujindused
import 'leaflet/dist/leaflet.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ui) // must come after the router

// Axios globaalselt kättesaadavaks
app.config.globalProperties.$axios = axios

app.mount('#app')
