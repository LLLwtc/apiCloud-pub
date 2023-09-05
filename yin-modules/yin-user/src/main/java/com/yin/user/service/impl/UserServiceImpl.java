package com.yin.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yin.api.user.model.entity.User;
import com.yin.common.common.ErrorCode;
import com.yin.common.exception.BusinessException;
import com.yin.user.mapper.UserMapper;
import com.yin.user.mq.SMS;
import com.yin.user.mq.SMSProducer;
import com.yin.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.yin.api.user.model.enums.UserConstant.ADMIN_ROLE;
import static com.yin.api.user.model.enums.UserConstant.USER_LOGIN_STATE;
import static com.yin.common.constant.CommonConstant.*;


/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SMSProducer smsProducer;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "mima";

    /**
     * 使用账号密码注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    /**
     * 手机号，验证码注册
     *
     * @param userPhone
     * @return
     */
    @Override
    public Boolean userRegisterByPhone(String userPhone) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userPhone)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userPhone.length() != 11) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号长度不对");
        }
        //2. 生成短信验证码
        String randomNumbers = RandomUtil.randomNumbers(6);
        //3. 发送到消息队列
        smsProducer.sendMessage(new SMS(userPhone, randomNumbers, SMSCODE));

        return true;
    }

    /**
     * 注册验证码效验
     *
     * @param smsCode
     * @return
     */
    @Override
    public User checkSMSCode(String userPhone, String smsCode) {
        if (StringUtils.isAnyBlank(userPhone, smsCode) || userPhone.length() != 11 || smsCode.length() != 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号或验证码长度不对");
        }
        //从缓存中拿出验证码
        String trueSmsCode = stringRedisTemplate.opsForValue().get(SMSCODE + FENHAO + userPhone);
        if (trueSmsCode == null) {
            throw new BusinessException(ErrorCode.SMS_CODE_ERROR, "验证码异常");
        }
        //验证码正确，插入数据
        User user = new User();
        if (trueSmsCode.equals(smsCode)) {
            user.setUserPhone(userPhone);

            //设置默认账号，密码，用户角色,ak,sk
            String userAccount = RandomUtil.randomNumbers(8);
            String userPassword = DigestUtils.md5DigestAsHex((SALT + userAccount).getBytes());
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            user.setUserAccount(userAccount);
            user.setUserPassword(userPassword);
            user.setUserRole("user");
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);

            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "手机号注册失败，数据库错误");
            }
        }
        return user == null ? null : user;
    }

    /**
     * 手机号登录
     *
     * @param userPhone
     * @return
     */
    @Override
    public Boolean userLoginByPhone(String userPhone) {
        //controller层已经效验了参数，这里不用效验
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("userPhone", userPhone));
        // 用户不存在
        if (user == null) {
            log.info("用户不存在");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或手机号不存在");
        }

        //发送验证码
        String randomNumbers = RandomUtil.randomNumbers(6);

        smsProducer.sendMessage(new SMS(userPhone, randomNumbers, SMSLOGINCODE));

        return true;
    }

    /**
     * 效验登录验证码
     *
     * @param userPhone
     * @param smsCode
     * @param request
     * @return
     */
    @Override
    public long checkSMSLoginCode(String userPhone, String smsCode, HttpServletRequest request) {
        //controller已经效验过参数
        //从缓存中拿出验证码
        String trueSmsCode = stringRedisTemplate.opsForValue().get(SMSLOGINCODE + FENHAO + userPhone);
        if (trueSmsCode == null) {
            throw new BusinessException(ErrorCode.SMS_CODE_ERROR, "验证码异常");
        }
        if (!trueSmsCode.equals(smsCode)) {
            log.error("验证码错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("userPhone", userPhone));
        // 用户不存在
        if (user == null) {
            log.info("用户不存在");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或手机号不存在");
        }

        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user.getId();
    }

    /**
     * 账号密码登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

}




