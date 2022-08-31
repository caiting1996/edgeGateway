package netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;

public abstract class NettyInitializer extends ChannelInitializer<SocketChannel> {
    //有连接到达时会创建一个channel
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //防粘包拆包
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer("_$".getBytes())));
        ch.pipeline().addLast(new DelimiterBasedMessageEncoder("_$"));
    }
}
