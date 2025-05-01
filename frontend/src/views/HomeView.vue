<template>
  <div class="home">
    <NavBar />
    <div class="container">
      <div class="weibo-list">
        <div v-if="weibos.length === 0" class="no-weibos">
          暂无微博，快去发布第一条微博吧！
        </div>
        <WeiboItem
          v-for="weibo in weibos"
          :key="weibo.id"
          :weibo="weibo"
        />
      </div>
      <div class="sidebar">
        <div class="post-card">
          <h3>快来分享新鲜事吧~</h3>
          <router-link to="/post" class="post-btn">发布微博</router-link>
        </div>
        <!-- 推荐用户部分已移除 -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useWeiboStore } from '@/stores/weibo'
import NavBar from '@/components/common/NavBar.vue'
import WeiboItem from '@/components/common/WeiboItem.vue'

const weiboStore = useWeiboStore()
const weibos = computed(() => weiboStore.getAllWeibos)
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
}

.weibo-list {
  flex: 1;
  margin-right: 20px;
}

.no-weibos {
  background-color: #fff;
  padding: 40px;
  text-align: center;
  color: #999;
  border-radius: 8px;
}

.sidebar {
  width: 280px;
}

.post-card {
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.post-card h3 {
  margin-top: 0;
  margin-bottom: 16px;
  font-size: 16px;
  color: #333;
}

.post-btn {
  display: block;
  width: 100%;
  padding: 8px 0;
  background-color: #1890ff;
  color: white;
  text-align: center;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.post-btn:hover {
  background-color: #40a9ff;
}
</style>