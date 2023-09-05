package com.yin.api.user.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体（手机号，验证码）
 *
 */
@Data
public class User2RegisterRequest implements Serializable {
    private static final long serialVersionUID = -6308934971286862602L;

    private String userPhone;
}
