package com.reggie.task;

import com.reggie.entity.Orders;
import com.reggie.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自定义定时任务，处理订单状态
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 支付超时订单处理
     * 对于下单后超过15分钟仍未支付的订单自动修改状态为 [已取消]
     */
    @Scheduled(cron = "0 0/1 * * * ?")//每分钟执行一次
    public void processTimeoutOrder() {
        log.info("开始进行支付超时订单处理:{}", LocalDateTime.now());

        //当前时间-15分钟
        LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-15);
        //查询超出15分钟并且未付款的状态
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, orderTime);

        if (ordersList != null || ordersList.size() > 0) {
            //循环更新超时的订单
            ordersList.forEach(item -> {
                Orders order = Orders.builder()
                        .id(item.getId())                       //订单id
                        .status(Orders.CANCELLED)               //订单状态 6已取消
                        .cancelReason("订单支付超时，自动取消")      //订单取消原因
                        .cancelTime(LocalDateTime.now())        //订单取消时间
                        .build();

                //更新订单信息
                orderMapper.updateOrdersById(order);
            });
        }
    }

    /**
     * 派送中状态的订单处理
     * 对于一直处于派送中状态的订单，自动修改状态为 [已完成]
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行一次
    public void processDeliveryOrder() {
        log.info("开始进行未完成订单状态处理:{}", LocalDateTime.now());

        //前一天中所有派送中的订单自动完成
        LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, orderTime);
        if (ordersList != null || ordersList.size() > 0) {
            //循环更新超时的订单
            ordersList.forEach(item -> {
                Orders order = Orders.builder()
                        .id(item.getId())                       //订单id
                        .status(Orders.COMPLETED)               //订单状态 5已完成
                        .build();

                //更新订单信息
                orderMapper.updateOrdersById(order);
            });
        }
    }

}
