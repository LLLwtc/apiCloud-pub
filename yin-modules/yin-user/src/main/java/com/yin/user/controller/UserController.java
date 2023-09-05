package com.yin.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.yin.api.user.model.dto.user.*;
import com.yin.api.user.model.entity.User;
import com.yin.api.user.model.vo.UserVO;
import com.yin.common.common.BaseResponse;
import com.yin.common.common.DeleteRequest;
import com.yin.common.common.ErrorCode;
import com.yin.common.common.ResultUtils;
import com.yin.common.exception.BusinessException;
import com.yin.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
//
//    @Resource
//    private RedisLimiterManager redisLimiterManager;
    // region 登录相关

    /**
     * 用户注册（账号，密码）
     *
     * @param user1RegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody User1RegisterRequest user1RegisterRequest) {
        if (user1RegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = user1RegisterRequest.getUserAccount();
        String userPassword = user1RegisterRequest.getUserPassword();
        String checkPassword = user1RegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户注册（手机号，短信）
     *
     * @param user2RegisterRequest
     * @return
     */
    @PostMapping("/registerByPhone")
    public BaseResponse<Boolean> userRegisterByPhone(@RequestBody User2RegisterRequest user2RegisterRequest) {
        if (user2RegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userPhone = user2RegisterRequest.getUserPhone();
        if (StringUtils.isAnyBlank(userPhone)) {
            return null;
        }
        long count = userService.count(Wrappers.<User>lambdaQuery().eq(User::getUserPhone, userPhone));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_USE);
        }
        //TODO 前端映射请求到网关端口8090，网关处做限流，路由（8090-》7529），这里就不用做限流
        // 限流判断，每个用户一个限流器
//        redisLimiterManager.doRateLimit("registerByPhone" + userPhone);
        Boolean result = userService.userRegisterByPhone(userPhone);

        return ResultUtils.success(result);
    }

    /**
     * 效验注册验证码
     *
     * @param smsCodeRequest
     * @return
     */
    @PostMapping("/checkSMSCode")
    public BaseResponse<User> checkSMSCode(@RequestBody checkSMSCodeRequest smsCodeRequest) {
        if (smsCodeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不能为空");
        }
        String smsCode = smsCodeRequest.getSmsCode();
        if (StringUtils.isAnyBlank(smsCode) || smsCode.length() != 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码长度错误");
        }
        User result = userService.checkSMSCode(smsCodeRequest.getUserPhone(), smsCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录（账号，密码）
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 手机号登录
     *
     * @param user2RegisterRequest
     * @return 验证码
     */
    @PostMapping("/loginByPhone")
    public BaseResponse<Boolean> userLoginByPhone(@RequestBody User2RegisterRequest user2RegisterRequest) {
        if (user2RegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userPhone = user2RegisterRequest.getUserPhone();
        if (StringUtils.isAnyBlank(userPhone) || userPhone.length() != 11) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //TODO 前端映射请求到网关端口8090，网关处做限流，路由（8090-》7529），这里就不用做限流
        //限流判断，每个用户一个限流器
//        redisLimiterManager.doRateLimit("LoginByPhone" + userPhone);

        Boolean result = userService.userLoginByPhone(userPhone);
        return ResultUtils.success(result);
    }

    //TODO 效验注册验证码和效验登录验证的controller层代码十分相似，后续采用xx设计模式重构

    /**
     * 效验登录验证码
     *
     * @param smsCodeRequest
     * @return
     */
    @PostMapping("/checkSMSLoginCode")
    public BaseResponse<Long> checkSMSLoginCode(@RequestBody checkSMSCodeRequest smsCodeRequest, HttpServletRequest request) {
        if (smsCodeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不能为空");
        }
        String smsCode = smsCodeRequest.getSmsCode();
        if (StringUtils.isAnyBlank(smsCode) || smsCode.length() != 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码长度错误");
        }
        long result = userService.checkSMSLoginCode(smsCodeRequest.getUserPhone(), smsCode, request);
        return ResultUtils.success(result);
    }


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    // endregion
}
