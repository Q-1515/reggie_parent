package com.reggie.controller.admin;

import com.reggie.constant.StatusConstant;
import com.reggie.dto.GoodsSalesDTO;
import com.reggie.entity.Orders;
import com.reggie.result.R;
import com.reggie.service.ReportService;
import com.reggie.service.UserService;
import com.reggie.vo.OrderReportVO;
import com.reggie.vo.SalesTop10ReportVO;
import com.reggie.vo.TurnoverReportVO;
import com.reggie.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "运用数据")
public class ReportController {
    @Autowired
    private ReportService reportService;


    /**
     * 查询指定时间段内的营业额
     *
     * @param begin 起始时间
     * @param end   结束时间
     * @return 营业额
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public R<TurnoverReportVO> getTurnover(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计:{},{}", begin, end);
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);//日期计算，当前日期加1天
            dateList.add(begin);
        }


        //存放每天的营业额列表
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);    //获取当天最小值
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);      //获取当天最大值

            //获取每天的营业额
            Double turnover = reportService.getTurnover(beginTime, endTime);
            turnoverList.add(turnover);
        }
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();

        return R.success(turnoverReportVO);
    }


    /**
     * 查询指定时间段内的用户数量
     *
     * @param begin 起始时间
     * @param end   结束时间
     * @return 时间，新增用户数，用户总数
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public R<UserReportVO> getuser(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计:{},{}", begin, end);
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);//日期计算，当前日期加1天
            dateList.add(begin);
        }


        //存放每天的用户总量列表
        List<Integer> totalUserList = new ArrayList<>();
        //存放每天的新增用户量列表
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //添加每天的用户总量列表
            totalUserList.add(reportService.getUserCount(null, date));
            //添加每天的新增用户量列表
            newUserList.add(reportService.getUserCount(date, date));
        }

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();

        return R.success(userReportVO);
    }


    /**
     * 根据订单状态统计指定时间段内的订单数量
     *
     * @param begin 起始时间
     * @param end   结束时间
     * @return 日期, 每日订单数, 每日有效订单数
     * 有效订单数、总订单数、订单完成率，订单完成率 = 有效订单数 / 总订单数
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public R<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计接口:{},{}", begin, end);
        //存放日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);//日期计算，当前日期加1天
            dateList.add(begin);
        }

        //存放每日订单数量列表
        List<Integer> orderCountList = new ArrayList<>();
        //存放每日有效订单数量列表
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //订单数量
            orderCountList.add(reportService.getOrderCount(beginTime, endTime, null));
            //有效订单数
            validOrderCountList.add(reportService.getOrderCount(beginTime, endTime, Orders.COMPLETED));
        }

        //订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //有效订单总数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //订单完成率
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;


        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))                        //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
                .orderCountList(StringUtils.join(orderCountList, ","))            //每日订单数，以逗号分隔，例如：260,210,215
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))  //每日有效订单数，以逗号分隔，例如：20,21,10
                .totalOrderCount(totalOrderCount)                                          //订单总数
                .validOrderCount(validOrderCount)                                          //有效订单数
                .orderCompletionRate(orderCompletionRate)                                  //订单完成率
                .build();

        return R.success(orderReportVO);

    }

    /**
     * 查询销量排名top10
     *
     * @param begin 起始时间
     * @param end   结束时间
     * @return top10的菜名和数量
     */
    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public R<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询销量排名top10接口:{},{}", begin, end);

        //获取时间范围内的所有菜品/套餐 名和数量
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> top10 = reportService.getSalesTop10(beginTime, endTime);

        //将top10的菜名和数量放入集合
        List<String> nameList = top10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = top10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        SalesTop10ReportVO top10ReportVO = SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
        return R.success(top10ReportVO);
    }
}


