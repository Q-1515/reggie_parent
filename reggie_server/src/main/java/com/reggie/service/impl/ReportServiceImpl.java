package com.reggie.service.impl;

import com.reggie.dto.GoodsSalesDTO;
import com.reggie.entity.Orders;
import com.reggie.mapper.OrderMapper;
import com.reggie.mapper.UserMapper;
import com.reggie.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询指定时间段内的营业额
     *
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @return 营业额
     */
    public Double getTurnover(LocalDateTime beginTime, LocalDateTime endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("beginTime", beginTime);         //起始时间
        map.put("endTime", endTime);             //结束时间
        map.put("status", Orders.COMPLETED);     //订单状态 5已完成
        Double turnover = orderMapper.sumByMap(map);
        return turnover != null ? turnover : 0.0;  //判断营业额是否为空
    }

    /**
     * 查询指定时间段内的用户数量
     *
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @return 时间，新增用户数，用户总数
     */
    public Integer getUserCount(LocalDate beginTime, LocalDate endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return userMapper.countByMap(map);
    }

    /**
     * 根据订单状态统计指定时间段内的订单数量
     *
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @param status    状态码
     * @return 日期, 每日订单数, 每日有效订单数
     * 有效订单数、总订单数、订单完成率，订单完成率 = 有效订单数 / 总订单数
     */
    public Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map<String, Object> map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    /**
     * 统计指定时间段内的商品销量排名top10
     *
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @return Top10
     */
    public List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("status", Orders.COMPLETED);
        return orderMapper.orderMapper(map);
    }
}
