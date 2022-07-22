package com.reggie.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.reggie.constant.MessageConstant;
import com.reggie.constant.StatusConstant;
import com.reggie.dto.SetmealDTO;
import com.reggie.dto.SetmealPageQueryDTO;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import com.reggie.exception.DeletionNotAllowedException;
import com.reggie.mapper.SetmealDishMapper;
import com.reggie.mapper.SetmealMapper;
import com.reggie.result.PageResult;
import com.reggie.service.SetmealService;
import com.reggie.vo.SetmealVO;
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

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO 套餐名称，分类id，售卖状态，页数，每页大小
     * @return PageResult 套餐数据
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //分页插件自动实现分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除的套餐
     *
     * @param ids 批量删除的套餐id
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
                    //通过套餐id查找套餐数据
                    Setmeal setmeal = setmealMapper.getById(id);
                    if (StatusConstant.ENABLE == setmeal.getStatus()) {
                        //起售中的套餐不能删除
                        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
                    }
                }
        );

        //套餐ids删除套餐表中的数据
        setmealMapper.deleteById(ids);
        //套餐id删除套餐菜品关系表中的数据
        setmealDishMapper.deleteBySetmealId(ids);
    }



}
