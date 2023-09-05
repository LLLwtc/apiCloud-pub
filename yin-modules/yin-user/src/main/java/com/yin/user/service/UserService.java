package com.yin.user.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yin.api.user.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     *使用手机号注册
     * @param userPhone
     * @return 新用户id
     */
    Boolean userRegisterByPhone(String userPhone);

    /**
     * 验证码效验（注册）
     * @param smsCode
     * @return
     */
    User checkSMSCode(String phone,String smsCode);

    /**
     * 手机号登录
     * @param userPhone
     * @return
     */
    Boolean userLoginByPhone(String userPhone);

    /**
     * 验证码效验（登录）
     * @param userPhone
     * @param smsCode
     * @param request
     * @return
     */
    long checkSMSLoginCode(String userPhone, String smsCode,HttpServletRequest request);
}
