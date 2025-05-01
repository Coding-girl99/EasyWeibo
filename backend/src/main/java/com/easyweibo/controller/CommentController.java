package com.easyweibo.controller;

import com.easyweibo.entity.Comment;
import com.easyweibo.entity.User;
import com.easyweibo.service.CommentService;
import com.easyweibo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/comment")
public class CommentController {

  @Resource
  private CommentService commentService;

  @Resource
  private UserService userService;

  @PostMapping
  public ResponseEntity<?> createComment(@RequestBody Comment comment,
      @AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.findByUsername(userDetails.getUsername());
    comment.setUserId(user.getId());
    return ResponseEntity.ok(commentService.createComment(comment));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getComment(@PathVariable Integer id) {
    return ResponseEntity.ok(commentService.findById(id));
  }

  @GetMapping("/weibo/{weiboId}")
  public ResponseEntity<?> getWeiboComments(@PathVariable Integer weiboId) {
    return ResponseEntity.ok(commentService.findByWeiboId(weiboId));
  }

  @GetMapping("/replies/{parentId}")
  public ResponseEntity<?> getCommentReplies(@PathVariable Integer parentId) {
    return ResponseEntity.ok(commentService.findByParentId(parentId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.findByUsername(userDetails.getUsername());
    commentService.deleteComment(id, user.getId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/like")
  public ResponseEntity<?> likeComment(@PathVariable Integer id) {
    commentService.updateLikeCount(id, true);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/like")
  public ResponseEntity<?> unlikeComment(@PathVariable Integer id) {
    commentService.updateLikeCount(id, false);
    return ResponseEntity.ok().build();
  }
}