package com.yin.Interface.mq;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.rabbitmq.client.Channel;
import com.yin.Interface.service.UserInterfaceInfoService;
import com.yin.api.Interface.model.entity.UserInterfaceInfo;
import com.yin.common.common.ErrorCode;
import com.yin.common.constant.MqConstant;
import com.yin.common.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yin.common.constant.CommonConstant.*;


@Component
@Slf4j
public class APIMessageDeadConsumer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {MqConstant.API_DEAD_QUEUE_NAME})
    public void receiveMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.warn("接收到死信队列信息，receiveMessage={}=======================================", message);
        if (message == null) {
            //消息为空，消息拒绝，不重复发送，不重新放入队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息队列：消息为空");
        }

        Long interfaceInfoId = message.getInterfaceInfoId();
        Long userId = message.getUserId();

        // 判断
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //从缓存查
        String KEY1 = UPDATE_LEFT_NUM + FENHAO + interfaceInfoId + XIAHUAXIAN + userId;
        String KEY2 = UPDATE_TOTAL_NUM + FENHAO + interfaceInfoId + XIAHUAXIAN + userId;

        String leftNum = stringRedisTemplate.opsForValue().get(KEY1);
        String totalNum = stringRedisTemplate.opsForValue().get(KEY2);

        if (leftNum != null && totalNum != null) {
            leftNum = String.valueOf(Long.valueOf(leftNum) - 1);
            totalNum = String.valueOf(Long.valueOf(totalNum) + 1);

            stringRedisTemplate.opsForValue().set(KEY1, leftNum);
            stringRedisTemplate.opsForValue().set(KEY2, totalNum);
            return;
        }
        //缓存没有，从数据库查
        //加锁，场景：（后续重构成分布式，并发）用户没有调用次数了，却瞬间调用（十万次。。。）
        //此处不用加锁，因为调用接口的时候时候已经加锁了
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);

        updateWrapper.gt("leftNum", 0);
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        userInterfaceInfoService.update(updateWrapper);
        UserInterfaceInfo info = userInterfaceInfoService.getOne(updateWrapper);

        //放入缓存
        stringRedisTemplate.opsForValue().set(KEY1, info.getLeftNum().toString());
        stringRedisTemplate.opsForValue().set(KEY2, info.getTotalNum().toString());

        // 消息确认
        channel.basicAck(deliveryTag, false);
    }
}