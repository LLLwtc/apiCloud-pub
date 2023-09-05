package com.yin.common.common;

/**
 * 错误码
 */
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    LOCK_FIAL(50002,"下线接口时候拿锁失败"),
    REPEAT_CALL_FIAL(50003,"不允许短时间内重复调用!"),
    SMS_CODE_ERROR(50004,"验证码异常!"),
    TOO_MANY_REQUEST(42900, "请求过于频繁"),
    PHONE_ALREADY_USE(42900, "手机号已注册"),
    ;

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
