<template>
  <div class="comment-list">
    <div v-if="comments.length === 0" class="no-comments">
      暂无评论，快来发表第一条评论吧！
    </div>
    <div v-else class="comments-wrapper">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-user">
          <div class="comment-avatar">
            <img :src="comment.avatar || defaultAvatar" :alt="`${comment.author}的头像`">
          </div>
          <!-- 添加关注按钮 -->
          <div class="follow-btn" v-if="comment.author !== currentUser" @click="toggleFollow(comment)">
            <span v-if="isFollowing(comment.author)">已关注</span>
            <span v-else>关注</span>
          </div>
        </div>
        <div class="comment-content">
          <div class="comment-header">
            <div class="comment-author">{{ comment.author }}</div>
            <div class="comment-time">{{ comment.time }}</div>
          </div>
          <div class="comment-text">{{ comment.content }}</div>

          <!-- 添加评论互动按钮 - 横向排列 -->
          <div class="comment-interaction">
            <div class="interaction-action like-action" @click="handleLike(comment)" :class="{ 'liked': comment.isLiked }">
              <span class="like-icon">❤️</span>
              <span class="like-count">{{ comment.likes || 0 }}</span>
            </div>
            <div class="interaction-action reply-action" @click="handleReply(comment)">
              <span class="reply-icon">💬</span>
              <span class="reply-text">回复</span>
            </div>
            <div class="interaction-action share-action" @click="handleShare(comment)">
              <span class="share-icon">🔄</span>
              <span class="share-text">分享</span>
            </div>
          </div>

          <!-- 添加回复区域 -->
          <div v-if="comment.replies && comment.replies.length > 0" class="comment-replies xiaohongshu-style">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <div class="reply-content">
                <span class="reply-author">{{ reply.author }}</span>
                <span class="reply-text">{{ reply.content }}</span>
              </div>
              <div class="reply-footer">
                <span class="reply-time">{{ reply.time }}</span>
                <span class="reply-action" @click="handleReply(comment, reply.author)">回复</span>
              </div>
            </div>
          </div>

          <!-- 添加回复输入框 -->
          <div v-if="replyingTo === comment.id" class="reply-input-container">
            <input
              type="text"
              v-model="replyText"
              :placeholder="replyPlaceholder"
              class="reply-input"
              @keyup.enter="submitReply(comment)"
            />
            <button class="reply-submit" @click="submitReply(comment)">发送</button>
            <button class="reply-cancel" @click="cancelReply">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  },
  weiboId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['reply-comment', 'like-comment', 'follow-user'])
const userStore = useUserStore()
const router = useRouter()
const defaultAvatar = '/src/assets/images/default-avatar.png'
const currentUser = computed(() => userStore.currentUser?.username || '')

// 回复相关状态
const replyingTo = ref(null)
const replyText = ref('')
const replyToUser = ref(null)

// 回复占位符
const replyPlaceholder = computed(() => {
  return replyToUser.value ? `回复 @${replyToUser.value}：` : '回复评论...'
})

// 关注状态
const followingUsers = ref([])

// 处理回复
const handleReply = (comment) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再回复评论')
    router.push('/login')
    return
  }

  replyingTo.value = comment.id
  replyToUser.value = comment.author
  // 聚焦到回复输入框
  setTimeout(() => {
    const replyInput = document.querySelector(`.reply-input-${comment.id}`)
    if (replyInput) {
      replyInput.focus()
    }
  }, 100)
}

// 提交回复
const submitReply = (comment) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再回复评论')
    router.push('/login')
    return
  }

  if (replyText.value.trim()) {
    emit('reply-comment', {
      commentId: comment.id,
      weiboId: props.weiboId,
      content: replyText.value,
      replyToUser: replyToUser.value
    })
    replyText.value = ''
    replyingTo.value = null
    replyToUser.value = null
  }
}

// 处理点赞
const handleLike = (comment) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再点赞')
    router.push('/login')
    return
  }

  emit('like-comment', {
    commentId: comment.id,
    weiboId: props.weiboId
  })
}

