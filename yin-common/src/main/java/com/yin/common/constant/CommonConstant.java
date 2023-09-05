package com.yin.common.constant;

/**
 * 通用常量
 *
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    /**
     * 存剩余调用次数的KEY
     */
    public static final String UPDATE_LEFT_NUM="updateLeftNum";
    /**
     * 存总调用次数的KEY
     */
    public static final String UPDATE_TOTAL_NUM="updateTotalNum";

    /**
     * 定时任务的锁
     */
    public static final String LEFT_NUM_AND_TOTAL_NUM="LeftNumAndTotalNum";
    /**
     * 定时任务的锁
     */
    public static final String INIT_FROM_DB="initFromDB";

    /**
     * 请求头中的随机数KEY
     */
    public static final String NONCE = "nonce";
    /**
     * 请求头中的用户
     */
    public static final String USER = "user";
    /**
     * 请求头中的接口信息
     */
    public static final String INTERFACEINFO = "interfaceInfo";

    /**
     * 注册验证码KEY
     */
    public final static String SMSCODE="SMSCODE";
    /**
     * 登录验证码KEY
     */
    public final static String SMSLOGINCODE="SMSLOGINCODE";

    public static final String FENHAO=":";

    public static final String XIAHUAXIAN="_";
}
