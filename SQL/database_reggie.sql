create database reggie character set utf8;
use reggie;

create table address_book
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_id       bigint                       not null comment '用户id',
    consignee     varchar(50)                  null comment '收货人',
    sex           tinyint                      null comment '性别 0 女 1 男',
    phone         varchar(11)                  not null comment '手机号',
    province_code varchar(12) charset utf8mb4  null comment '省级区划编号',
    province_name varchar(32) charset utf8mb4  null comment '省级名称',
    city_code     varchar(12) charset utf8mb4  null comment '市级区划编号',
    city_name     varchar(32) charset utf8mb4  null comment '市级名称',
    district_code varchar(12) charset utf8mb4  null comment '区级区划编号',
    district_name varchar(32) charset utf8mb4  null comment '区级名称',
    detail        varchar(200) charset utf8mb4 null comment '详细地址',
    label         varchar(100) charset utf8mb4 null comment '标签',
    is_default    tinyint(1) default 0         not null comment '默认 0 否 1是'
)
    comment '地址簿' collate = utf8_bin;

create table category
(
    id          bigint auto_increment comment '主键'
        primary key,
    type        int           null comment '类型   1 菜品分类 2 套餐分类',
    name        varchar(64)   not null comment '分类名称',
    sort        int default 0 not null comment '顺序',
    status      int           null comment '分类状态 0:禁用，1:启用',
    create_time datetime      null comment '创建时间',
    update_time datetime      null comment '更新时间',
    create_user bigint        null comment '创建人',
    update_user bigint        null comment '修改人',
    constraint idx_category_name
        unique (name)
)
    comment '菜品及套餐分类' collate = utf8_bin;

create table dish
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(64)    not null comment '菜品名称',
    category_id bigint         not null comment '菜品分类id',
    price       decimal(10, 2) null comment '菜品价格',
    image       varchar(200)   null comment '图片',
    description varchar(400)   null comment '描述信息',
    status      int default 1  null comment '0 停售 1 起售',
    create_time datetime       null comment '创建时间',
    update_time datetime       null comment '更新时间',
    create_user bigint         null comment '创建人',
    update_user bigint         null comment '修改人',
    constraint idx_dish_name
        unique (name)
)
    comment '菜品' collate = utf8_bin;

create table dish_flavor
(
    id      bigint auto_increment comment '主键'
        primary key,
    dish_id bigint       not null comment '菜品',
    name    varchar(64)  null comment '口味名称',
    value   varchar(500) null comment '口味数据list'
)
    comment '菜品口味关系表' collate = utf8_bin;

create table employee
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(32)   not null comment '姓名',
    username    varchar(32)   not null comment '用户名',
    password    varchar(64)   not null comment '密码',
    phone       varchar(11)   not null comment '手机号',
    sex         varchar(2)    not null comment '性别',
    id_number   varchar(18)   not null comment '身份证号',
    status      int default 1 not null comment '状态 0:禁用，1:启用',
    create_time datetime      null comment '创建时间',
    update_time datetime      null comment '更新时间',
    create_user bigint        null comment '创建人',
    update_user bigint        null comment '修改人',
    constraint idx_username
        unique (username)
)
    comment '员工信息' collate = utf8_bin;

create table order_detail
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(50)    null comment '名字',
    image       varchar(100)   null comment '图片',
    order_id    bigint         not null comment '订单id',
    dish_id     bigint         null comment '菜品id',
    setmeal_id  bigint         null comment '套餐id',
    dish_flavor varchar(50)    null comment '口味',
    number      int default 1  not null comment '数量',
    amount      decimal(10, 2) not null comment '金额'
)
    comment '订单明细表' collate = utf8_bin;

create table orders
(
    id                      bigint auto_increment comment '主键'
        primary key,
    number                  varchar(50)          null comment '订单号',
    status                  int        default 1 not null comment '订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款',
    user_id                 bigint               not null comment '下单用户',
    address_book_id         bigint               not null comment '地址id',
    order_time              datetime             not null comment '下单时间',
    checkout_time           datetime             null comment '结账时间',
    pay_method              int        default 1 not null comment '支付方式 1微信,2支付宝',
    pay_status              tinyint    default 0 not null comment '支付状态 0未支付 1已支付 2退款',
    amount                  decimal(10, 2)       not null comment '实收金额',
    remark                  varchar(100)         null comment '备注',
    phone                   varchar(255)         null comment '手机号',
    address                 varchar(255)         null comment '地址',
    user_name               varchar(255)         null comment '用户名称',
    consignee               varchar(255)         null comment '收货人',
    cancel_reason           varchar(255)         null comment '订单取消原因',
    rejection_reason        varchar(255)         null comment '订单拒绝原因',
    cancel_time             datetime             null comment '订单取消时间',
    estimated_delivery_time datetime             null comment '预计送达时间',
    delivery_status         tinyint(1) default 1 not null comment '配送状态  1立即送出  0选择具体时间',
    delivery_time           datetime             null comment '送达时间',
    pack_amount             int                  null comment '打包费',
    tableware_number        int                  null comment '餐具数量',
    tableware_status        tinyint(1) default 1 not null comment '餐具数量状态  1按餐量提供  0选择具体数量'
)
    comment '订单表' collate = utf8_bin;

