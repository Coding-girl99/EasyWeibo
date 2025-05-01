<template>
  <div class="post-page">
    <NavBar />
    <div class="container">
      <PostForm />
    </div>
  </div>
</template>

<script setup>
import NavBar from '@/components/common/NavBar.vue'
import PostForm from '@/components/forms/PostForm.vue'
</script>

<style scoped>
.post-page {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}
</style>