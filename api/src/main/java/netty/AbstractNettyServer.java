package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * netty客户端抽象类
 */
public abstract class AbstractNettyServer {

    protected ServerBootstrap bootstrap;
    protected EventLoopGroup workerGroup;
    protected EventLoopGroup bossGroup;
    protected NettyInitializer initializer;

    public AbstractNettyServer(NettyInitializer initializer){
        this.initializer=initializer;
        initNetty();
    }

    protected void initNetty() {
        // bootstrap服务端启动器，bossGroup辅助客户端的tcp连接请求, workGroup负责与客户端之前的读写操作
        workerGroup = new NioEventLoopGroup();
        bossGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup);
        // 设置NIO类型的channel
        bootstrap.channel(NioServerSocketChannel.class);
        //装配子通道流水线
        bootstrap.childHandler(initializer);

    }

    protected void destroy() throws InterruptedException {
        if(bossGroup != null){
            bossGroup.shutdownGracefully().sync();
        }
        if(workerGroup != null){
            workerGroup.shutdownGracefully().sync();
        }
    }
}
