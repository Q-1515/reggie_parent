package com.reggie.service;

import com.reggie.dto.*;
import com.reggie.result.PageResult;
import com.reggie.vo.OrderPaymentVO;
import com.reggie.vo.OrderStatisticsVO;
import com.reggie.vo.OrderSubmitVO;
import com.reggie.vo.OrderVO;

import java.time.LocalDateTime;

public interface OrderService {

    //用户下单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    //订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    //用户端订单分页查询
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    //查询订单详情
    OrderVO details(Long id);

    //催单
    void reminder(Long id);

    //用户取消订单
    void userCancelById(Long id);

    //再来一单
    void repetition(Long id);


    //---------------------------------------------------------->>>>>

    /**
     * 条件搜索订单
     *
     * @param pageNum
     * @param pageSize
     * @param number
     * @param phone
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    PageResult conditionSearch(int pageNum, int pageSize, String number, String phone, Integer status,
                               LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     *
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);
}
