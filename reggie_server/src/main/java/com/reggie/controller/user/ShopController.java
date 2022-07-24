package com.reggie.controller.user;

import com.reggie.constant.StatusConstant;
import com.reggie.result.R;
import com.reggie.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "C端-店铺操作接口")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 获取营业状态
     *
     * @return 营业状态
     */
    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public R<Integer> getShopOrStatus() {
        Integer shopStatus = null;

        try {
            shopStatus = shopService.getShopStatus();
        } catch (Exception e) {
            e.printStackTrace();
            shopStatus = StatusConstant.ENABLE;
        }
        log.info("当前营业状态为{}", shopStatus);
        return R.success(shopStatus);
    }


}
