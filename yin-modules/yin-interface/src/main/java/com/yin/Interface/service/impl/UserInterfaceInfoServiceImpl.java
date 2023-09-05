package com.yin.Interface.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yin.Interface.mapper.UserInterfaceInfoMapper;
import com.yin.Interface.service.UserInterfaceInfoService;
import com.yin.api.Interface.model.entity.UserInterfaceInfo;
import com.yin.common.common.ErrorCode;
import com.yin.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.yin.common.constant.CommonConstant.*;


/**
 * 用户接口信息服务实现类
 *
 */
@Service
@Slf4j
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }

    /**
     * 统计接口总调用次数，剩余调用次数（放入消息队列，快速返回结果，避免用户等待，使用消息队列解耦
     * 此方法不再调用
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public void invokeCount(long interfaceInfoId, long userId) {
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
        this.update(updateWrapper);
        UserInterfaceInfo info = this.getOne(updateWrapper);

        //放入缓存
        stringRedisTemplate.opsForValue().set(KEY1, info.getLeftNum().toString());
        stringRedisTemplate.opsForValue().set(KEY2, info.getTotalNum().toString());
    }

    /**
     * 是否还有剩余调用次数
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public boolean hasLeftNum(long interfaceInfoId, long userId) {
        // 判断
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查缓存
        String KEY = UPDATE_LEFT_NUM + FENHAO + interfaceInfoId + XIAHUAXIAN + userId;
        String invokeNum = stringRedisTemplate.opsForValue().get(KEY);

        if (invokeNum != null) {
            Long temp = Long.valueOf(invokeNum);
            return temp <= 0 ? false : true;
        }
        //查数据库
        //加锁，场景：（后续重构成分布式，并发）用户没有调用次数了，却瞬间调用（十万次。。。）
        //此处不用加锁，因为调用接口的时候时候已经加锁了
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        queryWrapper.gt("leftNum", 0);

        UserInterfaceInfo info = this.getOne(queryWrapper);

        //设置到缓存
        stringRedisTemplate.opsForValue().set(KEY, String.valueOf(info.getLeftNum()));

        return this.count(queryWrapper) > 0;
    }

}


