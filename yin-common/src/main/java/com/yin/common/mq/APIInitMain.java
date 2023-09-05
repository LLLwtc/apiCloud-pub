package com.yin.common.mq;

import com.yin.common.constant.MqConstant;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@AutoConfiguration
public class APIInitMain {
    /**
     * 短信死信队列和交换机声明
     */
    @Bean
    Queue SMSDeadQueue() {
        return QueueBuilder.durable(MqConstant.SMS_DEAD_QUEUE_NAME).build();
    }

    @Bean
    DirectExchange SMSDeadExchange() {
        return new DirectExchange(MqConstant.SMS_DEAD_EXCHANGE_NAME);
    }

    @Bean
    Binding SMSDeadBinding(Queue SMSDeadQueue, DirectExchange SMSDeadExchange) {
        return BindingBuilder.bind(SMSDeadQueue).to(SMSDeadExchange).with(MqConstant.SMS_DEAD_ROUTING_KEY);
    }

    /**
     * 短信队列和交换机声明
     */
    @Bean
    Queue SMSQueue() {
        //信息参数 设置TTL为1min
        Map<String, Object> arg = new HashMap<>();
        arg.put("x-message-ttl", 1000 * 60);
        //绑定死信交换机
        arg.put("x-dead-letter-exchange", MqConstant.SMS_DEAD_EXCHANGE_NAME);
        arg.put("x-dead-letter-routing-key", MqConstant.SMS_DEAD_ROUTING_KEY);
        return QueueBuilder.durable(MqConstant.SMS_QUEUE_NAME).withArguments(arg).build();
    }

    @Bean
    DirectExchange SMSExchange() {
        return new DirectExchange(MqConstant.SMS_EXCHANGE_NAME);
    }

    @Bean
    Binding SMSBinding(Queue SMSQueue, DirectExchange SMSExchange) {
        return BindingBuilder.bind(SMSQueue).to(SMSExchange).with(MqConstant.SMS_ROUTING_KEY_NAME);
    }

    /**
     * api死信队列和交换机声明
     */
    @Bean
    Queue APIDeadQueue() {
        return QueueBuilder.durable(MqConstant.API_DEAD_QUEUE_NAME).build();
    }

    @Bean
    DirectExchange APIDeadExchange() {
        return new DirectExchange(MqConstant.API_DEAD_EXCHANGE_NAME);
    }


    @Bean
    Binding APIDeadBinding(Queue APIDeadQueue, DirectExchange APIDeadExchange) {
        return BindingBuilder.bind(APIDeadQueue).to(APIDeadExchange).with(MqConstant.API_DEAD_ROUTING_KEY);
    }
    /**
     * api队列和交换机声明
     */
    @Bean
    Queue APIQueue() {
        //信息参数 设置TTL为1min
        Map<String, Object> arg = new HashMap<>();
        arg.put("x-message-ttl", 1000 * 60);
        //绑定死信交换机
        arg.put("x-dead-letter-exchange", MqConstant.API_DEAD_EXCHANGE_NAME);
        arg.put("x-dead-letter-routing-key", MqConstant.API_DEAD_ROUTING_KEY);
        return QueueBuilder.durable(MqConstant.API_QUEUE_NAME).withArguments(arg).build();
    }

    @Bean
    DirectExchange APIExchange() {
        return new DirectExchange(MqConstant.API_EXCHANGE_NAME);
    }

    @Bean
    Binding APIBinding(Queue APIQueue, DirectExchange APIExchange) {
        return BindingBuilder.bind(APIQueue).to(APIExchange).with(MqConstant.API_ROUTING_KEY_NAME);
    }
//    public static void main(String[] args) {
//        try {
//            ConnectionFactory factory = new ConnectionFactory();
//            factory.setHost("localhost");
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//            String APIEXCHANGE = MqConstant.API_EXCHANGE;
//            String SMSEXCHANGE = MqConstant.SMS_EXCHANGE;
//            channel.exchangeDeclare(APIEXCHANGE, "direct");
//            channel.exchangeDeclare(SMSEXCHANGE, "direct");
//
//            // 创建队列，随机分配一个队列名称
//            String APIQUEUEName = MqConstant.API_QUEUE;
//            String SMSQUEUEName = MqConstant.SMS_QUEUE;
//            channel.queueDeclare(APIQUEUEName, true, false, false, null);
//            channel.queueDeclare(SMSQUEUEName, true, false, false, null);
//            channel.queueBind(APIQUEUEName, APIEXCHANGE,  MqConstant.API_ROUTING_KEY);
//            channel.queueBind(SMSQUEUEName, SMSEXCHANGE,  MqConstant.SMS_ROUTING_KEY);
//        } catch (Exception e) {
//
//        }
//        System.out.println("执行完啦");
//    }
}
