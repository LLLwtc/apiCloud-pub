package com.yin.Interface.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yin.api.Interface.model.entity.UserInterfaceInfo;


/**
 * 用户接口信息服务
 *
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    void invokeCount(long interfaceInfoId, long userId);

    /**
     * 是否还有调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean hasLeftNum(long interfaceInfoId, long userId);
}
