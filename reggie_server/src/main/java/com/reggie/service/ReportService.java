package com.reggie.service;

import com.reggie.dto.GoodsSalesDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    //查询指定时间段内的营业额
    Double getTurnover(LocalDateTime beginTime, LocalDateTime endTime);

    //查询指定时间段内的用户数量
    Integer getUserCount(LocalDate beginTime, LocalDate endTime);

    //根据订单状态统计指定时间段内的订单数量
    Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    //统计指定时间段内的商品销量排名top10
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);

}
