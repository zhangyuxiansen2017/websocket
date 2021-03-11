package com.zgm.websocket.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zgm.websocket.entity.MessageEntity;
import org.springframework.stereotype.Repository;

/**
 * @author octopus
 * @Description:
 * @Date 2020/10/26 14:06
 * @WebSite https://www.z-gm.com
 */
@Repository
public interface MessageDao extends BaseMapper<MessageEntity> {
}
