package com.reggie.controller.admin;

import com.reggie.dto.SetmealDTO;
import com.reggie.dto.SetmealPageQueryDTO;
import com.reggie.result.PageResult;
import com.reggie.result.R;
import com.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐管理接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     *
     * @param setmealDTO 套餐数据
     * @return success
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public R<String> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐:{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return R.success("套餐添加成功");
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO 套餐名称，分类id，售卖状态，页数，每页大小
     * @return PageResult 套餐数据
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public R<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询:{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return R.success(pageResult);
    }

    /**
     * 批量删除的套餐
     *
     * @param ids 批量删除的套餐id
     * @return success
     */
    @DeleteMapping
    @ApiOperation("批量删除的套餐")
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("批量删除:{}", ids);
        setmealService.deleteBatch(ids);
        return R.success("删除成功");
    }


}
