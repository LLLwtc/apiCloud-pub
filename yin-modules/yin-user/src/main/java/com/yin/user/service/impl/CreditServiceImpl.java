package com.yin.user.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yin.api.user.model.entity.Credit;
import com.yin.api.user.model.enums.CreditConstant;
import com.yin.common.common.ErrorCode;
import com.yin.common.exception.BusinessException;
import com.yin.common.util.ThrowUtils;
import com.yin.user.mapper.CreditMapper;
import com.yin.user.service.CreditService;
import org.springframework.stereotype.Service;


@Service
public class CreditServiceImpl extends ServiceImpl<CreditMapper, Credit>
    implements CreditService {
    /**
     * 根据 当前用户ID 获取积分总数
     * @param userId
     * @return
     */
    @Override
    public Long getCreditTotal(Long userId) {
        if (userId == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QueryWrapper<Credit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        Credit credit = this.getOne(queryWrapper);
        ThrowUtils.throwIf(credit == null, ErrorCode.NOT_FOUND_ERROR);
        return credit.getCreditTotal();
    }

    /**
     * 每日签到
     * @param userId
     * @return
     */
    @Override
    public Boolean signUser(Long userId) {
        if (userId == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        synchronized (userId.toString().intern()) {
            QueryWrapper<Credit> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            Credit credit = this.getOne(queryWrapper);
            ThrowUtils.throwIf(credit == null, ErrorCode.NOT_FOUND_ERROR);
            //判断今天是否已经签过
            if (DateUtil.isSameDay(credit.getUpdateTime(), new DateTime())) {
                return false;
            }
            Long creditTotal = credit.getCreditTotal() + CreditConstant.CREDIT_DAILY;
            credit.setCreditTotal(creditTotal);
            //保持更新时间
            credit.setUpdateTime(null);
            return this.updateById(credit);
        }
    }

    /**
     * 更新积分（内部方法） 正数为增加积分，负数为消耗积分
     * @param userId
     * @param credits
     * @return
     */
    @Override
    public Boolean updateCredits(Long userId, long credits) {
        if (userId == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QueryWrapper<Credit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        Credit credit = this.getOne(queryWrapper);
        ThrowUtils.throwIf(credit == null, ErrorCode.NOT_FOUND_ERROR);
        Long creditTotal = credit.getCreditTotal();
        //积分不足时
        if (creditTotal+credits<0) return false;
        creditTotal =creditTotal + credits;
        credit.setCreditTotal(creditTotal);
        //保持更新时间
        credit.setUpdateTime(null);
        return this.updateById(credit);
    }

}




