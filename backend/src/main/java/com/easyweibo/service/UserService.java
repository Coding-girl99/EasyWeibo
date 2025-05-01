package com.easyweibo.service;

import com.easyweibo.entity.User;

public interface UserService {
    User findById(Integer id);

    User findByUsername(String username);

    User register(String username, String password, String nickname);

    User update(User user);

    boolean updateAvatar(Integer userId, String avatarUrl);

    boolean updateProfile(Integer userId, String nickname, String bio);
}