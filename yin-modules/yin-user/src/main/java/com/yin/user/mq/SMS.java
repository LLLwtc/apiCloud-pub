package com.yin.user.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SMS implements Serializable {

    private static final long serialVersionUID = -4193260561073779715L;

    String userPhone;

    String randomNumbers;

    /**
     * 缓存的key
     */
    String key;
}
