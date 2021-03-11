package com.zgm.websocket.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zgm.websocket.entity.MessageEntity;
import com.zgm.websocket.service.MessageService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.zgm.websocket.common.MessageStatus.READ;
import static com.zgm.websocket.common.MessageStatus.UN_READ;


/**
 * WebSocketServer
 *
 * @author zgm
 */
@Slf4j
@Component
@ServerEndpoint(value = "/socket/{userId}", configurator = EndpointConfig.class)
@NoArgsConstructor
public class WebSocketServer {
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private final static ConcurrentHashMap<String, WebSocketServer> WEB_SOCKET_MAP = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";

    private MessageService messageService;

    @Autowired
    public WebSocketServer(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        WEB_SOCKET_MAP.remove(userId);
        WEB_SOCKET_MAP.put(userId, this);
        log.info("用户连接:" + userId);
        try {
            List<String> messages = messageService.listByNoRead(userId);
            for (String message : messages) {
                sendMessage(message);
            }
        } catch (IOException e) {
            log.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        WEB_SOCKET_MAP.remove(userId);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("用户消息:" + userId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.userId);
                String toUserId = jsonObject.getString("toUserId");
                String msg = jsonObject.getString("message");
                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(toUserId) && WEB_SOCKET_MAP.containsKey(toUserId)) {
                    WEB_SOCKET_MAP.get(toUserId).sendMessage(msg);
                } else {
                    log.error("请求的userId:" + toUserId + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setContent(msg).setToUserId(toUserId).setUserId(this.userId).setStatus(UN_READ);
                    messageService.save(messageEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param error 连接错误
     */
    @OnError
    public void onError(Throwable error) {
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息
     */
    public void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setContent(message).setToUserId(userId).setUserId(this.userId);
        if (StringUtils.isNotBlank(userId) && WEB_SOCKET_MAP.containsKey(userId)) {
            WEB_SOCKET_MAP.get(userId).sendMessage(message);
            messageEntity.setStatus(READ);
        } else {
            messageEntity.setStatus(UN_READ);
            log.error("用户" + userId + ",不在线！");
        }
        messageService.save(messageEntity);
    }
}
