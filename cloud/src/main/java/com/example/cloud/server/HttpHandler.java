package com.example.cloud.server;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@ChannelHandler.Sharable
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        System.out.println(msg);
        Map<CharSequence, CharSequence> queryMap = UrlBuilder.ofHttp(msg.uri()).getQuery().getQueryMap();
        System.out.println(queryMap);
        CharSequence token = queryMap.get("token");
        CharSequence userId=queryMap.get("userId");
        if (token == null) {
            token = msg.headers().get("token");
        }
        if(userId == null){
            userId=msg.headers().get("userId");
        }

        //提前结束传递,关闭连接
        //if (token == null || !token.equals("1")) {
        //    ctx.writeAndFlush("token错误").addListener(ChannelFutureListener.CLOSE);
        //    return;
        //}

        ctx.channel().attr(AttributeKey.valueOf("token")).setIfAbsent(token.toString());

        ctx.fireChannelRead(msg.retain());
    }
}
