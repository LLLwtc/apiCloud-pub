package com.yin.user.mq;

import com.yin.common.constant.MqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class SMSProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送消息
     *
     * @param sms
     */
    public void sendMessage(SMS sms) {
        rabbitTemplate.convertAndSend(MqConstant.SMS_EXCHANGE_NAME, MqConstant.SMS_ROUTING_KEY_NAME, sms);
    }

}
