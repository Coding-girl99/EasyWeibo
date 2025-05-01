<template>
  <nav class="navbar">
    <div class="navbar-logo">
      <router-link to="/">简易微博</router-link>
    </div>
    <div class="navbar-menu">
      <router-link to="/" class="navbar-item">首页</router-link>
      <router-link to="/post" class="navbar-item">发布</router-link>
      <router-link to="/messages" class="navbar-item">消息</router-link>
    </div>
    <div class="navbar-user">
      <template v-if="isLoggedIn">
        <div class="user-dropdown">
          <router-link to="/profile" class="navbar-avatar">
            <img :src="userAvatar || defaultAvatar" :alt="userName">
          </router-link>
          <div class="dropdown-menu">
            <router-link to="/profile" class="dropdown-item">个人主页</router-link>
            <div class="dropdown-item" @click="handleLogout">退出登录</div>
          </div>
        </div>
      </template>
      <template v-else>
        <router-link to="/login" class="login-btn">登录</router-link>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userName = computed(() => userStore.currentUser?.username || '用户名')
const userAvatar = computed(() => userStore.currentUser?.avatar || '')
const defaultAvatar = '/src/assets/images/default-avatar.png'

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-logo a {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
  text-decoration: none;
}

.navbar-menu {
  display: flex;
  position: relative;
  justify-content: center; /* 居中显示选项卡 */
}

.navbar-item {
  margin: 0 90px; /* 增大间距，使三个选项卡均匀分布 */
  color: #333;
  text-decoration: none;
  font-size: 18px; /* 增大字体 */
  padding: 10px 5px;
  position: relative;
  transition: color 0.3s;
  font-weight: 500; /* 增加字体粗细 */
}

/* 添加选中效果的动画 */
.navbar-item.router-link-active {
  color: #1890ff;
}

.navbar-item.router-link-active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #1890ff;
  border-radius: 3px;
  animation: slideIn 0.3s ease-in-out;
}

@keyframes slideIn {
  from {
    transform: translateX(-50%);
    opacity: 0;
    width: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
    width: 100%;
  }
}

.user-dropdown {
  position: relative;
}

.navbar-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  width: 120px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  display: none;
  z-index: 10;
}

.user-dropdown:hover .dropdown-menu {
  display: block;
}

.dropdown-item {
  padding: 8px 16px;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.login-btn {
  padding: 6px 16px;
  background-color: #1890ff;
  color: white;
  border-radius: 4px;
  text-decoration: none;
}

.login-btn:hover {
  background-color: #40a9ff;
}
</style>