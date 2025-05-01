package com.easyweibo.service.impl;

import com.easyweibo.entity.Comment;
import com.easyweibo.mapper.CommentMapper;
import com.easyweibo.service.CommentService;
import com.easyweibo.service.WeiboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

  @Resource
  private CommentMapper commentMapper;

  @Resource
  private WeiboService weiboService;

  @Override
  public Comment createComment(Comment comment) {
    comment.setCreateTime(new Date());
    comment.setUpdateTime(new Date());
    comment.setLikeCount(0);
    commentMapper.insert(comment);
    weiboService.updateCommentCount(comment.getWeiboId(), true);
    return comment;
  }

  @Override
  public Comment findById(Integer id) {
    return commentMapper.findById(id);
  }

  @Override
  public List<Comment> findByWeiboId(Integer weiboId) {
    return commentMapper.findByWeiboId(weiboId);
  }

  @Override
  public List<Comment> findByParentId(Integer parentId) {
    return commentMapper.findByParentId(parentId);
  }

  @Override
  public void deleteComment(Integer id, Integer userId) {
    Comment comment = findById(id);
    if (comment != null) {
      commentMapper.delete(id, userId);
      weiboService.updateCommentCount(comment.getWeiboId(), false);
    }
  }

  @Override
  public void updateLikeCount(Integer id, boolean isLike) {
    commentMapper.updateLikeCount(id, isLike ? 1 : -1);
  }
}