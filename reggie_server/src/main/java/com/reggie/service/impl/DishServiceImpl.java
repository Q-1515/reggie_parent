package com.reggie.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.reggie.constant.MessageConstant;
import com.reggie.dto.DishDTO;
import com.reggie.dto.DishPageQueryDTO;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.entity.SetmealDish;
import com.reggie.exception.DeletionNotAllowedException;
import com.reggie.mapper.DishFlavorMapper;
import com.reggie.mapper.DishMapper;
import com.reggie.mapper.SetmealDishMapper;
import com.reggie.mapper.SetmealMapper;
import com.reggie.result.PageResult;
import com.reggie.service.DishService;
import com.reggie.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜品业务实现
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;    //菜品DAO
    @Autowired
    private DishFlavorMapper dishFlavorMapper;   //菜品味道DAO

    @Autowired
    private SetmealDishMapper setmealDishMapper;  //套餐菜品绑定DAO

    /**
     * 添加菜品
     *
     * @param dishDTO 分类id，菜品描述，口味，菜品id,菜品图片路径，菜品名称，菜品价格，菜品状态
     */
    @Transactional
    public void save(DishDTO dishDTO) {
        //菜品实体类
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //插入数据到菜品表
        dishMapper.insert(dish);

        //获取菜品主键值，需要保存到口味表中
        Long dishId = dish.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors == null || flavors.size() == 0) {
            return;
        }

        //循环添加id
        flavors.forEach(item -> {
            //设置口味关联的菜品id
            item.setDishId(dishId);
        });

        //插入菜品口味
        dishFlavorMapper.insert(flavors);
    }

    /**
     * 菜品信息分页查询
     *
     * @param dishPageQueryDTO 页数，每页记录数，菜品名称，分类id，菜品售卖状态
     * @return PageResult 总页数，菜品对象
     */
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //分页插件自动填充分页命令
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.PageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids 批量删除的id
     */
    @Transactional
    public void delete(List<Long> ids) {
        //判断所有菜品是否是启用的
        ids.forEach(dishId -> {
            Dish dish = dishMapper.selectById(dishId);

            //如果菜品起售丢出异常
            if (dish.getStatus() == 1){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        //判断菜品是否在套餐中
        List<Long> setmealId = setmealDishMapper.getSetmealIdsByDishIds(ids);

        //如果绑定了套餐则丢出异常
        if (setmealId != null && setmealId.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        ids.forEach(dishId ->{
            // 删除菜品表中的数据
             dishMapper.deleteById(dishId);
            // 删除菜品口味表中的数据
             dishFlavorMapper.deleteByDishId(dishId);
        });
    }



}
