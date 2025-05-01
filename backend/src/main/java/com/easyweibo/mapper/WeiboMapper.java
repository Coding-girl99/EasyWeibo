package com.easyweibo.mapper;

import com.easyweibo.entity.Weibo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface WeiboMapper {
    List<Weibo> findAll();

    List<Weibo> findByUserId(Integer userId);

    Weibo findById(Integer id);

    List<Weibo> findByKeyword(String keyword);

    int insert(Weibo weibo);

    int delete(Integer id);

    int updateLikeCount(@Param("id") Integer id, @Param("increment") Integer increment);

    /**
     * 更新微博评论数
     *
     * @param weiboId 微博ID
     * @param delta   变化量（正数增加，负数减少）
     */
    void updateCommentCount(Integer weiboId, Integer delta);

    /**
     * 获取微博点赞数
     *
     * @param id 微博ID
     * @return 点赞数
     */
    int countLikes(Integer id);

    /**
     * 获取微博评论数
     *
     * @param id 微博ID
     * @return 评论数
     */
    int countComments(Integer id);
}