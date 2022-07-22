package com.reggie.service;

import com.reggie.dto.DishDTO;
import com.reggie.dto.DishPageQueryDTO;
import com.reggie.result.PageResult;

import java.util.List;

public interface DishService {

    //添加菜品
    void save(DishDTO dishDTO);

    //菜品信息分页查询
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    //批量删除菜品
    void delete(List<Long> ids);
}
