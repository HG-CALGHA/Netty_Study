package com.netty.demo_day1.nio.zeroCopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @ClassName NewIOClient
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/22 19:43
 * @Version 1.0
 */
public class NewIOClient {
    public static void main(String[] args) {
        try {
            // 获取通信管道
            SocketChannel socketChannel = SocketChannel.open();
            // 绑定通信端口
            socketChannel.connect(new InetSocketAddress("127.0.0.1",7001));
            // 需要传输的文件
            FileChannel channel = new FileInputStream("C:\\Users\\CGH\\Desktop\\study\\Netty\\demo\\激活码.zip").getChannel();
            // 记录发送数据
            long timeMillis = System.currentTimeMillis();
            /**
             * 在Linux下transferTo方法就可直接完成传输
             * 但在windows下transferTo只能每次发送8m文件,就需要分段传输文件，且需要注意传输时的位置
             *  transferTo 底层使用零拷贝
             */
            long transferCount = channel.transferTo(0, channel.size(), socketChannel);
            System.out.println("发送的总的字节数 = "+transferCount+" 耗时: " + (System.currentTimeMillis()-timeMillis));
            // 关闭管道
            socketChannel.close();
        } catch (IOException e) {
        }
    }
}
