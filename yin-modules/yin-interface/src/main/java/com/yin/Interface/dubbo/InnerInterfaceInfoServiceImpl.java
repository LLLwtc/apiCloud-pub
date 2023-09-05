package com.yin.Interface.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yin.Interface.mapper.InterfaceInfoMapper;
import com.yin.api.Interface.model.entity.InterfaceInfo;
import com.yin.api.Interface.service.InnerInterfaceInfoService;
import com.yin.common.common.ErrorCode;
import com.yin.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

import static com.yin.common.constant.CommonConstant.*;

/**
 * 内部接口服务实现类
 *
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private RedisTemplate<String, InterfaceInfo> redisTemplate;

    /**
     * 获取调用的接口信息
     *
     * @param url
     * @param method
     * @return
     */
    @Override
    public Long getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1. 查缓存
        String KEY = INTERFACEINFO + FENHAO + XIAHUAXIAN + url;
        InterfaceInfo interfaceInfo = (InterfaceInfo) redisTemplate.opsForValue().get(KEY);
        //2. 缓存没有，查数据库
        if (interfaceInfo == null) {
            QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("url", url);
            queryWrapper.eq("method", method);
            interfaceInfo = interfaceInfoMapper.selectOne(queryWrapper);
            if (interfaceInfo == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口缓存信息为空");
            }
            //保存到缓存
            redisTemplate.opsForValue().set(KEY, interfaceInfo);
        }

        return interfaceInfo.getId();
    }
}
