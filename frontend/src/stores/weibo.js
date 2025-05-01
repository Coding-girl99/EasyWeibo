import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useUserStore } from './user'

export const useWeiboStore = defineStore('weibo', () => {
  // 状态
  const weibos = ref([
    {
      id: 1,
      author: '勇敢牛牛',
      avatar: '',
      content: '勇敢牛牛，不怕困难！',
      image: '',
      time: '2025-4-15 12:30',
      likes: 5,
      comments: 2,
      isLiked: false,
      commentList: [
        {
          id: 1,
          author: '小红',
          avatar: '',
          content: 'Nothing is impossible！',
          time: '2025-4-15 12:35'
        },
        {
          id: 2,
          author: '小明',
          avatar: '',
          content: 'Web开发真有趣！',
          time: '2025-4-17 12:40'
        }
      ]
    }
  ])

  // 获取所有微博
  const getAllWeibos = computed(() => weibos.value)

  // 根据ID获取微博
  const getWeiboById = (id) => {
    return weibos.value.find(weibo => weibo.id === id)
  }

  // 创建新微博
  const createWeibo = (weiboData) => {
    const newWeibo = {
      id: Date.now(),
      author: '当前用户', // 应该从用户状态获取
      avatar: '',
      content: weiboData.content,
      image: weiboData.imageFile ? URL.createObjectURL(weiboData.imageFile) : '',
      time: new Date().toLocaleString(),
      likes: 0,
      comments: 0,
      isLiked: false,
      commentList: []
    }

    weibos.value.unshift(newWeibo)
    return newWeibo
  }

  // 添加新微博（直接添加已构建好的微博对象）
  const addWeibo = (newWeibo) => {
    weibos.value.unshift(newWeibo)
    return newWeibo
  }

  // 点赞/取消点赞
  const likeWeibo = (id) => {
    const weibo = weibos.value.find(w => w.id === id)
    if (weibo) {
      weibo.isLiked = !weibo.isLiked
      weibo.likes += weibo.isLiked ? 1 : -1
    }
  }


  // 添加评论
  // 添加评论
  const addComment = (weiboId, commentText) => {
    const weibo = weibos.value.find(w => w.id === weiboId)
    if (weibo) {
      const userStore = useUserStore()
      const newComment = {
        id: Date.now(),
        author: userStore.currentUser?.username || '当前用户',
        avatar: userStore.currentUser?.avatar || '',
        content: commentText,
        time: new Date().toLocaleString(),
        likes: 0,
        isLiked: false,
        shares: 0
      }

      if (!weibo.commentList) {
        weibo.commentList = []
      }

      // 将新评论添加到数组开头，这样在不排序的情况下也能保证最新的在前面
      weibo.commentList.unshift(newComment)
      weibo.comments = weibo.commentList.length

      return newComment
    }
    return null
  }

  // 删除微博
  const deleteWeibo = (id) => {
    const index = weibos.value.findIndex(w => w.id === id)
    if (index !== -1) {
      weibos.value.splice(index, 1)
      return true
    }
    return false
  }

  // 添加评论回复
  const addReplyToComment = (weiboId, commentId, content, replyToUser) => {
    const weibo = weibos.value.find(w => w.id === weiboId)
    if (weibo && weibo.commentList) {
      const comment = weibo.commentList.find(c => c.id === commentId)
      if (comment) {
        const userStore = useUserStore()
        const currentUser = userStore.currentUser?.username || '当前用户'

        if (!comment.replies) {
          comment.replies = []
        }

        // 处理回复内容，如果有@用户，保留原样
        let replyContent = content
        if (replyToUser && !content.includes(`@${replyToUser}`)) {
          replyContent = `@${replyToUser} ${content}`
        }

        const newReply = {
          id: Date.now(),
          author: currentUser,
          avatar: userStore.currentUser?.avatar || '',
          content: replyContent,
          time: new Date().toLocaleString(),
          likes: 0,
          isLiked: false,
          replyTo: replyToUser || null
        }

        comment.replies.push(newReply)
        return newReply
      }
    }
    return null
  }

  // 点赞/取消点赞评论
  const likeComment = (weiboId, commentId) => {
    const weibo = weibos.value.find(w => w.id === weiboId)
    if (weibo && weibo.commentList) {
      const comment = weibo.commentList.find(c => c.id === commentId)
      if (comment) {
        if (!comment.likes) comment.likes = 0
        if (!comment.isLiked) comment.isLiked = false

        comment.isLiked = !comment.isLiked
        comment.likes += comment.isLiked ? 1 : -1

        return true
      }
    }
    return false
  }

  return {
    weibos,
    getAllWeibos,
    getWeiboById,
    createWeibo,
    addWeibo,  // 添加这个方法到返回列表
    likeWeibo,
    addComment,
    deleteWeibo,
    addReplyToComment,
    likeComment
  }
})

