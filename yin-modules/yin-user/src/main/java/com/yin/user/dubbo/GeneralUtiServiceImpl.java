package com.yin.user.dubbo;


import com.yin.api.user.model.entity.User;
import com.yin.api.user.service.GeneralUtiService;
import com.yin.api.user.service.InnerUserService;
import com.yin.common.util.SignUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.Map;

import static com.yin.common.constant.CommonConstant.*;


@DubboService
public class GeneralUtiServiceImpl implements GeneralUtiService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, User> redisTemplate;

    @Resource
    private InnerUserService innerUserService;

    /**
     * 效验请求头
     *
     * @param hashMap
     */
    @Override
    public Long checkHeaderMapByHeaders(Map hashMap) {
        String accessKey = (String) hashMap.get("accessKey");
        String nonce = (String) hashMap.get("nonce");
        String timestamp = (String) hashMap.get("timestamp");
        String sign = (String) hashMap.get("sign");
        String body = (String) hashMap.get("body");

        // 1. 根据accessKey从缓存查用户
        String KEY=USER + FENHAO + accessKey;
        User invokeUser = (User) redisTemplate.opsForValue().get(KEY);
        if (invokeUser == null) {
            //2. 缓存没有数据，从数据库查
            invokeUser = innerUserService.getInvokeUser(accessKey);
            if (invokeUser == null) {
                throw new RuntimeException("用户不存在");
            }
            //保存到缓存
            redisTemplate.opsForValue().set(KEY,invokeUser);
        }
        //3. 从缓存获取随机数
        String timeStampStr = stringRedisTemplate.opsForValue().get(NONCE + FENHAO + timestamp + XIAHUAXIAN + nonce);

        //4. 随机数存在，则该请求是重放
        if (timeStampStr != null) {
            throw new RuntimeException("无权限");
        }
        // 5. 时间和当前时间不能超过 5 分钟
        if (Math.abs(Long.parseLong(timestamp) - System.currentTimeMillis() / 1000) > 5 * 60) {
            throw new RuntimeException("无权限");
        }

        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            throw new RuntimeException("无权限");
        }
        //放入缓存，防止请求重放
        stringRedisTemplate.opsForValue().set(NONCE + FENHAO + timestamp + XIAHUAXIAN + nonce, nonce, Duration.ofMinutes(5));

        return invokeUser.getId();
    }

}
