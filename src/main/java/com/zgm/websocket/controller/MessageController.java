package com.zgm.websocket.controller;

import com.zgm.websocket.config.WebSocketServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author octopus
 * @Description:
 * @Date 2021/3/11 11:50
 * @WebSite https://www.z-gm.com
 */
@RestController
@RequestMapping(value = "/message")
public class MessageController {

    private final WebSocketServer webSocketServer;

    public MessageController(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @GetMapping(value = "/send")
    public String sendMsg(String msg, String userId) throws IOException {
        webSocketServer.sendInfo(msg, userId);
        return "success";
    }

}
