<template>
  <div class="register-page">
    <NavBar />
    <div class="container">
      <div class="register-form">
        <h2>注册</h2>
        <!-- 添加提示消息区域 -->
        <div v-if="message" class="message-box" :class="messageType">
          {{ message }}
        </div>

        <!-- 添加头像上传区域 -->
        <div class="avatar-upload-container">
          <div class="avatar-preview">
            <img :src="avatarPreview || defaultAvatar" alt="头像预览">
          </div>
          <div class="avatar-upload-btn">
            <label for="avatar-input">选择头像</label>
            <input
              type="file"
              id="avatar-input"
              accept="image/*"
              @change="handleAvatarChange"
              style="display: none;"
            >
          </div>
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
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="confirmPassword"
            placeholder="请再次输入密码"
            class="form-control"
          >
        </div>
        <button class="register-button" @click="handleRegister" :disabled="!isValid">注册</button>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
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
const confirmPassword = ref('')
const message = ref('')
const messageType = ref('')
const defaultAvatar = '/src/assets/images/default-avatar.png'
const avatarPreview = ref('')
const avatarFile = ref(null)

// 验证表单是否有效
const isValid = computed(() => {
  return username.value.trim() !== '' &&
         password.value.trim() !== '' &&
         password.value === confirmPassword.value
})

// 处理头像上传
const handleAvatarChange = (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.match('image.*')) {
    message.value = '请选择图片文件'
    messageType.value = 'error'
    return
  }

  // 验证文件大小（限制为2MB）
  if (file.size > 2 * 1024 * 1024) {
    message.value = '图片大小不能超过2MB'
    messageType.value = 'error'
    return
  }

  // 保存文件并创建预览
  avatarFile.value = file
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target.result
  }
  reader.readAsDataURL(file)
}

// 处理注册
const handleRegister = async () => {
  if (!isValid.value) return

  try {
    message.value = ''

    // 注册用户，包含头像信息
    await userStore.register({
      username: username.value,
      password: password.value,
      avatar: avatarPreview.value,
      registerTime: new Date().toISOString() // 添加注册时间
    })

    message.value = '注册成功！'
    messageType.value = 'success'

    // 延迟跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error) {
    message.value = error.message || '注册失败，请稍后再试'
    messageType.value = 'error'
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.container {
  max-width: 450px;
  margin: 0 auto;
  padding: 40px 20px;
}

.register-form {
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 15px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-control:focus {
  border-color: #1890ff;
  outline: none;
}

.register-button {
  width: 100%;
  padding: 12px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-button:hover {
  background-color: #40a9ff;
}

.register-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #1890ff;
  text-decoration: none;
}

.message-box {
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
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

/* 头像上传样式 */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 8px; /* 从20px减小到10px */
}

.avatar-preview {
  width: 150px;  /* 增加头像尺寸 */
  height: 150px; /* 增加头像尺寸 */
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 15px;
  border: 2px solid #eee;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  transition: all 0.3s ease; /* 添加过渡效果 */
}

.avatar-preview:hover {
  border-color: #1890ff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-upload-btn label {
  display: inline-block;
  padding: 6px 10px; /* 减小按钮内边距 */
  background-color: #f0f0f0;
  color: #333;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 14px; /* 减小字体大小 */
  width: auto; /* 自适应宽度，不再与头像一样宽 */
}

.avatar-upload-btn label:hover {
  background-color: #e0e0e0;
}</style>