package com.netty.learn.time.server.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @author Zhang Anjin
 * @description AIO Time Server Handle
 * @date 2023/10/24 21:13
 */
@Slf4j
public class AsyncTimeServerHandler implements Runnable {
    private int port;
    CountDownLatch countDownLatch;
    AsynchronousServerSocketChannel socketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {
            socketChannel = AsynchronousServerSocketChannel.open();
            socketChannel.bind(new InetSocketAddress(port));
            log.info("The time server is start in port: {}", port);
        } catch (IOException e) {
            log.info("", e);
        }
    }

    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);
        //接收客户端连接
        doAccept();
        try {
            //完成 Accept 之前当前线程阻塞
            //demo 中阻塞是防止服务端执行完成退出
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private void doAccept() {
        //传递一个 handler 接收 accept 操作成功的通知消息
        socketChannel.accept(this, new AcceptCompletionHandler());
    }
}
