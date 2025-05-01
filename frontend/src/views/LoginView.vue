<template>
  <div class="login-page">
    <NavBar />
    <div class="container">
      <div class="login-form">
        <h2>登录</h2>
        <!-- 添加提示消息区域 -->
        <div v-if="message" class="message-box" :class="messageType">
          {{ message }}
        </div>
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="username"
            placeholder="请输入用户名"
            class="form-control"
          >
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="password"
            placeholder="请输入密码"
            class="form-control"
          >
        </div>
        <button class="login-button" @click="handleLogin" :disabled="!isValid">登录</button>
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import NavBar from '@/components/common/NavBar.vue'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const message = ref('') // 添加消息状态
const messageType = ref('') // 消息类型：success 或 error

const isValid = computed(() => {
  return username.value.trim() !== '' && password.value.trim() !== ''
})

const handleLogin = async () => {
  if (!isValid.value) return

  try {
    await userStore.login({
      username: username.value,
      password: password.value
    })

    // 登录成功显示成功消息
    message.value = '登录成功！即将跳转到首页...'
    messageType.value = 'success'

    // 延迟跳转，让用户看到成功消息
    setTimeout(() => {
      router.push('/')
    }, 1500)
  } catch (error) {
    console.error('登录失败:', error)
    // 登录失败显示错误消息
    message.value = '用户名或密码错误，请重试'
    messageType.value = 'error'
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.container {
  max-width: 400px;
  margin: 0 auto;
  padding: 40px 20px;
}

.login-form {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  margin-top: 0;
  margin-bottom: 24px;
  text-align: center;
  font-size: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-control:focus {
  border-color: #1890ff;
  outline: none;
}

.login-button {
  width: 100%;
  padding: 12px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 8px;
}

.login-button:hover {
  background-color: #40a9ff;
}

.login-button:disabled {
  background-color: #bfbfbf;
  cursor: not-allowed;
}

.register-link {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

/* 添加消息框样式 */
.message-box {
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 16px;
  text-align: center;
}

.success {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
}

.error {
  background-color: #fff2f0;
  border: 1px solid #ffccc7;
  color: #ff4d4f;
}
</style>