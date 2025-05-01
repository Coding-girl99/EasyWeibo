<template>
  <div class="profile-page">
    <NavBar />
    <div class="container">
      <div class="profile-header">
        <div class="profile-avatar">
          <img :src="userAvatar || defaultAvatar" :alt="userName">
        </div>
        <div class="profile-info">
          <h2 class="profile-name">{{ userName }}</h2>
          <!-- 添加分割线 -->
          <div class="profile-divider"></div>
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-value">{{ userWeibos.length }}</div>
              <div class="stat-label">微博</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ followersCount }}</div>
              <div class="stat-label">粉丝</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ followingCount }}</div>
              <div class="stat-label">关注</div>
            </div>
          </div>
          <!-- 在用户个人资料页面显示注册时间 -->
          <!-- 修改注册时间显示部分 -->
          <div class="user-info-item">
            <div class="info-label">注册时间：</div>
            <div class="info-value">{{ formatRegisterTime }}</div>
          </div>
        </div>
      </div>

      <!-- 添加标签页导航 -->
      <div class="profile-tabs">
        <div
          class="tab-item"
          :class="{ 'active': activeTab === 'weibos' }"
          @click="activeTab = 'weibos'"
        >
          我的微博
        </div>
        <div
          class="tab-item"
          :class="{ 'active': activeTab === 'likes' }"
          @click="activeTab = 'likes'"
        >
          我的点赞
        </div>
        <div
          class="tab-item"
          :class="{ 'active': activeTab === 'settings' }"
          @click="activeTab = 'settings'"
        >
          设置
        </div>
      </div>

      <!-- 添加内容区域 -->
      <div class="profile-content">
        <div v-if="activeTab === 'weibos'">
          <div v-if="userWeibos.length === 0" class="empty-state">
            你还没有发布过微博
          </div>
          <WeiboItem
            v-for="weibo in userWeibos"
            :key="weibo.id"
            :weibo="weibo"
          />
        </div>
        <div v-else-if="activeTab === 'likes'">
          <div v-if="likedWeibos.length === 0" class="empty-state">
            你还没有点赞过微博
          </div>
          <WeiboItem
            v-for="weibo in likedWeibos"
            :key="weibo.id"
            :weibo="weibo"
          />
        </div>
        <!-- 添加设置内容区域 -->
        <div v-else-if="activeTab === 'settings'">
          <div class="settings-container">
            <h3 class="settings-title">个人资料设置</h3>

            <div class="settings-section">
              <div class="settings-avatar">
                <img :src="userAvatar || defaultAvatar" :alt="userName">
                <div class="avatar-upload">
                  <label for="avatar-input" class="upload-btn">更换头像</label>
                  <input
                    type="file"
                    id="avatar-input"
                    accept="image/*"
                    @change="handleAvatarChange"
                    class="hidden-input"
                  >
                </div>
              </div>

              <div class="settings-form">
                <div class="form-group">
                  <label>昵称</label>
                  <input type="text" v-model="userNickname" placeholder="请输入昵称">
                </div>

                <div class="form-group">
                  <label>个性签名</label>
                  <textarea v-model="userBio" placeholder="请输入个性签名"></textarea>
                </div>

                <div class="form-group">
                  <label>修改密码</label>
                  <input type="password" v-model="oldPassword" placeholder="请输入原密码">
                  <input type="password" v-model="newPassword" placeholder="请输入新密码">
                  <input type="password" v-model="confirmPassword" placeholder="请确认新密码">
                </div>

                <div class="form-actions">
                  <button class="save-btn" @click="saveSettings">保存修改</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useWeiboStore } from '@/stores/weibo'
import { useUserStore } from '@/stores/user'
import NavBar from '@/components/common/NavBar.vue'
import WeiboItem from '@/components/common/WeiboItem.vue'

const weiboStore = useWeiboStore()
const userStore = useUserStore()

const activeTab = ref('weibos')
const defaultAvatar = '/src/assets/images/default-avatar.png'

const userName = computed(() => userStore.currentUser?.username || '用户名')
const userAvatar = computed(() => userStore.currentUser?.avatar || '')
const followersCount = computed(() => 0) // 模拟数据
const followingCount = computed(() => 0) // 模拟数据

// 获取用户发布的微博
const userWeibos = computed(() => {
  return weiboStore.getAllWeibos.filter(weibo =>
    weibo.author === userName.value
  )
})

// 获取用户点赞的微博
const likedWeibos = computed(() => {
  return weiboStore.getAllWeibos.filter(weibo =>
    weibo.isLiked
  )
})

// 设置相关的状态
const userNickname = ref(userName.value)
const userBio = ref('')
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const avatarFile = ref(null)

// 处理头像上传
const handleAvatarChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    avatarFile.value = file
    // 创建预览
    const reader = new FileReader()
    reader.onload = (e) => {
      // 更新头像预览
      userStore.updateUserInfo({
        avatar: e.target.result
      })
    }
    reader.readAsDataURL(file)
  }
}

