package com.reggie.service;


import com.reggie.dto.CategoryDTO;
import com.reggie.dto.CategoryPageQueryDTO;
import com.reggie.entity.Category;
import com.reggie.result.PageResult;

import java.util.List;

/**
 * 分类操作
 */
public interface CategoryService {
    //新增分类
    void save(CategoryDTO categoryDTO);

    //分类信息分页查询
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    //根据id删除分类
    void deleteById(Long id);

    //修改分类
    void update(CategoryDTO categoryDTO);

    //启用/禁用 分类
    void startOrStop(Integer status, Long id);

    //根据类型查询分类
    List<Category> list(Integer type);
}
