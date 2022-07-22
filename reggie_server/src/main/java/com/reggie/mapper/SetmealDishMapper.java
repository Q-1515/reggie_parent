package com.reggie.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    //根据菜品ids找套餐
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
