package com.easyweibo.service.impl;

import com.easyweibo.entity.User;
import com.easyweibo.mapper.UserMapper;
import com.easyweibo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User register(String username, String password, String nickname) {
        User existingUser = userMapper.findByUsername(username);
        if (existingUser != null) {
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        userMapper.insert(user);

        return user;
    }

    @Override
    public User update(User user) {
        user.setUpdateTime(new Date());
        userMapper.update(user);
        return user;
    }

    @Override
    public boolean updateAvatar(Integer userId, String avatarUrl) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }

        user.setAvatar(avatarUrl);
        user.setUpdateTime(new Date());
        userMapper.update(user);
        return true;
    }

    @Override
    public boolean updateProfile(Integer userId, String nickname, String bio) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }

        user.setNickname(nickname);
        user.setBio(bio);
        user.setUpdateTime(new Date());
        userMapper.update(user);
        return true;
    }
}