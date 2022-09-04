package com.example.cloud.server;


import api.MessageService;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.cloud.config.NettyConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import model.MsgModel;
import netty.Macher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.JsonUtil;

import java.util.concurrent.ExecutionException;

@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    @Autowired
    private MessageService messageService;

    /**
     * 一旦连接，第一个被执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded 被调用"+ctx.channel().id().asLongText());
        // 添加到channelGroup 通道组
        NettyConfig.getChannelGroup().add(ctx.channel());
    }

    /**
     * 读取数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("服务器收到消息：{}",msg.text());
        MsgModel msgModel= JsonUtil.string2Obj(msg.text(),MsgModel.class);
        messageService.receiveMsg(msgModel);
        ctx.fireChannelRead(msg.retain());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved 被调用"+ctx.channel().id().asLongText());
        // 删除通道
        NettyConfig.getChannelGroup().remove(ctx.channel());
        removeUserId(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常：{}",cause.getMessage());
        // 删除通道
        NettyConfig.getChannelGroup().remove(ctx.channel());
        removeUserId(ctx);
        ctx.close();
    }

    /**
     * 删除用户与channel的对应关系
     * @param ctx
     */
    private void removeUserId(ChannelHandlerContext ctx){
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        NettyConfig.getUserChannelMap().remove(userId);
    }

    /**
     * 向指定的客户端发送消息
     * @param msg
     * @param uid
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void sendMsg(String msg,String uid) throws ExecutionException, InterruptedException {
        ChannelGroupFuture channelFuture=NettyConfig.getChannelGroup().writeAndFlush(new TextWebSocketFrame(msg),new Macher(NettyConfig.getUserChannelMap().get(uid)));
        System.out.println(channelFuture.get());
    }
}
