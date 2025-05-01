import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 用户状态
  const currentUser = ref(null)
  const isLoggedIn = computed(() => !!currentUser.value)

  // 模拟用户数据库
  const users = ref([
    {
      id: 1,
      username: '勇敢牛牛',
      password: '123456',
      // 添加一个有效的头像URL
      avatar: '/src/assets/images/default-avatar.png'
    }
  ])

  // 登录
  const login = async (credentials) => {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    const user = users.value.find(
      u => u.username === credentials.username && u.password === credentials.password
    )

    if (user) {
      // 不返回密码
      const { password, ...userWithoutPassword } = user
      currentUser.value = userWithoutPassword
      return userWithoutPassword
    } else {
      throw new Error('用户名或密码错误')
    }
  }

  // 注册
  // 在用户存储中添加注册时间处理
  const register = async (userData) => {
    try {
      // 确保注册时间被正确保存
      const user = {
        ...userData,
        registerTime: userData.registerTime || new Date().toISOString()
      };

      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500));

      // 保存用户数据到本地存储
      const users = JSON.parse(localStorage.getItem('users') || '[]');

      // 检查用户名是否已存在
      if (users.some(u => u.username === user.username)) {
        throw new Error('用户名已存在');
      }

      // 添加新用户
      users.push(user);
      localStorage.setItem('users', JSON.stringify(users));

      return true;
    } catch (error) {
      console.error('注册失败:', error);
      throw error;
    }
  };

  // 登出
  const logout = () => {
    currentUser.value = null
  }

  // 更新用户信息
  const updateUserInfo = (userInfo) => {
    if (!currentUser.value) return false

    currentUser.value = {
      ...currentUser.value,
      ...userInfo
    }

    // 同时更新用户数组中的用户信息
    const userIndex = users.value.findIndex(u => u.id === currentUser.value.id)
    if (userIndex !== -1) {
      users.value[userIndex] = {
        ...users.value[userIndex],
        ...userInfo
      }
    }

    return true
  }

  return {
    currentUser,
    isLoggedIn,
    login,
    register,
    logout,
    updateUserInfo
  }
})