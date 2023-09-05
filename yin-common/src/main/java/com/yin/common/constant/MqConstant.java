package com.yin.common.constant;

public interface MqConstant {

    /**
     * api
     */
    String API_EXCHANGE_NAME = "api_exchange";

    String API_QUEUE_NAME = "api_queue";

    String API_ROUTING_KEY_NAME = "api_routingKey";

    /**
     * api死信
     */
    String API_DEAD_EXCHANGE_NAME = "api_dead_exchange";

    String API_DEAD_QUEUE_NAME = "api_dead_queue";

    String API_DEAD_ROUTING_KEY = "api_dead_routingKey";

    /**
     * 短信
     */
    String SMS_EXCHANGE_NAME = "sms_exchange";

    String SMS_QUEUE_NAME = "sms_queue";

    String SMS_ROUTING_KEY_NAME = "sms_routingKey";

    /**
     * 短信死信
     */
    String SMS_DEAD_EXCHANGE_NAME = "sms_dead_exchange";

    String SMS_DEAD_QUEUE_NAME = "sms_dead_queue";

    String SMS_DEAD_ROUTING_KEY = "sms_dead_routingKey";

    /**
     * 订单支付
     */
    String ORDERS_EXCHANGE_NAME = "orders_exchange";

    String ORDERS_QUEUE_NAME = "orders_queue";

    String ORDERS_ROUTING_KEY = "orders_routingKey";

    /**
     * 订单支付死信
     */
    String ORDERS_DEAD_EXCHANGE_NAME = "orders_dead_exchange";

    String ORDERS_DEAD_QUEUE_NAME = "orders_dead_queue";

    String ORDERS_DEAD_ROUTING_KEY = "orders_dead_routingKey";
}
