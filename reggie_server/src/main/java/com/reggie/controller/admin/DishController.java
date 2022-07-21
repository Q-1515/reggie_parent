package com.reggie.controller.admin;

import com.reggie.dto.DishDTO;
import com.reggie.dto.DishPageQueryDTO;
import com.reggie.result.PageResult;
import com.reggie.result.R;
import com.reggie.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;


    /**
     * 添加菜品
     *
     * @param dishDTO 分类id，菜品描述，口味，菜品id,菜品图片路径，菜品名称，菜品价格，菜品状态
     * @return success
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public R<String> save(@RequestBody DishDTO dishDTO) {
        log.info("添加菜品:{}", dishDTO);
        dishService.save(dishDTO);
        return R.success("菜品添加成功");
    }


    /**
     * 菜品信息分页查询
     *
     * @param dishPageQueryDTO 页数，每页记录数，菜品名称，分类id，菜品售卖状态
     * @return PageResult 总页数，菜品对象
     */
    @GetMapping("/page")
    @ApiOperation("菜品信息分页查询")
    public R<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品信息分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return R.success(pageResult);
    }

}
