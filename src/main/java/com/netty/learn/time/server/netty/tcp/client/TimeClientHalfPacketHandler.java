package com.netty.learn.time.server.netty.tcp.client;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Zhang Anjin
 * @description netty half packet handler
 * @date 2023/11/12 20:46
 */
@Slf4j
public class TimeClientHalfPacketHandler extends ChannelInboundHandlerAdapter {

    private int counter;
    private byte[] req;

    public TimeClientHalfPacketHandler() {
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
        String body = (String) msg;
        log.info("Now is : {}; The counter is :{}", body, ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Unexpected exception from downstream :{}", cause.getMessage());
        ctx.close();
    }
}
