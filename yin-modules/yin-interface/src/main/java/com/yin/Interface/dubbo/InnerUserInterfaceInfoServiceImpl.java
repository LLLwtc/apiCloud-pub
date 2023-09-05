package com.yin.Interface.dubbo;

import com.yin.Interface.mq.APIMessageProducer;
import com.yin.Interface.mq.Message;
import com.yin.Interface.service.UserInterfaceInfoService;
import com.yin.api.Interface.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


/**
 * 内部用户接口信息服务实现类
 *
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private APIMessageProducer apiMessageProducer;

    /**
     * 统计接口总调用次数。剩余调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public void invokeCount(long interfaceInfoId, long userId) {
        //放入消息队列
        Message message = new Message(interfaceInfoId, userId);

        apiMessageProducer.sendMessage(message);
    }

    /**
     * 是否还有剩余调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public boolean hasLeftNum(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.hasLeftNum(interfaceInfoId, userId);
    }
}


