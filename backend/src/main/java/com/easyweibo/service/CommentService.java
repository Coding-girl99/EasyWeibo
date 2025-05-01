package com.easyweibo.service;

import com.easyweibo.entity.Comment;

import java.util.List;

public interface CommentService {
  Comment createComment(Comment comment);

  Comment findById(Integer id);

  List<Comment> findByWeiboId(Integer weiboId);

  List<Comment> findByParentId(Integer parentId);

  void deleteComment(Integer id, Integer userId);

  void updateLikeCount(Integer id, boolean isLike);
}