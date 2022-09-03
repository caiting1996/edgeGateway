package com.example.cloud.server;

import com.example.cloud.db.DataStorageContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(ServerHeartbeatHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("server channelRead:{}", msg);
        if (msg.equals("ping")) {
            ctx.channel().writeAndFlush("pong");
        } else {
            //由下一个handler处理,示例中则为SimpleServerHandler
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            AttributeKey<String> userKey = AttributeKey.valueOf("userId");
            //从通道中获取用户token
            String userId = ctx.channel().attr(userKey).get();
            System.out.println(userId);
            AttributeKey<String> tokenKey = AttributeKey.valueOf("token");
            //从通道中获取用户token
            String token = ctx.channel().attr(tokenKey).get();
            System.out.println("token="+token);
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
