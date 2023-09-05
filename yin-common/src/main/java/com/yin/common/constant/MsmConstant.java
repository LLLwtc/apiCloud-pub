package com.yin.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * tx短信服务配置
 */
//实现了InitializingBean接口，当spring进行初始化bean时，会执行afterPropertiesSet方法
@Component
//public class MsmConstantUtils implements InitializingBean {
public class MsmConstant {
    @Value("${tencent.msm.secretId}")
    private String secretID;

    @Value("${tencent.msm.secretKey}")
    private String secretKey;

    @Value("${tencent.msm.endPoint}")
    private String endPoint;

    @Value("${tencent.msm.region}")
    private String region;

    @Value("${tencent.msm.appId}")
    private String appId;

    @Value("${tencent.msm.signName}")
    private String signName;

    @Value("${tencent.msm.templateId}")
    private String templateId;

    @Value("${tencent.msm.phone}")
    private String phone;

    //六个相关的参数
    public static String SECRET_ID = xxx;
    public static String SECRET_KEY = xxx;
    public static String REGION = xxx;
    public static String END_POINT = xxx;
    public static String APP_ID = xxx;
    public static String SIGN_NAME = xxx;
    public static String TEMPLATE_ID = xxx;//短信模板
    public static String PHONE = xxx;

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        SECRET_ID = secretID;
//        SECRET_KEY = secretKey;
//        REGION=region;
//        END_POINT = endPoint;
//        APP_ID = appId;
//        SIGN_NAME = signName;
//        TEMPLATE_ID = templateId;
//        PHONE=phone;
//    }
}

