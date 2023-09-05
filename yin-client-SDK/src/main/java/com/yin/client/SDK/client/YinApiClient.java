package com.yin.client.SDK.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yin.client.SDK.model.User;

import java.util.HashMap;
import java.util.Map;

import static com.yin.common.util.SignUtils.genSign;


/**
 * 调用第三方接口的客户端
 *
 */
public class YinApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";


    private String accessKey;

    private String secretKey;

    public YinApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
//        String result = HttpUtil.get("http://localhost:8123" + "/api/name/get", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/post", paramMap);
        System.out.println(result);
        return result;
    }

    /**
     * 生成请求头
     * @param body
     * @return
     */
    private Map<String, String> setHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        String nonce=RandomUtil.randomNumbers(5);
        String timestamp=String.valueOf(System.currentTimeMillis() / 1000);
        String sign=genSign(body, secretKey);

        hashMap.put("nonce", nonce);//随机数
        hashMap.put("body", body);//请求参数
        hashMap.put("timestamp", timestamp);//时间戳
        hashMap.put("sign", sign);//签名
        return hashMap;
    }

    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
//        HttpResponse httpResponse = HttpRequest.post("http://localhost:8123" + "/api/name/user")
                .addHeaders(setHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());//状态码
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
}
