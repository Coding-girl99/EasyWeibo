<template>
  <div class="weibo-detail-page">
    <NavBar />
    <div class="container">
      <div class="weibo-detail-container">
        <!-- 微博内容 -->
        <div v-if="weibo" class="weibo-content-container">
          <WeiboItem :weibo="weibo" :showFullComments="true" />
        </div>

        <!-- 加载中状态 -->
        <div v-else-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <div class="loading-text">加载中...</div>
        </div>

        <!-- 错误状态 -->
        <div v-else class="error-state">
          <div class="error-icon">😕</div>
          <div class="error-text">微博不存在或已被删除</div>
          <router-link to="/" class="back-home">返回首页</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWeiboStore } from '@/stores/weibo'
import { useUserStore } from '@/stores/user'
import NavBar from '@/components/common/NavBar.vue'
import WeiboItem from '@/components/common/WeiboItem.vue'

const route = useRoute()
const router = useRouter()
const weiboStore = useWeiboStore()
const userStore = useUserStore()

const weibo = ref(null)
const loading = ref(true)

onMounted(async () => {
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    // 未登录时跳转到登录页面
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }

  const weiboId = route.params.id

  try {
    // 获取微博详情
    loading.value = true
    weibo.value = await weiboStore.getWeiboById(weiboId)
  } catch (error) {
    console.error('获取微博详情失败:', error)
    weibo.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.weibo-detail-page {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.weibo-detail-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.loading-state, .error-state {
  padding: 60px 0;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 16px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text, .error-text {
  font-size: 16px;
  color: #666;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.back-home {
  display: inline-block;
  margin-top: 16px;
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.back-home:hover {
  background-color: #40a9ff;
}
</style>