package com.netty.learn.time.server.nio;

/**
 * @author Zhang Anjin
 * @description NIO Client Server
 * @date 2023/10/22 21:32
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new Thread(new TimeClientHandler("127.0.0.1", port), "TimeClient-001").start();
    }
}
