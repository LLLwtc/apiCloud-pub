package com.yin.common.util;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import com.yin.common.constant.MsmConstant;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送短信工具类
 */
@Slf4j
public class SendSms {
    public static void main(String[] args) {
        sendMessage("成功");
    }

    /**
     * 短信内容：生成图片{message}！！
     * @param message
     */
    public static void sendMessage(String message) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(MsmConstant.SECRET_ID, MsmConstant.SECRET_KEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(MsmConstant.END_POINT);
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, MsmConstant.REGION, clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {MsmConstant.PHONE};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppId(MsmConstant.APP_ID);
            req.setSignName(MsmConstant.SIGN_NAME);
            req.setTemplateId(MsmConstant.TEMPLATE_ID);

            String[] templateParamSet = {message};
            req.setTemplateParamSet(templateParamSet);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            log.error("发送短信失败",e.toString());
        }
    }

    /**
     * 短信内容：验证码为：{message}，您正在登录，若非本人操作，请勿泄露。
     * @param phone
     * @param message
     */
    public static void sendMessageUsePhone(String phone,String message) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(MsmConstant.SECRET_ID, MsmConstant.SECRET_KEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(MsmConstant.END_POINT);
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, MsmConstant.REGION, clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
//            String[] phoneNumberSet1 = {MsmConstant.PHONE};
            String[] phoneNumberSet1 = {"86"+phone};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppId(MsmConstant.APP_ID);
            req.setSignName(MsmConstant.SIGN_NAME);
            req.setTemplateId(MsmConstant.TEMPLATE_ID);

            String[] templateParamSet = {message};
            req.setTemplateParamSet(templateParamSet);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            log.error("发送短信失败",e.toString());
        }
    }
}
