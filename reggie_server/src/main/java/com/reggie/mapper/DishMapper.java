package com.reggie.mapper;

import com.reggie.annotation.AutoFill;
import com.reggie.constant.AutoFillConstant;
import com.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {

    //根据分类id查询对应的菜品数量
    Long cuntByCategoryId(Long id);


    @AutoFill(type = AutoFillConstant.INSERT)
    //插入数据到菜品表
    void insert(Dish dish);
}