create table setmeal
(
    id          bigint auto_increment comment '主键'
        primary key,
    category_id bigint         not null comment '菜品分类id',
    name        varchar(64)    not null comment '套餐名称',
    price       decimal(10, 2) not null comment '套餐价格',
    status      int default 1  null comment '状态 0:停用 1:启用',
    description varchar(512)   null comment '描述信息',
    image       varchar(255)   null comment '图片',
    create_time datetime       null comment '创建时间',
    update_time datetime       null comment '更新时间',
    create_user bigint         null comment '创建人',
    update_user bigint         null comment '修改人',
    constraint idx_setmeal_name
        unique (name)
)
    comment '套餐' collate = utf8_bin;

create table setmeal_dish
(
    id         bigint auto_increment comment '主键'
        primary key,
    setmeal_id varchar(32)    not null comment '套餐id ',
    dish_id    varchar(32)    not null comment '菜品id',
    name       varchar(32)    null comment '菜品名称 （冗余字段）',
    price      decimal(10, 2) null comment '菜品原价（冗余字段）',
    copies     int            null comment '份数'
)
    comment '套餐菜品关系' collate = utf8_bin;

create table shopping_cart
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(50)    null comment '名称',
    image       varchar(100)   null comment '图片',
    user_id     bigint         not null comment '主键',
    dish_id     bigint         null comment '菜品id',
    setmeal_id  bigint         null comment '套餐id',
    dish_flavor varchar(50)    null comment '口味',
    number      int default 1  not null comment '数量',
    amount      decimal(10, 2) not null comment '金额',
    create_time datetime       null comment '创建时间'
)
    comment '购物车' collate = utf8_bin;

create table user
(
    id          bigint auto_increment comment '主键'
        primary key,
    openid      varchar(45)  null comment '微信用户唯一标识',
    name        varchar(50)  null comment '姓名',
    phone       varchar(100) null comment '手机号',
    sex         varchar(2)   null comment '性别',
    id_number   varchar(18)  null comment '身份证号',
    avatar      varchar(500) null comment '头像',
    create_time date         null
)
    comment '用户信息' collate = utf8_bin;


