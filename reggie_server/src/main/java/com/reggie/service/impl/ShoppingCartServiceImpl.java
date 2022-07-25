package com.reggie.service.impl;

import com.reggie.context.BaseContext;
import com.reggie.dto.ShoppingCartDTO;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.entity.ShoppingCart;
import com.reggie.mapper.DishMapper;
import com.reggie.mapper.SetmealMapper;
import com.reggie.mapper.ShoppingCartMapper;
import com.reggie.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 添加购物车
     *
     * @param shoppingCartDTO 购物车(口味,菜品id,套餐id)
     */
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //拷贝属性值
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        //设置当前用户的id
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //动态查询购物车里是否有数据
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() > 0) {
            //如果存在那么就相当于相同的
            shoppingCart = list.get(0);
            //在原有基础上+1
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            //更新数据
            shoppingCartMapper.updateNumberById(shoppingCart);
        } else {//不存在相同的数据
            //判断是添加套餐还是菜品
            if (shoppingCartDTO.getDishId() != null) {
                //添加的是菜品
                Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());                         //设置菜名
                shoppingCart.setAmount(dish.getPrice());                      //设置金额
                shoppingCart.setImage(dish.getImage());                       //设置图片
                shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());  //设置用户的口味
            } else {
                //添加的是套餐
                //获取套餐信息
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());                      //设置套餐名
                shoppingCart.setAmount(setmeal.getPrice());                   //设置金额
                shoppingCart.setImage(setmeal.getImage());                    //设置图片
            }

            //设置数量和时间
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            //插入购物车
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查询购物车
     *
     * @return 购物车信息
     */
    public List<ShoppingCart> list() {
        //获取用户id
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 清空购物车
     */
    public void clean() {
        //根据用户id删除购物车
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    /**
     * 删除购物车某个商品
     *
     * @param shoppingCartDTO 菜名id,套餐id，口味
     */
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //拷贝购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询需要删除的数据
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() > 0) {
            //获取当前套餐/菜品的数据
            shoppingCart = list.get(0);
            //如果是最后一份就删除整个
            if (shoppingCart.getNumber() == 1) {
                shoppingCartMapper.deleteById(shoppingCart.getId());
            } else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }
}
