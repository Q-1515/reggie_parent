package com.reggie.mapper;

import com.github.pagehelper.Page;
import com.reggie.annotation.AutoFill;
import com.reggie.constant.AutoFillConstant;
import com.reggie.dto.DishPageQueryDTO;
import com.reggie.entity.Dish;
import com.reggie.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {

    //根据分类id查询对应的菜品数量
    Long cuntByCategoryId(Long id);

    @AutoFill(type = AutoFillConstant.INSERT)
    //插入数据到菜品表
    void insert(Dish dish);

    //菜品信息分页查询
    Page<DishVO> PageQuery(DishPageQueryDTO dishPageQueryDTO);

    //根据id查询所有菜品
    Dish selectById(Long dishId);

    //根据id删除菜品
    void deleteById(Long dishId);



}
