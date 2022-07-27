package com.reggie.mapper;

import com.github.pagehelper.Page;
import com.reggie.dto.GoodsSalesDTO;
import com.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     *
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号和用户id查询订单
     *
     * @param orderNumber
     * @param userId
     * @return
     */
    Orders getByNumber(String orderNumber, Long userId);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void updateOrdersById(Orders orders);

    /**
     * 分页条件查询并按下单时间排序
     *
     * @param map
     * @return
     */
    Page<Orders> pageQuerySortByOrderTime(Map map);

    /**
     * 根据id查询订单
     *
     * @param id
     * @return
     */
    Orders getById(Long id);

    /**
     * 根据状态和下单时间查询订单
     *
     * @param status 状态
     * @param orderTime 下单时间
     * @return 订单
     */
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    //----------------------------------------------------->>>

    /**
     * 根据状态统计订单数量
     * @param status
     * @return
     */
    Integer countStatus(Integer status);

    //获取今天总订单数
    Integer countByMap(Map<String, Object> map);

    //获取今天总营业额
    Double sumByMap(Map<String, Object> map);

    //统计指定时间段内的商品销量排名top10
    List<GoodsSalesDTO> orderMapper(Map<String, Object> map);
}
