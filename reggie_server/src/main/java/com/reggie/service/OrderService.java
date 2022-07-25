package com.reggie.service;

import com.reggie.dto.OrdersPaymentDTO;
import com.reggie.dto.OrdersSubmitDTO;
import com.reggie.result.PageResult;
import com.reggie.vo.OrderPaymentVO;
import com.reggie.vo.OrderSubmitVO;
import com.reggie.vo.OrderVO;

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
}
