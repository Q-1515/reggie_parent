package com.reggie.mapper;


import com.github.pagehelper.Page;
import com.reggie.annotation.AutoFill;
import com.reggie.constant.AutoFillConstant;
import com.reggie.dto.CategoryPageQueryDTO;
import com.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

    //新增分类
    @AutoFill(type = AutoFillConstant.INSERT)
    void insert(Category category);

    //分类信息分页查询
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    //删除分类
    void deleteById(Long id);

    @AutoFill(type = AutoFillConstant.UPDATE)
    //修改分类
    void update(Category category);
}
