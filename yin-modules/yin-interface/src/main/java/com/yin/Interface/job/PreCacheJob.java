package com.yin.Interface.job;

import cn.hutool.core.bean.BeanUtil;
import com.yin.Interface.service.UserInterfaceInfoService;
import com.yin.api.Interface.model.entity.UserInterfaceInfo;
import com.yin.common.common.ErrorCode;
import com.yin.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yin.common.constant.CommonConstant.*;


/**
 * 定时更新每个用户的调用次数，（同一时间只能一个服务器执行）
 *
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 每隔5分钟执行，把缓存中的数据更新到数据库（每个用户剩余调用次数,总调用次数）
     */
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Async
    @PostConstruct //启动项目先执行
    public void initFromRedis() {
        //从数据库中查到每个InterfaceInfoId+UserId；
        //拿锁
        RLock lock = redissonClient.getLock(LEFT_NUM_AND_TOTAL_NUM);
        boolean isLock = false;
        try {
            isLock = lock.tryLock(1, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("定时任务拿锁失败");
        }
        //拿锁失败
        if (!isLock) {
            log.error("定时任务拿锁失败");
            throw new BusinessException(ErrorCode.REPEAT_CALL_FIAL, "定时任务拿锁失败");
        }
        try {
            //拿锁成功，执行操作
//            Thread.sleep(300000);//睡眠5分钟，（模拟该线程拿到锁，其他线程无法拿到锁）
            log.info("--------------------------->" + "定时任务开始");
            userInterfaceInfoService.list().stream().map(info -> {
                String KEY1 = UPDATE_LEFT_NUM + FENHAO + info.getInterfaceInfoId() + XIAHUAXIAN + info.getUserId();
                String KEY2 = UPDATE_TOTAL_NUM + FENHAO + info.getInterfaceInfoId() + XIAHUAXIAN + info.getUserId();
                //从缓存中对应拿新数据
                String leftNum = stringRedisTemplate.opsForValue().get(KEY1);
                String totalNum = stringRedisTemplate.opsForValue().get(KEY2);
                UserInterfaceInfo newInfo = new UserInterfaceInfo();
                BeanUtil.copyProperties(info, newInfo);
                //更新到数据库
                if (leftNum != null && totalNum != null) {
                    newInfo.setLeftNum(Integer.valueOf(leftNum));
                    newInfo.setTotalNum(Integer.valueOf(totalNum));
                    userInterfaceInfoService.updateById(newInfo);
                }
                log.info("--------------------------->" + "定时任务结束");
                return null;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("定时任务执行出错");
        } finally {
            //释放锁
            lock.unlock();
        }
    }
}

// 项目启动，把数据库中的数据更新到缓存（用户,接口信息）
