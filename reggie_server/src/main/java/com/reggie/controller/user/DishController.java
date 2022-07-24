package com.reggie.controller.user;

import com.reggie.entity.Dish;
import com.reggie.result.R;
import com.reggie.service.DishService;
import com.reggie.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类id
     * @return 菜品和口味
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public R<List<DishVO>> list(Long categoryId) {
        //创建redisKey
        String key = "dish_" + categoryId;

        //从redis中获取缓存
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get("key");
        if (list != null) {
            //查询到缓存数据，直接返回
            return R.success(list);
        }

        //redis没有就从数据库中拿出
        //设置要查询的分类id和启用的状态
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(Dish.ON);

        //从数据库根据分类id查询菜品
        list = dishService.listWithFlavor(dish);

        //存入缓存中
        redisTemplate.opsForValue().set(key, list);
        return R.success(list);
    }

}
