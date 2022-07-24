package com.reggie.controller.admin;

import com.reggie.dto.DishDTO;
import com.reggie.dto.DishPageQueryDTO;
import com.reggie.entity.Dish;
import com.reggie.result.PageResult;
import com.reggie.result.R;
import com.reggie.service.DishService;
import com.reggie.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 清理缓存
     *
     * @param pattern redis key值
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }


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

        cleanCache("dish_"+dishDTO.getCategoryId());
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

    /**
     * 批量删除菜品
     *
     * @param ids 批量删除的id
     * @return success
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品:{}", ids);
        dishService.delete(ids);

        cleanCache("dish_*");
        return R.success("批量删除菜品成功");
    }

    /**
     * 根据id查询菜品和关联的口味
     *
     * @param id 菜品id
     * @return DishVO 菜品的参数
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品和关联的口味")
    public R<DishVO> select(@PathVariable Long id) {
        log.info("根据id查询菜品和关联的口味:{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);

        return R.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO 修改的菜品数据
     * @return success
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public R<String> update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品:{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        cleanCache("dish_*");
        return R.success("修改成功");
    }

    /**
     * 菜品起售、停售
     *
     * @param status 菜品状态
     * @param id     菜品id
     * @return success
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售")
    public R<String> startOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品起售、停售---status:{},id:{}", status, id);
        dishService.startOrStop(status, id);
        cleanCache("dish_*");
        return R.success("状态修改成功");
    }


    /**
     * 根据分类id || 菜名 查询菜品
     *
     * @param categoryId 分类id
     * @param name       菜名
     * @return 菜品
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id || 菜名 查询菜品接口")
    public R<List<Dish>> list(Long categoryId, String name) {
        log.info("根据菜名查询菜品接口:{}", name);
        List<Dish> list = dishService.list(categoryId, name);
        return R.success(list);
    }


}
