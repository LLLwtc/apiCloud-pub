package com.yin.api.Interface.service;

/**
 * 内部用户接口信息服务
 *
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 统计接口调用总次数，剩余次数
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
