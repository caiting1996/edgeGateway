package com.example.edge.mqttClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.springframework.stereotype.Component;

@Component
public class MqttClientHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        MqttMessage mqttMessage = (MqttMessage) msg;
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        switch (mqttFixedHeader.messageType()) {
            case CONNACK:
                // 连接报文的响应
                System.out.println("连接成功");
                break;
            case SUBACK:
                // 订阅报文的响应
                break;
            case UNSUBACK:
                // 取消订阅报文的响应
                break;
            case PUBLISH:
                MqttClientMsgBack.puback(ctx.channel(),mqttMessage);
                // 收到消息报文
                break;
            case PUBACK:
                // 发布消息报文响应
                // qos = 1的发布才有该回应
                break;
            case PUBREC:
                // qos = 2的发布才参与
                break;
            case PUBREL:
                // qos = 2的发布才参与
                break;
            case PUBCOMP:
                // qos = 2的发布才参与
                break;
            case PINGRESP:
                // ping报文响应
                break;
            default:
                break;
        }
    }
}
