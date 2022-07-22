package com.reggie.mapper;

import com.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealMapper {

    //根据分类id查询对应的套餐数量
    Long cuntByCategoryId(Long id);

}
