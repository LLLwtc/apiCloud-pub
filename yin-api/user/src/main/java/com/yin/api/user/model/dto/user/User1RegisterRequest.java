package com.yin.api.user.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体（用户账号，密码）
 *
 */
@Data
public class User1RegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
