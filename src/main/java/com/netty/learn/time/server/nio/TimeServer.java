package com.netty.learn.time.server.nio;

/**
 * @author Zhang Anjin
 * @description NIO Time Server
 * @date 2023/10/19 21:37
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        if (null != args && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        MultiplexerTimeServerHandle handle = new MultiplexerTimeServerHandle(port);
        new Thread(handle, "NIO-MultiplexerTimeServer-001").start();
    }
}
