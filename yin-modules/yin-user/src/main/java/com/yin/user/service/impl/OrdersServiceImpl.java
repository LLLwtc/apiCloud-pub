package com.yin.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yin.api.user.model.entity.Orders;
import com.yin.user.mapper.OrdersMapper;
import com.yin.user.service.OrdersService;
import org.springframework.stereotype.Service;


@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService {

}




