package com.reggie.vo;

import lombok.Data;

@Data
public class OrderStatisticsVO {
    //待接单数量
    private Integer toBeConfirmed;

    //待派送数量
    private Integer confirmed;

    //派送中数量
    private Integer deliveryInProgress;
}
