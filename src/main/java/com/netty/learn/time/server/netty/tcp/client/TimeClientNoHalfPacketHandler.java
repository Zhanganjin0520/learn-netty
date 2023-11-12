package com.netty.learn.time.server.netty.tcp.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author Zhang Anjin
 * @description netty no half packet client handler
 * @date 2023/11/5 21:00
 */
@Slf4j
public class TimeClientNoHalfPacketHandler extends ChannelInboundHandlerAdapter {
    private int counter;
    private byte[] req;

    /**
     * create a client-side handler
     */
    public TimeClientNoHalfPacketHandler() {
        //line.separator is \n used one byte
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req);
        log.info("Now is :{}, the counter is: {}", body, ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream:{}", cause.getMessage());
        ctx.close();
    }
}
