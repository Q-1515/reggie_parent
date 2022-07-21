package com.reggie.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealMapper {

    //根据分类id查询对应的套餐数量
    Long cuntByCategoryId(Long id);
}
