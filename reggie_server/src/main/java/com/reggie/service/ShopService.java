package com.reggie.service;

/**
 * 店铺操作
 */
public interface ShopService {

    //设置店铺的营业状态 1为营业 0为打样
    public void setShopStatus(Integer status);

    //获取店铺的营业状态
    public Integer getShopStatus();

}
