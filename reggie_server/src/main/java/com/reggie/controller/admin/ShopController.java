package com.reggie.controller.admin;

import com.reggie.constant.StatusConstant;
import com.reggie.result.R;
import com.reggie.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺操作接口")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 设置营业状态
     *
     * @return success
     */
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public R<String> setShopOrStatus(@PathVariable Integer status) {
        log.info("设置店铺状态为:{}", status);
        shopService.setShopStatus(status);
        return R.success("店铺状态更新成功");
    }


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
            log.error(e.getMessage());
            shopStatus = StatusConstant.ENABLE;
        }
        log.info("当前营业状态为{}", shopStatus);
        return R.success(shopStatus);
    }


}
