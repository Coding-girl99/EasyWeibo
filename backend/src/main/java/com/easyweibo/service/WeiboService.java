package com.easyweibo.service;

import com.easyweibo.entity.Weibo;
import java.util.List;

public interface WeiboService {
    List<Weibo> findAll(Integer currentUserId);

    List<Weibo> findByUserId(Integer userId, Integer currentUserId);

    Weibo findById(Integer id, Integer currentUserId);

    List<Weibo> search(String keyword, Integer currentUserId);

    Weibo create(Integer userId, String content, String imageUrl);

    boolean delete(Integer id, Integer userId);

    boolean like(Integer weiboId, Integer userId);

    boolean unlike(Integer weiboId, Integer userId);

    /**
     * 更新微博评论数
     * 
     * @param weiboId     微博ID
     * @param isIncrement 是否增加评论数（true增加，false减少）
     */
    void updateCommentCount(Integer weiboId, boolean isIncrement);
}