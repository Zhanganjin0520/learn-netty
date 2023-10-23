package com.netty.learn.time.server.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Zhang Anjin
 * @description 多路复用类
 * @date 2023/10/19 21:50
 */
@Slf4j
public class MultiplexerTimeServerHandle implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private volatile boolean stop;

    public MultiplexerTimeServerHandle(int port) {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.info("The time server is start in port: {}", port);
        } catch (IOException e) {
            log.error("", e);
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }


    @Override
    public void run() {
        while (!stop) {
            try {
                //休眠时间为1s selector 每隔1s 被唤醒一次
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception ex) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }

                }
            } catch (Throwable e) {
                log.error("", e);
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //处理新接入的请求消息
            if (key.isAcceptable()) {
                //Accept the new connection
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                //Add the new connection to the selector
                //向多路复用注册器读事件 OP_READ
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                //Read the data
                SocketChannel socketChannel = (SocketChannel) key.channel();
                //作为例子 开辟 1MB 的缓冲区
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);
                //返回值大于0 读到了字节 对字节进行编解码
                if (readBytes > 0) {
                    //将缓冲区的当前limit设置为position,position设置为0
                    //用于后续对缓冲区的读取操作
                    //目的就是为了从头开始读数据，将例子中的16个字节读取到数组中
                    readBuffer.flip();
                    //根据缓冲区可读的字节个数创建字节数组
                    byte[] bytes = new byte[readBuffer.remaining()];
                    //将可读的字节数组复制到新创建的字节数组中
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    log.info("The time server receive order: {}", body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
                    doWrite(socketChannel, currentTime);
                    //返回值为 -1 链路已经关闭 需要关闭 SocketChannel 释放资源
                } else if (readBytes < 0) {
                    //对端链路关闭
                    key.channel();
                    socketChannel.close();
                }
                //返回值等于 0 没有读取到字节 属于正常场景 忽略
            }
        }
    }

    private void doWrite(SocketChannel channel, String resp) throws IOException {
        if (resp != null && resp.trim().length() > 0) {
            byte[] bytes = resp.getBytes();
            ByteBuffer writerBuffer = ByteBuffer.allocate(bytes.length);
            //将字节数组复制到缓冲区中的字节数组
            writerBuffer.put(bytes);
            //对缓冲区进行 flip 操作
            writerBuffer.flip();
            channel.write(writerBuffer);
        }
    }
}
