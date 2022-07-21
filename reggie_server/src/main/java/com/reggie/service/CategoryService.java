package com.reggie.service;


import com.reggie.dto.CategoryDTO;
import com.reggie.dto.CategoryPageQueryDTO;
import com.reggie.mapper.CategoryMapper;
import com.reggie.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

public interface CategoryService {
    //新增分类
    void save(CategoryDTO categoryDTO);

    //分类信息分页查询
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
}