// 切换关注状态
const toggleFollow = (comment) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再关注用户')
    router.push('/login')
    return
  }

  const username = comment.author
  if (isFollowing(username)) {
    const index = followingUsers.value.indexOf(username)
    if (index !== -1) {
      followingUsers.value.splice(index, 1)
    }
  } else {
    followingUsers.value.push(username)
  }

  emit('follow-user', {
    username,
    isFollowing: isFollowing(username)
  })
}

// 处理分享
const handleShare = (comment) => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再分享')
    router.push('/login')
    return
  }

  alert('分享功能即将上线！')
}
// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyText.value = ''
  replyToUser.value = null
}

// 检查是否已关注
const isFollowing = (username) => {
  return followingUsers.value.includes(username)
}
</script>

<style scoped>
.comment-list {
  margin-top: 16px;
}

.no-comments {
  color: #999;
  text-align: center;
  padding: 16px 0;
}

.comments-wrapper {
  max-height: 400px;
  overflow-y: auto;
}

.comment-item {
  display: flex;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.comment-user {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 12px;
}

.comment-avatar {
  margin-bottom: 8px;
}

.comment-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.follow-btn {
  font-size: 12px;
  padding: 2px 6px;
  background-color: #f2f2f2;
  color: #1890ff;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
}

.follow-btn:hover {
  background-color: #e6f7ff;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center; /* 确保垂直居中对齐 */
  margin-bottom: 4px;
}

.comment-author {
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-actions {
  position: absolute;
  right: 0;
  top: 0; /* 调整位置与评论头部对齐 */
  display: flex;
  align-items: center; /* 确保垂直居中对齐 */
  height: 24px; /* 设置固定高度与评论头部一致 */
}

.comment-action {
  display: flex;
  align-items: center;
  margin-left: 16px;
  cursor: pointer;
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
  word-break: break-word;
}

/* 小红书风格盖楼样式 */
.comment-replies.xiaohongshu-style {
  background-color: #f8f8f8;
  border-radius: 8px;
  padding: 8px 12px;
  margin-top: 8px;
  margin-bottom: 8px;
}

.reply-item {
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-content {
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 4px;
}

.reply-author {
  font-weight: bold;
  color: #333;
  margin-right: 6px;
}

.reply-text {
  color: #333;
  word-break: break-word;
}

.reply-footer {
  display: flex;
  justify-content: space-between;
  align-items: center; /* 确保垂直居中对齐 */
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.reply-time {
  color: #999;
}

.reply-action {
  color: #666;
  cursor: pointer;
}

.reply-action:hover {
  color: #1890ff;
}

.reply-input-container {
  display: flex;
  margin-top: 8px;
  align-items: center;
}

.reply-input {
  flex: 1;
  border: 1px solid #e0e0e0;
  border-radius: 18px;
  padding: 6px 12px;
  font-size: 13px;
  outline: none;
  transition: border-color 0.3s;
}

.reply-input:focus {
  border-color: #1890ff;
}

.reply-submit, .reply-cancel {
  margin-left: 8px;
  border: none;
  border-radius: 4px;
  padding: 4px 10px;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.reply-submit {
  background-color: #1890ff;
  color: white;
}

.reply-submit:hover {
  background-color: #40a9ff;
}

.reply-cancel {
  background-color: #f0f0f0;
  color: #666;
}

.reply-cancel:hover {
  background-color: #e0e0e0;
}

.comment-actions {
  display: flex;
  margin-top: 8px;
}

/* 修改评论互动样式为横向排列 */
.comment-interaction {
  display: flex;
  flex-direction: row; /* 确保横向排列 */
  margin-top: 8px;
  margin-bottom: 8px;
}

.interaction-action {
  display: flex;
  align-items: center;
  margin-right: 24px; /* 横向间距 */
  cursor: pointer;
  color: #666;
  font-size: 12px;
  transition: color 0.2s;
}

.interaction-action:last-child {
  margin-right: 0; /* 最后一个元素不需要右边距 */
}

.comment-action:hover {
  color: #1890ff;
}

.like-action.liked {
  color: #ff4d4f;
}

.reply-icon, .like-icon {
  font-size: 14px;
  margin-right: 4px;
}

/* 确保评论时间与其他元素对齐 */
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}
</style>