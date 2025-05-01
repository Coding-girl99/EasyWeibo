package com.easyweibo.service.impl;

import com.easyweibo.entity.Weibo;
import com.easyweibo.mapper.LikeMapper;
import com.easyweibo.mapper.WeiboMapper;
import com.easyweibo.service.WeiboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class WeiboServiceImpl implements WeiboService {

    @Resource
    private WeiboMapper weiboMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Override
    public void updateCommentCount(Integer weiboId, boolean isIncrement) {
        weiboMapper.updateCommentCount(weiboId, isIncrement ? 1 : -1);
    }

    @Override
    public Weibo findById(Integer id, Integer currentUserId) {
        Weibo weibo = weiboMapper.findById(id);
        if (weibo != null) {
            enrichWeibo(weibo, currentUserId);
        }
        return weibo;
    }

    @Override
    public List<Weibo> findAll(Integer currentUserId) {
        List<Weibo> weibos = weiboMapper.findAll();
        weibos.forEach(weibo -> enrichWeibo(weibo, currentUserId));
        return weibos;
    }

    @Override
    public List<Weibo> findByUserId(Integer userId, Integer currentUserId) {
        List<Weibo> weibos = weiboMapper.findByUserId(userId);
        weibos.forEach(weibo -> enrichWeibo(weibo, currentUserId));
        return weibos;
    }

    @Override
    public List<Weibo> search(String keyword, Integer currentUserId) {
        List<Weibo> weibos = weiboMapper.findByKeyword(keyword);
        weibos.forEach(weibo -> enrichWeibo(weibo, currentUserId));
        return weibos;
    }

    @Override
    public Weibo create(Integer userId, String content, String imageUrl) {
        Weibo weibo = new Weibo();
        weibo.setUserId(userId);
        weibo.setContent(content);
        weibo.setImage(imageUrl);
        weibo.setCreateTime(new Date());
        weibo.setUpdateTime(new Date());

        weiboMapper.insert(weibo);

        return findById(weibo.getId(), userId);
    }

    @Override
    public boolean delete(Integer id, Integer userId) {
        Weibo weibo = weiboMapper.findById(id);
        if (weibo == null || !weibo.getUserId().equals(userId)) {
            return false;
        }

        return weiboMapper.delete(id) > 0;
    }

    @Override
    public boolean like(Integer weiboId, Integer userId) {
        // 检查微博是否存在
        if (weiboMapper.findById(weiboId) == null) {
            return false;
        }

        // 检查是否已点赞
        if (likeMapper.exists(userId, 1, weiboId)) {
            return true;
        }

        return likeMapper.insert(userId, 1, weiboId) > 0;
    }

    @Override
    public boolean unlike(Integer weiboId, Integer userId) {
        return likeMapper.delete(userId, 1, weiboId) > 0;
    }

    // 填充微博的额外信息（点赞数、评论数、是否已点赞）
    private void enrichWeibo(Weibo weibo, Integer currentUserId) {
        weibo.setLikeCount(weiboMapper.countLikes(weibo.getId()));
        weibo.setCommentCount(weiboMapper.countComments(weibo.getId()));

        if (currentUserId != null) {
            weibo.setIsLiked(likeMapper.exists(currentUserId, 1, weibo.getId()));
        } else {
            weibo.setIsLiked(false);
        }
    }
}