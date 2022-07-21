package com.reggie.mapper;

import com.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    //插入菜品口味
    void insert(@Param("flavors") List<DishFlavor> flavors);
}
