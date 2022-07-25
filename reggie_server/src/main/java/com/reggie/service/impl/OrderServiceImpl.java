package com.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.reggie.constant.MessageConstant;
import com.reggie.context.BaseContext;
import com.reggie.dto.*;
import com.reggie.entity.*;
import com.reggie.exception.AddressBookBusinessException;
import com.reggie.exception.OrderBusinessException;
import com.reggie.exception.ShoppingCartBusinessException;
import com.reggie.mapper.*;
import com.reggie.result.PageResult;
import com.reggie.service.OrderService;
import com.reggie.vo.*;
import com.reggie.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;                //订单DAO
    @Autowired
    private OrderDetailMapper orderDetailMapper;    //订单明细DAO
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;  //购物车DAO
    @Autowired
    private UserMapper userMapper;                  //用户DAO
    @Autowired
    private AddressBookMapper addressBookMapper;    //地址簿DAO

    @Autowired
    private WebSocketServer webSocketServer;


    /**
     * 用户下单
     *
     * @param ordersSubmitDTO 订单购买信息
     * @return 订单信息（订单id，金额，订单号，下单时间）
     */
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
//        用户下单处理逻辑：
//        处理下单过程各种异常情况（购物车为空，地址簿为空等）
//        封装订单数据，向订单表插入一条数据
//        封装订单明细数据，向订单明细表插入一条或多条数据
//        清空当前用户的购物车数据
//        封装返回结果


        //获取用户id
        Long userId = BaseContext.getCurrentId();
        //获取当前的购物车
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        //判断购物车为空
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            //购物车不能为空
            log.error("用户下单失败，失败原因：{}", MessageConstant.SHOPPING_CART_IS_NULL);
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //获取地址簿id
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        // 查询用户地址信息
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if (addressBook == null) {
            //如果地址簿查询为空，直接抛出业务异常
            log.error("用户下单失败，失败原因：{}", MessageConstant.ADDRESS_BOOK_IS_NULL);
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //封装订单数据，向订单表插入一条数据
        Orders order = Orders.builder()
                .number(String.valueOf(System.currentTimeMillis()))                 //订单号
                .status(Orders.PENDING_PAYMENT)                                     //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
                .userId(userId)                                                     //用户id
                .addressBookId(addressBookId)                                       //地址id
                .orderTime(LocalDateTime.now())                                     //下单时间
                .payMethod(ordersSubmitDTO.getPayMethod())                          //支付方式 1微信，2支付宝
                .payStatus(Orders.UN_PAID)                                          //支付状态 0未支付 1已支付 2退款
                .amount(ordersSubmitDTO.getAmount())                                //实收金额
                .remark(ordersSubmitDTO.getRemark())                                //备注
                .userName(userMapper.getById(userId).getName())                     //用户名
                .phone(addressBook.getPhone())                                      //手机号
                .address(addressBook.getProvinceName()                              //地址
                        + addressBook.getCityName()
                        + addressBook.getDistrictName()
                        + addressBook.getDetail())
                .consignee(addressBook.getConsignee())                              //收货人
                .estimatedDeliveryTime(ordersSubmitDTO.getEstimatedDeliveryTime())  //预计送达时间
                .deliveryStatus(ordersSubmitDTO.getDeliveryStatus())                //配送状态 1立即送出  0选择具体时间
                .packAmount(ordersSubmitDTO.getPackAmount())                        //打包费
                .tablewareNumber(ordersSubmitDTO.getTablewareNumber())              //餐具数量
                .tablewareStatus(ordersSubmitDTO.getTablewareStatus())              //餐具数量状态 1按餐量提供  0选择具体数量
                .build();

        //向订单表插入一条数据
        orderMapper.insert(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            //拷贝购物车数据
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            //设置订单id
            orderDetail.setOrderId(order.getId());
            //加入到订单详集合
            orderDetails.add(orderDetail);
        }

        // 向订单明细表插入多条数据
        orderDetailMapper.insertBatch(orderDetails);

        // 清空购物车表
        shoppingCartMapper.deleteByUserId(userId);

        //封装返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())                     //订单id
                .orderNumber(order.getNumber())        //订单号
                .orderTime(order.getOrderTime())       //下单时间
                .orderAmount(order.getAmount())        //实收金额
                .build();

        log.info("用户下单成功：{}", orderSubmitVO);
        return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber(), userId);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payMethod(ordersPaymentDTO.getPayMethod())
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.updateOrdersById(orders);

        //TODO 后续通过WebSocket实现来单提醒
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 1);//通知类型 1来单提醒 2客户催单
        map.put("orderId", orders.getId());//订单id
        map.put("content", "订单号：" + ordersDB.getNumber());

        webSocketServer.sendToAllClient(JSON.toJSONString(map));

        // 封装预计送达时间，响应结果
        OrderPaymentVO orderPaymentVO = new OrderPaymentVO();
        orderPaymentVO.setEstimatedDeliveryTime(ordersDB.getEstimatedDeliveryTime());

        return orderPaymentVO;
    }

    /**
     * 用户端订单分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        // map封装查询条件
        Map map = new HashMap();
        map.put("userId", BaseContext.getCurrentId());
        map.put("status", status);
        if (status != null && status.equals(Orders.CANCELLED)) {
            map.put("payStatus", Orders.REFUND);
        }

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuerySortByOrderTime(map);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getDetailByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    public OrderVO details(Long id) {
        // 根据id查询订单
        Orders orders = orderMapper.getById(id);

        // 查询该订单对应的菜品/套餐详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getDetailByOrderId(orders.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 催单
     *
     * @param id
     */
    public void reminder(Long id) {
        // 查询订单是否存在
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }


        //TODO 后续基于WebSocket实现催单
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 2);//通知类型 1来单提醒 2客户催单
        map.put("orderId",id);//订单id
        map.put("content", "订单号：" + orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    @Override
    public void userCancelById(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间、付款状态
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.updateOrdersById(orders);
    }

    /**
     * 再来一单
     *
     * @param id
     */
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getDetailByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    //---------------------------------------------------->>>

    /**
     * 订单搜索
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
    @Override
    public PageResult conditionSearch(int pageNum, int pageSize, String number, String phone, Integer status,
                                      LocalDateTime beginTime, LocalDateTime endTime) {
        PageHelper.startPage(pageNum, pageSize);

        Map map = new HashMap();
        map.put("number", number);
        map.put("phone", phone);
        map.put("status", status);
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);

        Page<Orders> page = orderMapper.pageQuerySortByOrderTime(map);

        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(), orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetailVO> orderDetailVOList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailVOList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    public OrderStatisticsVO statistics() {
        // 根据状态，分别查询出待接单、待派送、派送中的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.updateOrdersById(orders);
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间、支付状态
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setPayStatus(Orders.REFUND);
        orderMapper.updateOrdersById(orders);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间、支付状态
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setPayStatus(Orders.REFUND);
        orderMapper.updateOrdersById(orders);
    }

    /**
     * 派送订单
     *
     * @param id
     */
    public void delivery(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为3
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为派送中
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        orderMapper.updateOrdersById(orders);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    public void complete(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为4
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.updateOrdersById(orders);
    }


}
