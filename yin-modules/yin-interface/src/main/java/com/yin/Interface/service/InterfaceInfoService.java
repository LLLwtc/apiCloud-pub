package com.yin.Interface.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yin.api.Interface.model.entity.InterfaceInfo;


/**
 * 接口信息服务
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
