package com.reggie.service;

import com.reggie.dto.ShoppingCartDTO;
import com.reggie.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车service
 */
public interface ShoppingCartService {

    //添加购物车
    void add(ShoppingCartDTO shoppingCartDTO);

    //查询购物车
    List<ShoppingCart> list();

    //清空购物车
    void clean();

    //删除购物车某个商品
    void sub(ShoppingCartDTO shoppingCartDTO);
}
