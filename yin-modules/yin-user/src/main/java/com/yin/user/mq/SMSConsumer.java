package com.yin.user.mq;

import com.rabbitmq.client.Channel;
import com.yin.common.common.ErrorCode;
import com.yin.common.constant.MqConstant;
import com.yin.common.exception.BusinessException;
import com.yin.common.util.SendSms;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;

import static com.yin.common.constant.CommonConstant.FENHAO;


@Component
@Slf4j
public class SMSConsumer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {MqConstant.SMS_QUEUE_NAME})
    public void receiveMessage(SMS sms, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.warn("接收到普通队列信息，receiveMessage={}=======================================", sms);
        String userPhone = sms.getUserPhone();
        String randomNumbers = sms.getRandomNumbers();
        String key = sms.getKey();
        if (StringUtils.isAnyBlank(userPhone, randomNumbers, key)) {
            //消息为空，消息拒绝，不重复发送，不重新放入队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        //发送验证码
        SendSms.sendMessageUsePhone(userPhone, randomNumbers);

        //3. 验证码放入缓存,5分钟内有效
        stringRedisTemplate.opsForValue().set(key + FENHAO + userPhone, randomNumbers, Duration.ofMinutes(5));

        //消息确认
        channel.basicAck(deliveryTag, false);
    }
}
