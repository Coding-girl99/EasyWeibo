<template>
  <div class="post-form">
    <div class="form-header">
      <h2>发布微博</h2>
    </div>
    <div class="form-content">
      <textarea 
        v-model="content" 
        placeholder="有什么新鲜事想分享给大家？" 
        class="content-input"
        :maxlength="maxLength"
      ></textarea>
      <div class="content-length">{{ content.length }}/{{ maxLength }}</div>
    </div>
    <div class="form-image" v-if="imageUrl">
      <img :src="imageUrl" alt="上传的图片" class="preview-image">
      <button class="remove-image" @click="removeImage">×</button>
    </div>
    <div class="form-actions">
      <div class="upload-action">
        <label for="image-upload" class="upload-label">
          <span class="upload-icon">📷</span>
          <span>图片</span>
        </label>
        <input 
          type="file" 
          id="image-upload" 
          accept="image/*" 
          @change="handleImageUpload" 
          class="upload-input"
        >
      </div>
      <button 
        class="post-button" 
        @click="submitPost" 
        :disabled="!isValid"
      >
        发布
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useWeiboStore } from '@/stores/weibo'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const router = useRouter()
const weiboStore = useWeiboStore()
const userStore = useUserStore()

const content = ref('')
const imageUrl = ref('')
const maxLength = 280 // 微博最大字符数
const imageFile = ref(null)

// 在组件挂载时检查用户是否登录
onMounted(() => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再发布微博')
    router.push('/login')
  }
})

const isValid = computed(() => {
  return content.value.trim().length > 0
})

const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    imageFile.value = file
    imageUrl.value = URL.createObjectURL(file)
  }
}

const removeImage = () => {
  imageUrl.value = ''
  imageFile.value = null
  // 重置文件输入
  const fileInput = document.getElementById('image-upload')
  if (fileInput) {
    fileInput.value = ''
  }
}

const submitPost = () => {
  if (!userStore.isLoggedIn) {
    alert('请先登录后再发布微博')
    router.push('/login')
    return
  }
  
  if (isValid.value) {
    // 创建新微博对象
    const newWeibo = {
      id: Date.now(), // 生成唯一ID
      author: userStore.currentUser.username,
      avatar: userStore.currentUser.avatar || '/src/assets/images/default-avatar.png',
      content: content.value,
      image: imageUrl.value,
      time: new Date().toLocaleString(),
      likes: 0,
      comments: 0,
      isLiked: false,
      commentList: []
    }
    
    try {
      // 调用store方法发布微博
      weiboStore.addWeibo(newWeibo)
      
      // 重置表单
      content.value = ''
      imageUrl.value = ''
      imageFile.value = null
      
      // 显示成功消息
      alert('微博发布成功！')
      
      // 跳转到首页
      router.push('/')
    } catch (error) {
      console.error('发布微博失败:', error)
      alert('发布失败，请重试')
    }
  }
}
</script>

<style scoped>
.post-form {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-header {
  margin-bottom: 16px;
}

.form-header h2 {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.content-input {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  resize: vertical;
  font-size: 14px;
  line-height: 1.6;
  outline: none;
  transition: border-color 0.3s;
}

.content-input:focus {
  border-color: #1890ff;
}

.content-length {
  text-align: right;
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.form-image {
  margin-top: 16px;
  position: relative;
  display: inline-block;
}

.preview-image {
  max-width: 100%;
  max-height: 200px;
  border-radius: 4px;
}

.remove-image {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
  align-items: center;
}

.upload-action {
  position: relative;
}

.upload-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #666;
}

.upload-icon {
  margin-right: 4px;
  font-size: 18px;
}

.upload-input {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
}

.post-button {
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 24px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.post-button:hover:not(:disabled) {
  background-color: #40a9ff;
}

.post-button:disabled {
  background-color: #bfbfbf;
  cursor: not-allowed;
}
</style>