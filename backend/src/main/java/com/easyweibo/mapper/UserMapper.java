package com.easyweibo.mapper;

import com.easyweibo.entity.User;

public interface UserMapper {
    User findById(Integer id);

    User findByUsername(String username);

    int insert(User user);

    int update(User user);
}