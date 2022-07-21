package com.reggie.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.reggie.constant.MessageConstant;
import com.reggie.constant.StatusConstant;
import com.reggie.dto.CategoryDTO;
import com.reggie.dto.CategoryPageQueryDTO;
import com.reggie.entity.Category;
import com.reggie.exception.DeletionNotAllowedException;
import com.reggie.mapper.CategoryMapper;
import com.reggie.mapper.DishMapper;
import com.reggie.mapper.SetmealMapper;
import com.reggie.result.PageResult;
import com.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper; //分类 DAO

    @Autowired
    private DishMapper dishMapper;  //菜品DAO

    @Autowired
    private SetmealMapper setmealMapper;  // 套餐DAO

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

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id删除分类
     *
     * @param id 要删除分类的id
     */
    public void deleteById(Long id) {
        //根据分类id查询对应的菜品数量
        Long dishCount = dishMapper.cuntByCategoryId(id);
        if (dishCount > 0) {
            //当前关联了菜品不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //根据分类id查询对应的套餐数量
        Long setmealCount = setmealMapper.cuntByCategoryId(id);
        if (setmealCount > 0) {
            //当前关联了套餐不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        //删除分类
        categoryMapper.deleteById(id);
    }


    /**
     * 修改分类
     *
     * @param categoryDTO 修改id 修改name 修改sort
     */
    public void update(CategoryDTO categoryDTO) {
        //拷贝数据
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.update(category);

    }

    /**
     * 启用/禁用 分类
     *
     * @param status 状态码
     * @param id     分类id
     */
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();

        categoryMapper.update(category);
    }

    /**
     * 根据类型查询分类
     *
     * @param type 分类类型
     * @return 所有  菜品/套餐
     */
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
