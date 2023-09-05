package com.yin.api.Interface.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 接口id
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;
}