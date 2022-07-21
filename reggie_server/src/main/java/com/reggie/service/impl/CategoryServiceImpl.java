package com.reggie.service.impl;

import com.reggie.constant.StatusConstant;
import com.reggie.dto.CategoryDTO;
import com.reggie.entity.Category;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     *新增分类
     * @param categoryDTO 新增分类(菜品、套餐)
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        //拷贝参数
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        //设置分类的状态默认为0
        category.setStatus(StatusConstant.DISABLE);

        //公共字段无需设置，通过AutoFillAspect切面统一设置
        categoryMapper.insert(category);


    }
}
