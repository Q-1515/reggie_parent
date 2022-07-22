package com.reggie.service.impl;

import com.reggie.constant.StatusConstant;
import com.reggie.dto.SetmealDTO;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import com.reggie.mapper.SetmealDishMapper;
import com.reggie.mapper.SetmealMapper;
import com.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;  //套餐DAO

    @Autowired
    private SetmealDishMapper setmealDishMapper;  //套餐菜品关系 DAO

    /**
     * 新增套餐
     *
     * @param setmealDTO 套餐数据
     */
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        //拷贝套餐数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //套餐表插入数据
        setmealMapper.insert(setmeal);

        //获取关联的外键
        Long setmealId = setmeal.getId();
        //关联的菜品集合
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        //设置关联的外键
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);


    }
}
