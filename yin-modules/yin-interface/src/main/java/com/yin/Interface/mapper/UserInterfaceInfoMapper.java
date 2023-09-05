package com.yin.Interface.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yin.api.Interface.model.entity.UserInterfaceInfo;

import java.util.List;

/**
 * 用户接口信息 Mapper
 *
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




