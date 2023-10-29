package com.netty.learn.time.server.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * @author Zhang Anjin
 * @description TimeClient 客户端
 * @date 2023/10/26 22:30
 */
@Slf4j
public class AsyncTimeClientHandler implements
        CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {
    private String host;
    private Integer port;
    private AsynchronousSocketChannel client;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        client.connect(new InetSocketAddress(host, port), this, this);
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("", e);
        }
        try {
            client.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @Override
    public void completed(Void unused, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes(StandardCharsets.UTF_8);
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer integer, ByteBuffer byteBuffer) {
                if (byteBuffer.hasRemaining()) {
                    client.write(byteBuffer, byteBuffer, this);
                } else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer integer, ByteBuffer byteBuffer) {
                            byteBuffer.flip();
                            byte[] bytes = new byte[byteBuffer.remaining()];
                            byteBuffer.get(bytes);
                            String body = new String(bytes, StandardCharsets.UTF_8);
                            log.info("Now is :{}", body);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable throwable, ByteBuffer byteBuffer) {
                            try {
                                client.close();
                                latch.countDown();
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
                    client.close();
                    latch.countDown();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        });
    }

    @Override
    public void failed(Throwable throwable, AsyncTimeClientHandler attachment) {
        throwable.printStackTrace();
        try {
            client.close();
            latch.countDown();
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
