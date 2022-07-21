package com.reggie.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {

    //根据分类id查询对应的菜品数量
    Long cuntByCategoryId(Long id);
}
