import request from '@/utils/request.js'

export const login = (username, password) => {
  return request.post('/auth/login', { username, password });
};

export const register = (username, password, nickname) => {
  return request.post('/auth/register', { username, password, nickname });
};

export const getPublicWeibos = () => {
  return request.get('/weibo/public/list');
};

export const getWeibos = () => {
  return request.get('/weibo/list');
};

export const getUserWeibos = (userId) => {
  return request.get(`/weibo/user/${userId}`);
};

export const getWeibo = (id) => {
  return request.get(`/weibo/${id}`);
};

export const searchWeibos = (keyword) => {
  return request.get('/weibo/search', { params: { keyword } });
};

export const createWeibo = (content, image) => {
  const formData = new FormData();
  formData.append('content', content);
  if (image) formData.append('image', image);
  return request.post('/weibo', formData);
};

export const deleteWeibo = (id) => {
  return request.delete(`/weibo/${id}`);
};

export const likeWeibo = (id) => {
  return request.post(`/weibo/${id}/like`);
};

export const unlikeWeibo = (id) => {
  return request.delete(`/weibo/${id}/like`);
};

export const createComment = (comment) => {
  return request.post('/comment', comment);
};

export const getComment = (id) => {
  return request.get(`/comment/${id}`);
};

export const getWeiboComments = (weiboId) => {
  return request.get(`/comment/weibo/${weiboId}`);
};

export const getCommentReplies = (parentId) => {
  return request.get(`/comment/replies/${parentId}`);
};

export const deleteComment = (id) => {
  return request.delete(`/comment/${id}`);
};

export const likeComment = (id) => {
  return request.post(`/comment/${id}/like`);
};

export const unlikeComment = (id) => {
  return request.delete(`/comment/${id}/like`);
};

export const getCurrentUser = () => {
  return request.get('/user/current');
};

export const getUser = (id) => {
  return request.get(`/user/${id}`);
};

export const updateAvatar = (avatar) => {
  const formData = new FormData();
  formData.append('avatar', avatar);
  return request.post('/user/avatar', formData);
};

export const updateProfile = (nickname, bio) => {
  return request.put('/user/profile', { nickname, bio });
};