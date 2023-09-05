package com.yin.api.user.service;

import java.util.Map;

public interface GeneralUtiService {
    /**
     * 效验请求头（网关）
     *
     * @param hashMap
     */
    public Long checkHeaderMapByHeaders(Map hashMap);
}
