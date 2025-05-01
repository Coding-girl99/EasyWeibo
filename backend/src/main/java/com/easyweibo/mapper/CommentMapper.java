package com.easyweibo.mapper;

import com.easyweibo.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
  @Insert("INSERT INTO comment (weibo_id, user_id, content, parent_id, create_time, update_time) " +
      "VALUES (#{weiboId}, #{userId}, #{content}, #{parentId}, #{createTime}, #{updateTime})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Comment comment);

  @Select("SELECT * FROM comment WHERE id = #{id}")
  Comment findById(@Param("id") Integer id);

  @Select("SELECT * FROM comment WHERE weibo_id = #{weiboId} ORDER BY create_time ASC")
  List<Comment> findByWeiboId(@Param("weiboId") Integer weiboId);

  @Select("SELECT * FROM comment WHERE parent_id = #{parentId} ORDER BY create_time ASC")
  List<Comment> findByParentId(@Param("parentId") Integer parentId);

  @Update("UPDATE comment SET like_count = like_count + #{delta} WHERE id = #{id}")
  int updateLikeCount(@Param("id") Integer id, @Param("delta") Integer delta);

  @Delete("DELETE FROM comment WHERE id = #{id} AND user_id = #{userId}")
  int delete(@Param("id") Integer id, @Param("userId") Integer userId);
}