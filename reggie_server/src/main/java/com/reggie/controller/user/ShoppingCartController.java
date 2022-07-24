package com.reggie.controller.user;

import com.reggie.dto.ShoppingCartDTO;
import com.reggie.entity.ShoppingCart;
import com.reggie.result.R;
import com.reggie.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C端-购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO 购物车(口味,菜品id,套餐id)
     * @return success
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车接口")
    public R<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车，商品：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return R.success("购物车添加成功");
    }


    /**
     * 查询购物车
     *
     * @return 购物车信息
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查询购物车");
        List<ShoppingCart> shoppingCarts = shoppingCartService.list();
        return R.success(shoppingCarts);
    }
}