// 日期格式化函数
const formatDate = (dateString) => {
  if (!dateString || dateString === '未知') return '未知';

  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return '未知';

    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  } catch (error) {
    console.error('日期格式化错误:', error);
    return '未知';
  }
}

// 保存设置
const saveSettings = () => {
  // 验证密码
  if (newPassword.value && newPassword.value !== confirmPassword.value) {
    alert('两次输入的密码不一致')
    return
  }

  // 更新用户信息
  const updateData = {
    username: userNickname.value
  }

  if (userBio.value) {
    updateData.bio = userBio.value
  }

  // 如果有新密码，则更新密码
  if (newPassword.value && oldPassword.value) {
    // 在实际应用中，这里应该先验证旧密码是否正确
    updateData.password = newPassword.value
  }

  // 更新用户信息
  const success = userStore.updateUserInfo(updateData)

  if (success) {
    alert('设置已保存')
    // 清空密码字段
    oldPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } else {
    alert('保存失败，请重试')
  }
}

// 修改注册时间计算属性
const formatRegisterTime = computed(() => {
  if (userStore.currentUser && userStore.currentUser.registerTime) {
    return formatDate(userStore.currentUser.registerTime);
  } else {
    // 如果没有注册时间，设置一个默认时间
    const defaultTime = new Date().toISOString();
    // 尝试更新用户信息，添加注册时间
    userStore.updateUserInfo({
      registerTime: defaultTime
    });
    return formatDate(defaultTime);
  }
});
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  display: flex;
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.profile-avatar {
  margin-right: 24px;
}

.profile-avatar img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #eee;
}

.profile-info {
  flex: 1;
}

.profile-name {
  margin-top: 0;
  margin-bottom: 16px;
  font-size: 24px;
  color: #333;
}

/* 添加分割线样式 */
.profile-divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 10px 0 16px 0;
  width: 100%;
}

.profile-stats {
  display: flex;
}

.stat-item {
  margin-right: 24px;
  text-align: center;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.profile-tabs {
  display: flex;
  background-color: #fff;
  border-radius: 8px 8px 0 0;  /* 修改这里：只保留上方圆角 */
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 0;  /* 修改这里：移除底部外边距 */
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 16px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
  position: relative; /* 添加相对定位 */
}

.tab-item.active {
  color: #1890ff;
}

/* 添加新的伪元素来创建下划线 */
.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%); /* 居中显示 */
  width: 30%; /* 控制下划线宽度，可以根据需要调整 */
  height: 2px;
  background-color: #1890ff;
}

.profile-content {
  background-color: #fff;  /* 添加这里：设置内容区背景色 */
  border-radius: 0 0 8px 8px;  /* 添加这里：设置下方圆角 */
  padding: 20px;  /* 添加这里：增加内边距 */
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);  /* 添加这里：保持阴影一致 */
  margin-bottom: 20px;  /* 添加这里：底部外边距 */
}

.empty-state {
  background-color: transparent;  /* 修改这里：移除背景色 */
  padding: 40px;
  text-align: center;
  color: #999;
  border-radius: 0;  /* 修改这里：移除圆角 */
  margin-bottom: 0;  /* 修改这里：移除底部外边距 */
}

/* 设置页面样式 */
.settings-container {
  padding: 20px 0;
}

.settings-title {
  margin-top: 0;
  margin-bottom: 24px;
  font-size: 18px;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12px;
}

.settings-section {
  display: flex;
  flex-direction: column;
  align-items: center; /* 居中显示 */
}

.settings-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.settings-avatar img {
  width: 150px; /* 增大头像尺寸，原来是100px */
  height: 150px; /* 增大头像尺寸，原来是100px */
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #eee;
  margin-bottom: 12px;
}

.avatar-upload {
  margin-top: 8px;
}

.upload-btn {
  display: inline-block;
  padding: 4px 12px; /* 减小按钮内边距 */
  background-color: #1890ff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 12px; /* 减小字体大小 */
  width: 80px; /* 设置固定宽度，小于头像宽度 */
  text-align: center;
}

.upload-btn:hover {
  background-color: #40a9ff;
}

.hidden-input {
  display: none;
}

.settings-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center; /* 居中显示表单 */
}

.form-group {
  margin-bottom: 20px;
  width: 80%; /* 将输入框宽度设为原来的1/3 */
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-group textarea {
  min-height: 80px;
  resize: vertical;
}

.form-actions {
  margin-top: 24px;
  width: 33%; /* 与表单宽度一致 */
  display: flex;
  justify-content: center; /* 居中显示按钮 */
}

.save-btn {
  padding: 8px 24px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.save-btn:hover {
  background-color: #40a9ff;
}

/* 添加用户信息项样式 */
.user-info-item {
  display: flex;
  align-items: center;
  margin-top: 12px;
  font-size: 14px;
}

.info-label {
  color: #666;
  margin-right: 8px;
}

.info-value {
  color: #333;
}
</style>