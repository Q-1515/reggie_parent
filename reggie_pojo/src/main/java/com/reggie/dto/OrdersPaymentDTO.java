package com.reggie.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersPaymentDTO {
    //订单号
    private String orderNumber;

    //付款方式
    private Integer payMethod;

}