insert into employee (id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) values (1, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13812312312', '1', '110101199001010047', 1, '2022-02-15 15:51:20', '2022-02-17 09:16:20', 10, 1);

insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (11, 1, '酒水饮料', 10, 1, '2022-06-09 22:09:18', '2022-06-09 22:09:18', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (12, 1, '传统主食', 9, 1, '2022-06-09 22:09:32', '2022-06-09 22:18:53', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (13, 2, '人气套餐', 12, 1, '2022-06-09 22:11:38', '2022-06-10 11:04:40', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (15, 2, '商务套餐', 13, 1, '2022-06-09 22:14:10', '2022-06-10 11:04:48', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (16, 1, '蜀味烤鱼', 4, 1, '2022-06-09 22:15:37', '2022-06-09 22:15:37', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (17, 1, '蜀味牛蛙', 5, 1, '2022-06-09 22:16:14', '2022-06-09 22:16:42', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (18, 1, '特色蒸菜', 6, 1, '2022-06-09 22:17:42', '2022-06-09 22:17:42', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (19, 1, '新鲜时蔬', 7, 1, '2022-06-09 22:18:12', '2022-06-09 22:18:28', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (20, 1, '水煮鱼', 8, 1, '2022-06-09 22:22:29', '2022-06-09 22:23:45', 1, 1);
insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) values (21, 1, '汤类', 11, 1, '2022-06-10 10:51:47', '2022-06-10 10:51:47', 1, 1);

insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (46, '王老吉', 11, 6.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png', '', 1, '2022-06-09 22:40:47', '2022-06-09 22:40:47', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (47, '北冰洋', 11, 4.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/4451d4be-89a2-4939-9c69-3a87151cb979.png', '还是小时候的味道', 1, '2022-06-10 09:18:49', '2022-06-10 09:18:49', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (48, '雪花啤酒', 11, 4.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/bf8cbfc1-04d2-40e8-9826-061ee41ab87c.png', '', 1, '2022-06-10 09:22:54', '2022-06-10 09:22:54', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (49, '米饭', 12, 2.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/76752350-2121-44d2-b477-10791c23a8ec.png', '精选五常大米', 1, '2022-06-10 09:30:17', '2022-06-10 09:30:17', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (50, '馒头', 12, 1.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/475cc599-8661-4899-8f9e-121dd8ef7d02.png', '优质面粉', 1, '2022-06-10 09:34:28', '2022-06-10 09:34:28', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (51, '老坛酸菜鱼', 20, 56.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/4a9cefba-6a74-467e-9fde-6e687ea725d7.png', '原料：汤，草鱼，酸菜', 1, '2022-06-10 09:40:51', '2022-06-10 09:40:51', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (52, '经典酸菜鮰鱼', 20, 66.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/5260ff39-986c-4a97-8850-2ec8c7583efc.png', '原料：酸菜，江团，鮰鱼', 1, '2022-06-10 09:46:02', '2022-06-10 09:46:02', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (53, '蜀味水煮草鱼', 20, 38.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/a6953d5a-4c18-4b30-9319-4926ee77261f.png', '原料：草鱼，汤', 1, '2022-06-10 09:48:37', '2022-06-10 09:48:37', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (54, '清炒小油菜', 19, 18.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/3613d38e-5614-41c2-90ed-ff175bf50716.png', '原料：小油菜', 1, '2022-06-10 09:51:46', '2022-06-10 09:51:46', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (55, '蒜蓉娃娃菜', 19, 18.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/4879ed66-3860-4b28-ba14-306ac025fdec.png', '原料：蒜，娃娃菜', 1, '2022-06-10 09:53:37', '2022-06-10 09:53:37', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (56, '清炒西兰花', 19, 18.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/e9ec4ba4-4b22-4fc8-9be0-4946e6aeb937.png', '原料：西兰花', 1, '2022-06-10 09:55:44', '2022-06-10 09:55:44', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (57, '炝炒圆白菜', 19, 18.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/22f59feb-0d44-430e-a6cd-6a49f27453ca.png', '原料：圆白菜', 1, '2022-06-10 09:58:35', '2022-06-10 09:58:35', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (58, '清蒸鲈鱼', 18, 98.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/c18b5c67-3b71-466c-a75a-e63c6449f21c.png', '原料：鲈鱼', 1, '2022-06-10 10:12:28', '2022-06-10 10:12:28', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (59, '东坡肘子', 18, 138.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/a80a4b8c-c93e-4f43-ac8a-856b0d5cc451.png', '原料：猪肘棒', 1, '2022-06-10 10:24:03', '2022-06-10 10:24:03', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (60, '梅菜扣肉', 18, 58.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/6080b118-e30a-4577-aab4-45042e3f88be.png', '原料：猪肉，梅菜', 1, '2022-06-10 10:26:03', '2022-06-10 10:26:03', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (61, '剁椒鱼头', 18, 66.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/13da832f-ef2c-484d-8370-5934a1045a06.png', '原料：鲢鱼，剁椒', 1, '2022-06-10 10:28:54', '2022-06-10 10:28:54', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (62, '金汤酸菜牛蛙', 17, 88.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/7694a5d8-7938-4e9d-8b9e-2075983a2e38.png', '原料：鲜活牛蛙，酸菜', 1, '2022-06-10 10:33:05', '2022-06-10 10:33:05', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (63, '香锅牛蛙', 17, 88.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/f5ac8455-4793-450c-97ba-173795c34626.png', '配料：鲜活牛蛙，莲藕，青笋', 1, '2022-06-10 10:35:40', '2022-06-10 10:35:40', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (64, '馋嘴牛蛙', 17, 88.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/7a55b845-1f2b-41fa-9486-76d187ee9ee1.png', '配料：鲜活牛蛙，丝瓜，黄豆芽', 1, '2022-06-10 10:37:52', '2022-06-10 10:37:52', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (65, '草鱼2斤', 16, 68.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/b544d3ba-a1ae-4d20-a860-81cb5dec9e03.png', '原料：草鱼，黄豆芽，莲藕', 1, '2022-06-10 10:41:08', '2022-06-10 10:41:08', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (66, '江团鱼2斤', 16, 119.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/a101a1e9-8f8b-47b2-afa4-1abd47ea0a87.png', '配料：江团鱼，黄豆芽，莲藕', 1, '2022-06-10 10:42:42', '2022-06-10 10:42:42', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (67, '鮰鱼2斤', 16, 72.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/8cfcc576-4b66-4a09-ac68-ad5b273c2590.png', '原料：鮰鱼，黄豆芽，莲藕', 1, '2022-06-10 10:43:56', '2022-06-10 10:43:56', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (68, '鸡蛋汤', 21, 4.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/c09a0ee8-9d19-428d-81b9-746221824113.png', '配料：鸡蛋，紫菜', 1, '2022-06-10 10:54:25', '2022-06-10 10:54:25', 1, 1);
insert into dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) values (69, '平菇豆腐汤', 21, 6.00, 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/16d0a3d6-2253-4cfc-9b49-bf7bd9eb2ad2.png', '配料：豆腐，平菇', 1, '2022-06-10 10:55:02', '2022-06-10 10:55:02', 1, 1);

insert into dish_flavor (id, dish_id, name, value) values (40, 10, '甜味', '["无糖","少糖","半糖","多糖","全糖"]');
insert into dish_flavor (id, dish_id, name, value) values (41, 7, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (42, 7, '温度', '["热饮","常温","去冰","少冰","多冰"]');
insert into dish_flavor (id, dish_id, name, value) values (45, 6, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (46, 6, '辣度', '["不辣","微辣","中辣","重辣"]');
insert into dish_flavor (id, dish_id, name, value) values (47, 5, '辣度', '["不辣","微辣","中辣","重辣"]');
insert into dish_flavor (id, dish_id, name, value) values (48, 5, '甜味', '["无糖","少糖","半糖","多糖","全糖"]');
insert into dish_flavor (id, dish_id, name, value) values (49, 2, '甜味', '["无糖","少糖","半糖","多糖","全糖"]');
insert into dish_flavor (id, dish_id, name, value) values (50, 4, '甜味', '["无糖","少糖","半糖","多糖","全糖"]');
insert into dish_flavor (id, dish_id, name, value) values (51, 3, '甜味', '["无糖","少糖","半糖","多糖","全糖"]');
insert into dish_flavor (id, dish_id, name, value) values (52, 3, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (86, 52, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (87, 52, '辣度', '["不辣","微辣","中辣","重辣"]');
insert into dish_flavor (id, dish_id, name, value) values (88, 51, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (89, 51, '辣度', '["不辣","微辣","中辣","重辣"]');
insert into dish_flavor (id, dish_id, name, value) values (92, 53, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (93, 53, '辣度', '["不辣","微辣","中辣","重辣"]');
insert into dish_flavor (id, dish_id, name, value) values (94, 54, '忌口', '["不要葱","不要蒜","不要香菜"]');
insert into dish_flavor (id, dish_id, name, value) values (95, 56, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (96, 57, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (97, 60, '忌口', '["不要葱","不要蒜","不要香菜","不要辣"]');
insert into dish_flavor (id, dish_id, name, value) values (101, 66, '辣度', '["不辣","微辣","中辣","重辣"]');
insert into dish_flavor (id, dish_id, name, value) values (102, 67, '辣度', '["不辣","微辣","中辣","重辣"]');
insert into dish_flavor (id, dish_id, name, value) values (103, 65, '辣度', '["不辣","微辣","中辣","重辣"]');

insert into setmeal (id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user) values (29, 15, '商务套餐A', 20.00, 1, '', 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/21a5ed3a-97f6-447a-af9d-53deabfb5661.png', '2022-06-10 10:58:09', '2022-06-10 10:58:09', 1, 1);
insert into setmeal (id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user) values (30, 15, '商务套餐B', 22.00, 1, '', 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/8d0075f8-9008-4390-94ca-2ca631440304.png', '2022-06-10 11:00:13', '2022-06-10 11:11:37', 1, 1);
insert into setmeal (id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user) values (31, 15, '商务套餐C', 24.00, 1, '', 'https://reggie-itcast.oss-cn-beijing.aliyuncs.com/8979566b-0e17-462b-81d8-8dbace4138f4.png', '2022-06-10 11:11:23', '2022-06-10 11:11:23', 1, 1);

insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (32, '29', '54', '清炒小油菜', 18.00, 1);
insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (33, '29', '49', '米饭', 2.00, 1);
insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (34, '29', '68', '鸡蛋汤', 4.00, 1);
insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (38, '31', '56', '清炒西兰花', 18.00, 1);
insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (39, '31', '49', '米饭', 2.00, 1);
insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (40, '31', '68', '鸡蛋汤', 4.00, 1);
insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (41, '30', '69', '平菇豆腐汤', 6.00, 1);
insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (42, '30', '49', '米饭', 2.00, 1);
insert into setmeal_dish (id, setmeal_id, dish_id, name, price, copies) values (43, '30', '55', '蒜蓉娃娃菜', 18.00, 1);
