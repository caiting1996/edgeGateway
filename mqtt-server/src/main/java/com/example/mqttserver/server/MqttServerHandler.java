package com.example.mqttserver.server;

import com.example.mqttserver.config.NettyConfig;
import io.netty.channel.*;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@ChannelHandler.Sharable
public class MqttServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(MqttServerHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    /**
     * 	客户端与服务端 断连时执行 channelInactive方法之后执行
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    /**
     * 	从客户端收到新的数据时，这个方法会在收到消息时被调用
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception, IOException {
        if (null != msg) {
            log.info(msg.toString());
            MqttMessage mqttMessage = (MqttMessage) msg;
            log.info("info--"+mqttMessage.toString());
            MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
            Channel channel = ctx.channel();

            if(mqttFixedHeader.messageType().equals(MqttMessageType.CONNECT)){
                //	在一个网络连接上，客户端只能发送一次CONNECT报文。服务端必须将客户端发送的第二个CONNECT报文当作协议违规处理并断开客户端的连接
                //	to do 建议connect消息单独处理，用来对客户端进行认证管理等 这里直接返回一个CONNACK消息
                BootMqttMsgBack.connack(channel, mqttMessage);
            }

            switch (mqttFixedHeader.messageType()){
                case PUBLISH:		//	客户端发布消息
                    //	PUBACK报文是对QoS 1等级的PUBLISH报文的响应
                    System.out.println("123");
                    BootMqttMsgBack.puback(channel, mqttMessage);
                    break;
                case PUBREL:		//	发布释放
                    //	PUBREL报文是对PUBREC报文的响应
                    //	to do
                    BootMqttMsgBack.pubcomp(channel, mqttMessage);
                    break;
                case SUBSCRIBE:		//	客户端订阅主题
                    //	客户端向服务端发送SUBSCRIBE报文用于创建一个或多个订阅，每个订阅注册客户端关心的一个或多个主题。
                    //	为了将应用消息转发给与那些订阅匹配的主题，服务端发送PUBLISH报文给客户端。
                    //	SUBSCRIBE报文也（为每个订阅）指定了最大的QoS等级，服务端根据这个发送应用消息给客户端
                    // 	to do
                    BootMqttMsgBack.suback(channel, mqttMessage);
                    break;
                case UNSUBSCRIBE:	//	客户端取消订阅
                    //	客户端发送UNSUBSCRIBE报文给服务端，用于取消订阅主题
                    //	to do
                    BootMqttMsgBack.unsuback(channel, mqttMessage);
                    break;
                case PINGREQ:		//	客户端发起心跳
                    //	客户端发送PINGREQ报文给服务端的
                    //	在没有任何其它控制报文从客户端发给服务的时，告知服务端客户端还活着
                    //	请求服务端发送 响应确认它还活着，使用网络以确认网络连接没有断开
                    BootMqttMsgBack.pingresp(channel, mqttMessage);
                    break;
                case DISCONNECT:	//	客户端主动断开连接
                    //	DISCONNECT报文是客户端发给服务端的最后一个控制报文， 服务端必须验证所有的保留位都被设置为0
                    //	to do
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 	从客户端收到新的数据、读取完成时调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException {
    }

    /**
     * 	当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    /**
     * 	客户端与服务端第一次建立连接时执行
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * 	客户端与服务端 断连时执行
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception, IOException {
        log.info("删除断连客户端订阅的topic");
        List<String> topics=NettyConfig.getChannelTopicMap().get(ctx.channel());
        if(topics==null || topics.size()==0) return;
        for (String topic: topics){
            List<Channel> list=NettyConfig.getTopicMap().get(topic);
            list.remove(ctx.channel());
            NettyConfig.getTopicMap().put(topic,list);
        }
        NettyConfig.getChannelTopicMap().remove(ctx.channel());
        super.channelInactive(ctx);
    }

    /**
     * 	服务端 当读超时时 会调用这个方法
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception, IOException {
        super.userEventTriggered(ctx, evt);
        ctx.close();
    }


    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }


}
