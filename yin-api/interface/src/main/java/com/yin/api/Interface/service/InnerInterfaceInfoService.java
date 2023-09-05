package com.yin.api.Interface.service;


/**
 * 内部接口信息服务
 *
 */
public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    Long getInterfaceInfo(String path, String method);
}
