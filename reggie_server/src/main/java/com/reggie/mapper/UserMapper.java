package com.reggie.mapper;

import com.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 插入
     *
     * @param user
     */
    public void insert(User user);

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    public User getById(Long id);

    /**
     * 根据id修改
     *
     * @param user
     */
    public void update(User user);

    /**
     * 根据主键删除
     *
     * @param id
     */
    public void deleteById(Long id);

    /**
     * 条件查询
     *
     * @param user
     * @return
     */
    public List<User> list(User user);

    /**
     * 根据openid查询用户
     * @return
     */
    public User getByOpenid(String openid);
}
