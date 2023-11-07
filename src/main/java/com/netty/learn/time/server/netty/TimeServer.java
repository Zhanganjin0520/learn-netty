package com.netty.learn.time.server.netty;

import com.netty.learn.time.server.netty.tcp.TimeServerHalfPacketHandler;
import com.netty.learn.time.server.netty.tcp.TimeServerNoHalfPacketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Zhang Anjin
 * @description netty time server
 * @date 2023/10/31 22:30
 */
@Slf4j
public class TimeServer {
    /**
     * bind
     *
     * @param port .
     */
    public void bind(int port) {
        //配置服务端的 NIO 线程组 实际就是 Reactor 线程组
        //处理客户端网络连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //SocketChannel 读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        //绑定线程组
        bootstrap.group(bossGroup, workerGroup)
                //设置 Channel 类型
                .channel(NioServerSocketChannel.class)
                //设置 TCP 参数
                .option(ChannelOption.SO_BACKLOG, 1024)
                //配置 Handler
                .childHandler(new ChildChannelHandler());
        try {
            //绑定端口，同步等待成功
            //异步操作的通知回调
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务端监听端口
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("", e);
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        new TimeServer().bind(port);
    }

    /**
     * handler
     */
    class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
//            socketChannel.pipeline().addLast(new TimeServerHandler());
            //验证无半包处理 handler
            socketChannel.pipeline().addLast(new TimeServerNoHalfPacketHandler());
            //验证处理半包
//            socketChannel.pipeline().addLast(new TimeServerHalfPacketHandler());
        }
    }
}
