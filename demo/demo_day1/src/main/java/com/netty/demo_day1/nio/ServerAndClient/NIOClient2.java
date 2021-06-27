package com.netty.demo_day1.nio.ServerAndClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient2 {
    public static void main(String[] args) {
        try {
            // 获取通讯通道
            SocketChannel socketChannel = SocketChannel.open();
            // 设置非阻塞模式
            socketChannel.configureBlocking(false);
            // 绑定服务器IP和端口
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
            if(!socketChannel.connect(inetSocketAddress)){
                while (!socketChannel.finishConnect()){
                    System.out.println("请稍等，暂未连接成功，但客户端可做其他工作");
                }
            }
            // 设置需要发送的数据
            String str = "hello World~";
            // wrap方法可自动设置与传入数据相同大小的ByteBuffer
            ByteBuffer wrap = ByteBuffer.wrap(str.getBytes());
            // 发送数据,将 buffer数据写入channel
            socketChannel.write(wrap);

            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}