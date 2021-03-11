package com.zgm.websocket.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zgm.websocket.entity.MessageEntity;

import java.util.List;

/**
 * @Description: TODO
 * @Author octopus
 * @Date 2020-10-26 14:08
 * @WebSite https://www.z-gm.com
 */
public interface MessageService extends IService<MessageEntity> {
    List<String> listByNoRead(String userId);
}
