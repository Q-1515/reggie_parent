package com.reggie.mapper;

import com.reggie.annotation.AutoFill;
import com.reggie.constant.AutoFillConstant;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {

    //根据分类id查询对应的套餐数量
    Long cuntByCategoryId(Long id);

    //根据套餐id更新套餐数据
    @AutoFill(type = AutoFillConstant.UPDATE)
    void updatesByIds(Setmeal setmeal);

    //套餐表插入数据
    @AutoFill(type = AutoFillConstant.INSERT)
    void insert(Setmeal setmeal);
}
