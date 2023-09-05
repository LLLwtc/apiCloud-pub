package com.yin.api.user.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class checkSMSCodeRequest implements Serializable {

    private static final long serialVersionUID = -2460651472416834160L;
    private String userPhone;
    private String smsCode;
}
