package com.reggie.controller.admin;

import com.reggie.result.R;
import com.reggie.service.WorkSpaceService;
import com.reggie.vo.BusinessDataVO;
import com.reggie.vo.DishOverViewVO;
import com.reggie.vo.OrderOverViewVO;
import com.reggie.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台接口")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;


    /**
     * 查询今日营业数据
     *
     * @return 今日营业数据
     */
    @GetMapping("/businessData")
    @ApiOperation("查询今日营业数据")
    public R<BusinessDataVO> dataOverView() {
        log.info("今日数据接口");
        //获取今天的开始和结束时间啊
        LocalDateTime beginTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);
        BusinessDataVO businessData = workSpaceService.getBusinessData(beginTime, endTime);
        return R.success(businessData);

    }
    /**
     * 查询订单管理数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public R<OrderOverViewVO> orderOverView(){
        return R.success(workSpaceService.getOrderOverView());
    }

    /**
     * 查询菜品总览
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品总览")
    public R<DishOverViewVO> dishOverView(){
        return R.success(workSpaceService.getDishOverView());
    }

    /**
     * 查询套餐总览
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public R<SetmealOverViewVO> setmealOverView(){
        return R.success(workSpaceService.getSetmealOverView());
    }
}
