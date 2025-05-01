package com.easyweibo.controller;

import com.easyweibo.dto.ApiResponse;
import com.easyweibo.entity.User;
import com.easyweibo.service.FileService;
import com.easyweibo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    // 获取当前登录用户
    @GetMapping("/current")
    public ApiResponse<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.error(401, "未登录");
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        return ApiResponse.success(user);
    }

    // 获取指定用户信息
    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        // 不返回密码
        user.setPassword(null);

        return ApiResponse.success(user);
    }

    // 更新头像
    @PostMapping("/avatar")
    public ApiResponse<?> updateAvatar(@RequestParam MultipartFile avatar) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.error(401, "未登录");
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        try {
            String avatarUrl = fileService.uploadFile(avatar, "avatars");
            if (userService.updateAvatar(user.getId(), avatarUrl)) {
                return ApiResponse.success("头像更新成功", avatarUrl);
            } else {
                return ApiResponse.error(500, "头像更新失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "头像上传失败");
        }
    }

    // 更新个人资料
    @PutMapping("/profile")
    public ApiResponse<?> updateProfile(@RequestParam String nickname, @RequestParam String bio) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.error(401, "未登录");
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        if (userService.updateProfile(user.getId(), nickname, bio)) {
            user = userService.findById(user.getId());
            return ApiResponse.success("个人资料更新成功", user);
        } else {
            return ApiResponse.error(500, "个人资料更新失败");
        }
    }

    // 删除私有的saveAvatar方法，使用FileService代替
}