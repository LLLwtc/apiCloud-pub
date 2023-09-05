package com.yin.Interface.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* 消息体
*/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message implements Serializable {

private static final long serialVersionUID = -8662036678998114010L;

    Long interfaceInfoId;

    Long userId;
}