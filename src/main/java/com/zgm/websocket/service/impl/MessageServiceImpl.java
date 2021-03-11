package com.zgm.websocket.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zgm.websocket.dao.MessageDao;
import com.zgm.websocket.entity.MessageEntity;
import com.zgm.websocket.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.zgm.websocket.common.MessageStatus.READ;


/**
 * @author octopus
 * @Description:
 * @Date 2020/10/26 14:08
 * @WebSite https://www.z-gm.com
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageDao, MessageEntity> implements MessageService {

    @Override
    public List<String> listByNoRead(String userId) {
        List<MessageEntity> list = this.list(new QueryWrapper<MessageEntity>().eq("to_user_id", userId).eq("status",0));
        List<MessageEntity> collect = list.stream().map(p -> p.setStatus(READ)).collect(Collectors.toList());
        this.updateBatchById(collect);
        return list.stream().map(MessageEntity::getContent).collect(Collectors.toList());
    }
}
