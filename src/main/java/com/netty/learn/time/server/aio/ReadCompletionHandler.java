package com.netty.learn.time.server.aio;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Zhang Anjin
 * @description 读取完成的回调
 * @date 2023/10/24 22:05
 */
@Slf4j
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    //主要用于读取半包消息和发送应答
    private final AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel result) {
        this.channel = result;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        //为缓冲区读取数据做准备
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        String req = new String(body, StandardCharsets.UTF_8);
        log.info("The time server receive order: {}", req);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date().toString() : "BAD ORDER";
        //发送给客户端
        doWrite(currentTime);

    }

    private void doWrite(String currentTime) {
        if (StringUtils.isNotBlank(currentTime)) {
            byte[] bytes = currentTime.getBytes(StandardCharsets.UTF_8);
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer integer, ByteBuffer byteBuffer) {
                    //如果没有发送完成继续发送
                    if (byteBuffer.hasRemaining()) {
                        channel.write(byteBuffer, byteBuffer, this);
                    }
                }

                @Override
                public void failed(Throwable throwable, ByteBuffer byteBuffer) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        log.error("", e);
                    }
                }
            });

        }
    }

    @Override
    public void failed(Throwable throwable, ByteBuffer byteBuffer) {
        try {
            this.channel.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
