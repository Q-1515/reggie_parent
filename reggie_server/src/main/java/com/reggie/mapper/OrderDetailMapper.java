package com.reggie.mapper;

import com.reggie.entity.OrderDetail;
import com.reggie.vo.OrderDetailVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    List<OrderDetail> getDetailByOrderId(Long orderId);

    //------------------------------------------------>>>

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    List<OrderDetailVO> getByOrderId(Long orderId);

}
