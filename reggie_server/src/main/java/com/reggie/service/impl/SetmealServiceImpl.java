package com.reggie.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.reggie.constant.MessageConstant;
import com.reggie.constant.StatusConstant;
import com.reggie.dto.SetmealDTO;
import com.reggie.dto.SetmealPageQueryDTO;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import com.reggie.exception.DeletionNotAllowedException;
import com.reggie.exception.SetmealEnableFailedException;
import com.reggie.mapper.DishMapper;
import com.reggie.mapper.SetmealDishMapper;
import com.reggie.mapper.SetmealMapper;
import com.reggie.result.PageResult;
import com.reggie.service.SetmealService;
import com.reggie.vo.DishItemVO;
import com.reggie.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private DishMapper dishMapper; //菜品DAO

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

    /**
     * id查询套餐
     *
     * @param id 套餐id
     * @return setmealVO 回显修改套餐的数据
     */
    public SetmealVO getById(Long id) {
        return setmealMapper.getByIdWithDish(id);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO 修改的套餐信息
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //拷贝套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //更新套餐数据
        setmealMapper.updatesByIds(setmeal);

        //套餐id
        Long setmealId = setmealDTO.getId();

        //删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        List<Long> ids = new ArrayList<>();
        ids.add(setmealId);
        setmealDishMapper.deleteBySetmealId(ids);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        //3、重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 套餐 启用禁用
     *
     * @param status 套餐状态
     * @param id     套餐id
     */
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if (StatusConstant.ENABLE == status) {
            //根据套餐id查询菜品
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if (dishList != null && dishList.size() > 0) {
                dishList.forEach(dish -> {
                            //判断菜品书否是未启动
                            if (dish.getStatus() == StatusConstant.DISABLE) {
                                //套餐启动失败，套餐包未起售菜品
                                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                            }
                        }
                );
            }
        }

        //起售状态的话直接更新为停售
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.updatesByIds(setmeal);

    }

    /**
     * 条件查询
     *
     * @param setmeal 菜动态条件查询套餐
     * @return 所有套餐
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return dishMapper.getDishItemBySetmealId(id);
    }

}
