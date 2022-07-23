package com.reggie.service.impl;

import com.reggie.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    public static final String KEY = "SHOP:STATUS";

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 设置店铺的营业状态 1为营业 0为打烊
     *
     * @param status 营业状态
     */
    public void setShopStatus(Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
        log.info("设置店铺营业状态为：{}", status == 1 ? "营业" : "打烊");
    }

    /**
     * 获取店铺的营业状态
     *
     * @return 店铺的营业状态
     */
    public Integer getShopStatus() {
        return (Integer) redisTemplate.opsForValue().get(KEY);
    }
}
