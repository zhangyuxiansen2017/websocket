package com.zgm.websocket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author octopus
 * @Description: 未消费的消息
 * @Date 2020/10/26 13:56
 * @WebSite https://www.z-gm.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("rs_message")
public class MessageEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息发送方
     */
    private String userId;
    /**
     * 消息接收方
     */
    private String toUserId;
    /**
     * 消息状态 0为维度，1为已读
     */
    private Integer status;
}
