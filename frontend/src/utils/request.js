const instance = axios.create({
  // baseURL: 'https://localhost:8081/api',
  // baseURL: 'https://localhost:8081/',
  baseURL: 'https://localhost:8081/',
  timeout: 1000,
  headers: { 'X-Custom-Header': 'foobar' }
});

// 添加请求拦截器
axios.interceptors.request.use(function (config) {
  // 在发送请求之前添加JWT令牌
  const token = localStorage.getItem('jwtToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 添加响应拦截器
axios.interceptors.response.use(function (response) {
  // 处理成功的响应
  console.log('Response:', response.data);
  return response.data;
}, function (error) {
  // 处理错误响应
  if (error.response) {
    console.error('Error:', error.response.status, error.response.data);
  } else {
    console.error('Error:', error.message);
  }
  return Promise.reject(error);
});

