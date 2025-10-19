// import { createApp } from 'vue';
// import App from './App.vue';
// import router from './router';
// import axios from 'axios';

// // main.js
// import 'bootstrap/dist/css/bootstrap.min.css';
// import 'bootstrap/dist/js/bootstrap.bundle.min.js'; // bundle có cả Popper

// // npm install bootstrap-icons
// axios.defaults.baseURL = 'http://localhost:8081/api';



// const app = createApp(App);
// app.use(router);
// app.config.globalProperties.$axios = axios;
// app.mount('#app');

import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import axios from 'axios';

// Bootstrap CSS + JS bundle (có Popper)
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

// Optional: bootstrap-icons (nếu dùng)
import 'bootstrap-icons/font/bootstrap-icons.css';

axios.defaults.baseURL = 'http://localhost:8081/api';

const app = createApp(App);
app.use(router);

app.config.globalProperties.$axios = axios;
app.mount('#app');