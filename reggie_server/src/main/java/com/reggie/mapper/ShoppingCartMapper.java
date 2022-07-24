package com.reggie.mapper;

import com.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 条件查询
     *
     * @param shoppingCart
     * @return
     */
    public List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 更新商品数量
     *
     * @param shoppingCart
     */
    public void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     *
     * @param shoppingCart
     */
    public void insert(ShoppingCart shoppingCart);

    /**
     * 根据id删除购物车数据
     *
     * @param id
     */
    public void deleteById(Long id);

    /**
     * 根据用户id删除购物车数据
     *
     * @param userId
     */
    public void deleteByUserId(Long userId);

    /**
     * 批量插入购物车数据
     * 
     * @param shoppingCartList
     */
    public void insertBatch(List<ShoppingCart> shoppingCartList);
}
