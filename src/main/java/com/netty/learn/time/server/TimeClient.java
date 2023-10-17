package com.netty.learn.time.server;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Zhang Anjin
 * @description Time Client
 * @date 2023/10/17 21:47
 */
@Slf4j
public class TimeClient {
    /**
     * @param args .
     */
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket("127.0.0.1", port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("QUERY TIME ORDER");
            log.info("Send order 2 server succeed.");
            String resp = in.readLine();
            log.info("Now is: {}", resp);

        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioException) {
                    log.info("", ioException);
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    log.error("", ioException);
                }
                socket = null;
            }
        }
    }
}
