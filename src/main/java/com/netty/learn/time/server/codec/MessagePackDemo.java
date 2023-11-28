package com.netty.learn.time.server.codec;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Zhang Anjin
 * @description msg pack
 * @date 2023/11/28 21:54
 */
public class MessagePackDemo {

    public static void main(String[] args) throws IOException {
        List<String> src = new ArrayList<>();
        src.add("msgPack");
        src.add("viver");
        src.add("kumofs");

        MessagePack msgPack = new MessagePack();
        byte[] raw = msgPack.write(src);

        List<String> read = msgPack.read(raw, Templates.tList(Templates.TString));
        System.out.println(read.get(0));
        System.out.println(read.get(1));
        System.out.println(read.get(2));
    }
}
