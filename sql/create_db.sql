use my_api;


create table if not exists credit
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             null comment '创建用户Id',
    creditTotal bigint                             null default 0 comment '总积分',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
) comment '积分表' collate = utf8mb4_unicode_ci;




create table if not exists orders
(
    id            bigint auto_increment comment 'id' primary key comment '订单id',
    alipayTradeNo varchar(128)                       null comment '支付宝交易凭证id',
    `userId`      bigint                             NOT NULL COMMENT '用户id',
    subject       varchar(128)                       not null comment '交易名称',
    totalAmount   double                             not null comment '交易金额',
    tradeStatus   varchar(128)                       not null default 'unpaid ' comment 'unpaid,paying,succeed,failed',
    buyerId       varchar(64)                        null comment '支付宝买家id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
    ) comment '充值订单表' collate = utf8mb4_unicode_ci;