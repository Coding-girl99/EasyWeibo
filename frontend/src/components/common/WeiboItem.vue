<template>
  <div class="weibo-item">
    <div class="weibo-header">
      <div class="weibo-avatar">
        <img :src="weibo.avatar || defaultAvatar" :alt="`${weibo.author}的头像`">
      </div>
      <div>
        <div class="weibo-author">{{ weibo.author }}</div>
        <div class="weibo-time">{{ weibo.time }}</div>
      </div>
    </div>
    <div class="weibo-content">{{ weibo.content }}</div>
    <div v-if="weibo.image" class="weibo-image">
      <img :src="weibo.image" :alt="weibo.content" @click="showFullImage">
    </div>
    <div class="weibo-actions">
      <div class="weibo-action like-action" @click="handleLike" :class="{ 'liked': weibo.isLiked }">
        <span class="like-icon">❤️</span>
        <span class="like-count">{{ weibo.likes }}</span>
      </div>
      <div class="weibo-action comment-action" @click="handleComment">
        <span class="comment-icon">💬</span>
        <span class="comment-count">{{ weibo.comments }}</span>
      </div>
      <div class="weibo-action share-action" @click="handleShare">
        <span class="share-icon">🔄</span>
        <span class="share-text">分享</span>
      </div>
    </div>

    <!-- 添加评论列表区域 -->
    <div v-if="showComments" class="comments-container">
      <CommentList
        :comments="showFullComments ? sortedComments : sortedComments.slice(0, 10)"
        :weiboId="weibo.id"
        @reply-comment="handleReplyComment"
        @like-comment="handleLikeComment"
        @follow-user="handleFollowUser"
      />
      <div v-if="!showFullComments && sortedComments.length > 10" class="view-more-comments">
        <router-link :to="`/weibo/${weibo.id}`" class="view-more-link">查看全部 {{ sortedComments.length }} 条评论</router-link>
      </div>
    </div>

    <div v-if="showCommentInput" class="comment-input-container">
      <input
        type="text"
        v-model="commentText"
        placeholder="发表评论..."
        class="comment-input"
        @keyup.enter="submitComment"
      />
      <button class="comment-submit" @click="submitComment">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useWeiboStore } from '@/stores/weibo'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import CommentList from '@/components/common/CommentList.vue'

const props = defineProps({
  weibo: {
    type: Object,
    required: true
  },
  showFullComments: {
    type: Boolean,
    default: false
  }
})

const defaultAvatar = '/src/assets/images/default-avatar.png'
const weiboStore = useWeiboStore()
const userStore = useUserStore()
const router = useRouter()
const showCommentInput = ref(false)
const showComments = ref(false)
const commentText = ref('')

// 按时间降序排列评论
const sortedComments = computed(() => {
  if (!props.weibo.commentList) return []
  return [...props.weibo.commentList].sort((a, b) => {
    return new Date(b.time) - new Date(a.time)
  })
})

const handleLike = () => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再点赞')
    router.push('/login')
    return
  }

  weiboStore.likeWeibo(props.weibo.id)
  // 添加点赞动画效果
  const likeIcon = document.querySelector(`.weibo-item[data-id="${props.weibo.id}"] .like-icon`)
  if (likeIcon) {
    likeIcon.classList.add('like-animation')
    setTimeout(() => {
      likeIcon.classList.remove('like-animation')
    }, 500)
  }
}

const handleComment = () => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再评论')
    router.push('/login')
    return
  }

  showComments.value = !showComments.value
  showCommentInput.value = showComments.value
  if (showCommentInput.value) {
    setTimeout(() => {
      const commentInput = document.querySelector(`.weibo-item[data-id="${props.weibo.id}"] .comment-input`)
      if (commentInput) {
        commentInput.focus()
      }
    }, 100)
  }
}

const submitComment = () => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再评论')
    router.push('/login')
    return
  }

  if (commentText.value.trim()) {
    // 添加评论提交动画
    const commentBtn = document.querySelector(`.weibo-item[data-id="${props.weibo.id}"] .comment-submit`)
    if (commentBtn) {
      commentBtn.classList.add('submit-animation')
      setTimeout(() => {
        commentBtn.classList.remove('submit-animation')
      }, 300)
    }

    weiboStore.addComment(props.weibo.id, commentText.value)
    commentText.value = ''
    showComments.value = true // 提交评论后显示评论列表
  }
}

