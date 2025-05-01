package com.easyweibo.controller;

import com.easyweibo.dto.ApiResponse;
import com.easyweibo.entity.User;
import com.easyweibo.entity.Weibo;
import com.easyweibo.service.UserService;
import com.easyweibo.service.WeiboService;
import com.easyweibo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/weibo")
public class WeiboController {

    @Autowired
    private WeiboService weiboService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    // 获取当前登录用户ID
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        User user = userService.findByUsername(authentication.getName());
        return user != null ? user.getId() : null;
    }

    // 公开接口 - 获取所有微博
    @GetMapping("/public/list")
    public ApiResponse<List<Weibo>> getPublicWeibos() {
        return ApiResponse.success(weiboService.findAll(null));
    }

    // 获取所有微博（带登录状态）
    @GetMapping("/list")
    public ApiResponse<List<Weibo>> getWeibos() {
        return ApiResponse.success(weiboService.findAll(getCurrentUserId()));
    }

    // 获取指定用户的微博
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Weibo>> getUserWeibos(@PathVariable Integer userId) {
        return ApiResponse.success(weiboService.findByUserId(userId, getCurrentUserId()));
    }

    // 获取单条微博
    @GetMapping("/{id}")
    public ApiResponse<Weibo> getWeibo(@PathVariable Integer id) {
        Weibo weibo = weiboService.findById(id, getCurrentUserId());
        if (weibo == null) {
            return ApiResponse.error(404, "微博不存在");
        }
        return ApiResponse.success(weibo);
    }

    // 搜索微博
    @GetMapping("/search")
    public ApiResponse<List<Weibo>> searchWeibos(@RequestParam String keyword) {
        return ApiResponse.success(weiboService.search(keyword, getCurrentUserId()));
    }

    // 发布微博
    @PostMapping
    public ApiResponse<Weibo> createWeibo(@RequestParam String content,
            @RequestParam(required = false) MultipartFile image) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = fileService.uploadFile(image, "images");
            } catch (Exception e) {
                return ApiResponse.error(500, "图片上传失败");
            }
        }

        Weibo weibo = weiboService.create(userId, content, imageUrl);
        return ApiResponse.success("发布成功", weibo);
    }

    // 删除微博
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteWeibo(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        if (weiboService.delete(id, userId)) {
            return ApiResponse.success("删除成功", null);
        } else {
            return ApiResponse.error(403, "无权删除该微博");
        }
    }

    // 点赞微博
    @PostMapping("/{id}/like")
    public ApiResponse<?> likeWeibo(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        if (weiboService.like(id, userId)) {
            return ApiResponse.success("点赞成功", null);
        } else {
            return ApiResponse.error(400, "点赞失败");
        }
    }

    // 取消点赞
    @DeleteMapping("/{id}/like")
    public ApiResponse<?> unlikeWeibo(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        if (weiboService.unlike(id, userId)) {
            return ApiResponse.success("取消点赞成功", null);
        } else {
            return ApiResponse.error(400, "取消点赞失败");
        }
    }
}