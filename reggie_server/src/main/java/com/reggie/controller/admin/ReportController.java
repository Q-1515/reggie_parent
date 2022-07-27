package com.reggie.controller.admin;

import com.reggie.dto.GoodsSalesDTO;
import com.reggie.entity.Orders;
import com.reggie.result.R;
import com.reggie.service.ReportService;
import com.reggie.service.WorkSpaceService;
import com.reggie.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private WorkSpaceService workSpaceService;


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

    /**
     * 导出Excel报表
     */
    @GetMapping("/export")
    @ApiOperation("导出Excel报表接口")
    public void export(HttpServletResponse response) throws IOException {
        //计算日期范围
        LocalDate begin = LocalDate.now().plusDays(-30);
        LocalDate end = LocalDate.now().plusDays(-1);
        //查询最近30天的运营数据
        BusinessDataVO businessData = workSpaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN),
                LocalDateTime.of(end, LocalTime.MAX));

        //通过输入流读取模板文件
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //创建表格对象
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        //获得第1个工作表
        XSSFSheet sheet = workbook.getSheetAt(0);
        //获取第二行
        XSSFRow row = sheet.getRow(1);
        //写入时间范围
        row.getCell(1).setCellValue("时间：" + begin + "至" + end);//时间范围

        //获取第三行
        row = sheet.getRow(3);
        row.getCell(2).setCellValue(businessData.getTurnover());//营业额
        row.getCell(4).setCellValue(businessData.getOrderCompletionRate());//订单完成率
        row.getCell(6).setCellValue(businessData.getNewUsers());//新增用户数

        //获取第四行
        row = sheet.getRow(4);
        row.getCell(2).setCellValue(businessData.getValidOrderCount());//有效订单
        row.getCell(4).setCellValue(businessData.getUnitPrice());//平均客单价


        int rowNum = 7;
        //循环插入30天数据
        for (int i = 0; i < 30; i++) {
            LocalDate date = begin.plusDays(i);
            //查询某天的运营数据
            businessData = workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

            row = sheet.getRow(rowNum++);  //获取当天的行号
            row.getCell(1).setCellValue(date.toString());                       //当天的时间
            row.getCell(2).setCellValue(businessData.getTurnover());            //当天的营业额
            row.getCell(3).setCellValue(businessData.getValidOrderCount());     //当天的有效订单
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate()); //当天的订单完成率
            row.getCell(5).setCellValue(businessData.getUnitPrice());           //当天的平均客单价
            row.getCell(6).setCellValue(businessData.getNewUsers());            //当天的新增用户数
        }

        //获得输出流
        ServletOutputStream out = response.getOutputStream();
        //通过输出流将内存中的Excel文件写到客户端浏览器
        workbook.write(out);
        //关闭资源
        out.flush();
        out.close();
        workbook.close();
    }

}


