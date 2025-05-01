package com.easyweibo.mapper;

import org.apache.ibatis.annotations.Param;

public interface LikeMapper {
    boolean exists(@Param("userId") Integer userId, @Param("type") Integer type, @Param("targetId") Integer targetId);

    int insert(@Param("userId") Integer userId, @Param("type") Integer type, @Param("targetId") Integer targetId);

    int delete(@Param("userId") Integer userId, @Param("type") Integer type, @Param("targetId") Integer targetId);
}