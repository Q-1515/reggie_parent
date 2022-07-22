package com.reggie.mapper;

import com.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    //根据菜品ids找套餐
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    //保存套餐和菜品的关联关系
    void insertBatch(List<SetmealDish> setmealDishes);
}
