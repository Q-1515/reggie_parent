package com.reggie.mapper;


import com.reggie.annotation.AutoFill;
import com.reggie.constant.AutoFillConstant;
import com.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

    //新增分类
    @AutoFill(type = AutoFillConstant.INSERT)
    void insert(Category category);
}
