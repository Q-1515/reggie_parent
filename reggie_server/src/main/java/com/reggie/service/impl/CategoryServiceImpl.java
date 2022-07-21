package com.reggie.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.reggie.constant.StatusConstant;
import com.reggie.dto.CategoryDTO;
import com.reggie.dto.CategoryPageQueryDTO;
import com.reggie.entity.Category;
import com.reggie.mapper.CategoryMapper;
import com.reggie.result.PageResult;
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
     * 新增分类
     *
     * @param categoryDTO 新增分类(菜品、套餐)
     */
    public void save(CategoryDTO categoryDTO) {
        //拷贝参数
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        //设置分类的状态默认为0
        category.setStatus(StatusConstant.DISABLE);

        //公共字段无需设置，通过AutoFillAspect切面统一设置
        categoryMapper.insert(category);


    }

    /**
     * 分类信息分页查询
     *
     * @param categoryPageQueryDTO 查询页数，记录数，分类类型，分类的名称
     * @return PageResult 分类集合，总记录数
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        //分页插件自动填充分页sql
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }
}
