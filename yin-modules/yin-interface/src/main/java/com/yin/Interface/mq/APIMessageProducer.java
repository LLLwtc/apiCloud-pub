package com.yin.Interface.mq;

import com.yin.common.constant.MqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class APIMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(Message message){
        rabbitTemplate.convertAndSend(MqConstant.API_EXCHANGE, MqConstant.API_ROUTING_KEY, message);
    }

//    public void afterPropertiesSet() {
//        //使用JSON序列化
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//    }
}