const handleShare = () => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再分享')
    router.push('/login')
    return
  }

  // 实现分享功能
  const shareOptions = [
    { name: '微信好友', icon: '💬' },
    { name: '朋友圈', icon: '👥' },
    { name: 'QQ', icon: '🐧' },
    { name: '微博', icon: '🔄' },
    { name: '复制链接', icon: '🔗' }
  ]

  // 创建分享弹窗
  const shareModal = document.createElement('div')
  shareModal.className = 'share-modal'
  shareModal.innerHTML = `
    <div class="share-modal-content">
      <div class="share-modal-header">
        <h3>分享到</h3>
        <span class="close-btn">×</span>
      </div>
      <div class="share-options">
        ${shareOptions.map(option => `
          <div class="share-option">
            <div class="share-icon">${option.icon}</div>
            <div class="share-name">${option.name}</div>
          </div>
        `).join('')}
      </div>
    </div>
  `

  document.body.appendChild(shareModal)

  // 添加关闭事件
  shareModal.querySelector('.close-btn').addEventListener('click', () => {
    document.body.removeChild(shareModal)
  })

  // 添加分享选项点击事件
  shareModal.querySelectorAll('.share-option').forEach((option, index) => {
    option.addEventListener('click', () => {
      if (index === 4) { // 复制链接
        const url = `${window.location.origin}/weibo/${props.weibo.id}`
        navigator.clipboard.writeText(url)
        alert('链接已复制到剪贴板')
      } else {
        alert(`${shareOptions[index].name}分享功能即将上线！`)
      }
      document.body.removeChild(shareModal)
    })
  })
}

const showFullImage = () => {
  // 图片预览功能
  if (props.weibo.image) {
    // 这里可以实现图片预览功能，例如使用模态框展示大图
    alert('图片预览功能即将上线！')
  }
}

// 处理评论回复
const handleReplyComment = ({ commentId, weiboId, content, replyToUser }) => {
  weiboStore.addReplyToComment(weiboId, commentId, content, replyToUser)
}

// 处理评论点赞
const handleLikeComment = ({ commentId, weiboId }) => {
  weiboStore.likeComment(weiboId, commentId)
}

// 处理关注用户
const handleFollowUser = ({ username, isFollowing }) => {
  // 这里可以调用用户存储中的关注方法
  console.log(`${isFollowing ? '已关注' : '取消关注'} ${username}`)
  // 实际项目中应该调用 userStore 中的方法
  // userStore.toggleFollow(username)
}
</script>

<style scoped>
.weibo-item {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.weibo-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.weibo-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.weibo-avatar {
  margin-right: 12px;
  position: relative;
}

.weibo-avatar img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f2f5;
  transition: all 0.3s ease;
}

.weibo-avatar img:hover {
  border-color: #1890ff;
  transform: scale(1.05);
}

.weibo-author {
  font-weight: bold;
  font-size: 16px;
  color: #333;
  margin-bottom: 2px;
}

.weibo-time {
  font-size: 12px;
  color: #999;
}

.weibo-content {
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 12px;
  color: #333;
  word-break: break-word;
}

.weibo-image {
  margin-bottom: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.weibo-image img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.weibo-image img:hover {
  transform: scale(1.02);
}

.weibo-actions {
  display: flex;
  border-top: 1px solid #f0f2f5;
  padding-top: 12px;
}

.weibo-action {
  display: flex;
  align-items: center;
  margin-right: 24px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s ease;
  padding: 6px 10px;
  border-radius: 20px;
}

.weibo-action:hover {
  background-color: #f0f2f5;
  color: #1890ff;
}

.like-action.liked {
  color: #ff4d4f;
}

.like-icon, .comment-icon, .share-icon {
  margin-right: 6px;
  font-size: 18px;
}

.comments-container {
  margin-top: 16px;
  border-top: 1px solid #f0f2f5;
  padding-top: 16px;
}

.comment-input-container {
  margin-top: 12px;
  display: flex;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
}

.comment-input {
  flex: 1;
  border: 1px solid #e0e0e0;
  border-radius: 18px;
  padding: 8px 16px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.comment-input:focus {
  border-color: #1890ff;
}

.comment-submit {
  margin-left: 8px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 18px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.comment-submit:hover {
  background-color: #40a9ff;
}
.view-more-comments {
  text-align: center;
  padding: 10px 0;
  border-top: 1px solid #f0f0f0;
}

.view-more-link {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
}

.view-more-link:hover {
  text-decoration: underline;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.comment-time {
  font-size: 12px;
  color: #999;
  line-height: 24px; /* 与回复按钮高度一致 */
}

.comment-actions {
  display: flex;
  align-items: center;
  height: 24px;
}
</style>

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.comment-time {
  font-size: 12px;
  color: #999;
  line-height: 24px; /* 与回复按钮高度一致 */
}

.comment-actions {
  display: flex;
  align-items: center;
  height: 24px;
}

/* 添加点赞动画 */
.like-animation {
  animation: like-pulse 0.5s ease;
}

@keyframes like-pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.5); }
  100% { transform: scale(1); }
}

/* 添加评论提交动画 */
.submit-animation {
  animation: submit-pulse 0.3s ease;
}

@keyframes submit-pulse {
  0% { transform: scale(1); }
  50% { transform: scale(0.95); }
  100% { transform: scale(1); }
}

/* 分享弹窗样式 */
.share-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.share-modal-content {
  background-color: white;
  border-radius: 12px;
  width: 300px;
  overflow: hidden;
}

.share-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f2f5;
}

.share-modal-header h3 {
  margin: 0;
  font-size: 16px;
}

.close-btn {
  font-size: 20px;
  cursor: pointer;
  color: #999;
}

.share-options {
  display: flex;
  flex-wrap: wrap;
  padding: 16px;
}

.share-option {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 16px;
  cursor: pointer;
}

.share-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.share-name {
  font-size: 12px;
  color: #666;
}