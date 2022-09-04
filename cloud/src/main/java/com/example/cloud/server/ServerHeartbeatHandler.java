package com.example.cloud.server;

import com.example.cloud.config.NettyConfig;
import com.example.cloud.db.DataStorageContext;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ServerHeartbeatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(ServerHeartbeatHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("server channelRead:{}", msg.text());
        if (msg.text().equals("ping")) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame("pong"));
        } else {
            //由下一个handler处理
            ctx.fireChannelRead(msg.retain());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            AttributeKey<String> userKey = AttributeKey.valueOf("userId");
            //从通道中获取用户token
            String userId = ctx.channel().attr(userKey).get();
            AttributeKey<String> tokenKey = AttributeKey.valueOf("token");
            //从通道中获取用户token
            String token = ctx.channel().attr(tokenKey).get();
            log.info("将user和channel绑定");
            NettyConfig.getUserChannelMap().put(userId,ctx.channel());
            log.info("连接成功,channel{}",ctx.channel());
            //校验token逻辑
            if (!token.equals(DataStorageContext.getFileStorage().getData(userId))) {
                System.out.println("验签失败，关闭连接");
                //如果token校验不通过，发送连接关闭的消息给客户端，设置自定义code和msg用来区分下服务器是因为token不对才导致关闭
                ctx.writeAndFlush(new CloseWebSocketFrame()).addListener(ChannelFutureListener.CLOSE);
            }
        }

        if (evt instanceof IdleStateEvent) {
            //该事件需要配合 io.netty.handler.timeout.IdleStateHandler使用
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                //超过指定时间没有读事件,关闭连接
                log.info("超过心跳时间,关闭和服务端的连接:{}", ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
