package com.netty.learn.time.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @author Zhang Anjin
 * @description 文件定义
 * @date 2023/11/28 22:03
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MessagePack msgPack = new MessagePack();
        byte[] raw = msgPack.write(o);
        byteBuf.writeBytes(raw);
    }
}
