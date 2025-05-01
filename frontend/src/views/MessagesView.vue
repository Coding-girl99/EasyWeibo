<template>
  <div class="messages-page">
    <NavBar />
    <div class="container">
      <div class="messages-header">
        <h2>消息中心</h2>
      </div>

      <div class="messages-content">
        <div class="messages-sidebar">
          <div
            class="message-type"
            :class="{ 'active': activeType === 'comment' }"
            @click="setActiveType('comment')"
          >
            <span class="type-icon">💬</span>
            <span class="type-text">评论</span>
            <span class="type-badge" v-if="commentCount > 0">{{ commentCount }}</span>
          </div>
          <div
            class="message-type"
            :class="{ 'active': activeType === 'like' }"
            @click="setActiveType('like')"
          >
            <span class="type-icon">❤️</span>
            <span class="type-text">点赞</span>
            <span class="type-badge" v-if="likeCount > 0">{{ likeCount }}</span>
          </div>
          <div
            class="message-type"
            :class="{ 'active': activeType === 'notification' }"
            @click="setActiveType('notification')"
          >
            <span class="type-icon">🔔</span>
            <span class="type-text">通知</span>
            <span class="type-badge" v-if="notificationCount > 0">{{ notificationCount }}</span>
          </div>
          <div
            class="message-type"
            :class="{ 'active': activeType === 'follower' }"
            @click="setActiveType('follower')"
          >
            <span class="type-icon">👥</span>
            <span class="type-text">新粉丝</span>
            <span class="type-badge" v-if="followerCount > 0">{{ followerCount }}</span>
          </div>
        </div>

        <div class="messages-list">
          <div v-if="filteredMessages.length === 0" class="no-messages">
            暂无新消息
          </div>
          <div v-else class="message-items">
            <div v-for="(message, index) in filteredMessages" :key="index" class="message-item">
              <div class="message-avatar">
                <img :src="message.avatar || defaultAvatar" :alt="message.username">
              </div>
              <div class="message-content">
                <div class="message-info">
                  <span class="message-username">{{ message.username }}</span>
                  <span class="message-action">{{ message.action }}</span>
                  <span class="message-target">{{ message.target }}</span>
                </div>
                <div class="message-text" v-if="message.text">{{ message.text }}</div>
                <div class="message-time">{{ message.time }}</div>
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
import NavBar from '@/components/common/NavBar.vue'

const defaultAvatar = '/src/assets/images/default-avatar.png'

// 当前激活的消息类型
const activeType = ref('comment') // 默认显示评论消息

// 设置当前激活的消息类型
const setActiveType = (type) => {
  activeType.value = type
}

// 消息计数
const commentCount = ref(3)
const likeCount = ref(5)
const notificationCount = ref(1)
const followerCount = ref(2)

// 模拟消息数据
const allMessages = ref([
  {
    type: 'comment',
    username: '用户A',
    avatar: '',
    action: '评论了你的微博',
    target: '《今天天气真好》',
    text: '确实很好，我也出去玩了！',
    time: '10分钟前'
  },
  {
    type: 'comment',
    username: '用户C',
    avatar: '',
    action: '评论了你的微博',
    target: '《分享一张照片》',
    text: '拍得真好看！',
    time: '1小时前'
  },
  {
    type: 'comment',
    username: '用户D',
    avatar: '',
    action: '回复了你的评论',
    target: '',
    text: '谢谢你的建议，我会考虑的！',
    time: '2小时前'
  },
  {
    type: 'like',
    username: '用户B',
    avatar: '',
    action: '点赞了你的微博',
    target: '《周末计划》',
    text: '',
    time: '30分钟前'
  },
  {
    type: 'like',
    username: '用户E',
    avatar: '',
    action: '点赞了你的微博',
    target: '《新买的相机》',
    text: '',
    time: '1天前'
  },
  {
    type: 'like',
    username: '用户F',
    avatar: '',
    action: '点赞了你的评论',
    target: '',
    text: '我也觉得这个想法很棒！',
    time: '2天前'
  },
  {
    type: 'like',
    username: '用户G',
    avatar: '',
    action: '点赞了你的微博',
    target: '《学习笔记》',
    text: '',
    time: '3天前'
  },
  {
    type: 'like',
    username: '用户H',
    avatar: '',
    action: '点赞了你的微博',
    target: '《旅行照片》',
    text: '',
    time: '4天前'
  },
  {
    type: 'notification',
    username: '系统通知',
    avatar: '',
    action: '你的账号已完成实名认证',
    target: '',
    text: '现在你可以使用更多功能了！',
    time: '1天前'
  },
  {
    type: 'follower',
    username: '用户J',
    avatar: '',
    action: '关注了你',
    target: '',
    text: '',
    time: '2小时前'
  },
  {
    type: 'follower',
    username: '用户K',
    avatar: '',
    action: '关注了你',
    target: '',
    text: '',
    time: '1天前'
  }
])

// 根据当前选中的类型过滤消息
const filteredMessages = computed(() => {
  return allMessages.value.filter(message => message.type === activeType.value)
})
</script>

<style scoped>
.messages-page {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.messages-header {
  margin-bottom: 20px;
}

.messages-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
  padding: 10px 0;
}

.messages-content {
  display: flex;
  gap: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 左侧导航栏样式优化 */
.messages-sidebar {
  min-width: 180px;
  background-color: #f9f9f9;
  padding: 16px 0;
  border-right: 1px solid #eee;
  flex-shrink: 0; /* 防止被压缩 */
}

.message-type {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  margin-bottom: 4px;
  position: relative;
  white-space: nowrap; /* 防止文字换行 */
}

.message-type:hover {
  background-color: #f0f0f0;
}

.message-type.active {
  background-color: #e6f7ff;
  border-left: 3px solid #1890ff;
  color: #1890ff;
}

.type-icon {
  margin-right: 10px;
  font-size: 18px;
}

.type-text {
  flex: 1;
}

.type-badge {
  background-color: #ff4d4f;
  color: white;
  border-radius: 50%; /* 改为50%使其完全圆形 */
  padding: 0; /* 移除内边距 */
  font-size: 12px;
  min-width: 20px; /* 固定宽度 */
  height: 20px; /* 固定高度，与宽度相同确保是圆形 */
  line-height: 20px; /* 行高与高度相同，确保文字垂直居中 */
  text-align: center;
  margin-left: 5px; /* 减小与左侧文字的距离 */
  display: inline-block; /* 确保元素是块级 */
}

/* 消息列表样式优化 */
.messages-list {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  max-height: 80vh;
}

.no-messages {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}

.message-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  padding: 12px;
  border-radius: 8px;
  background-color: #fff;
  transition: background-color 0.3s;
  border-bottom: 1px solid #f0f0f0;
}

.message-item:hover {
  background-color: #f9f9f9;
}

.message-item:last-child {
  border-bottom: none;
}

/* 头像样式优化 - 圆形且更小 */
.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
  border: 1px solid #eee;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.message-username {
  font-weight: 600;
  color: #333;
  margin-right: 4px;
}

.message-action {
  color: #666;
}

.message-target {
  color: #1890ff;
}

.message-text {
  color: #333;
  margin: 4px 0;
  line-height: 1.5;
}

.message-time {
  font-size: 12px;
  color: #999;
}
</style>