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
}
