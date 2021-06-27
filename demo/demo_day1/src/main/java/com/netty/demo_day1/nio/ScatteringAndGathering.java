package com.netty.demo_day1.nio;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering 可在数据写入buffer时,采用buffer数组依次写入
 * Gathering 可在buffer中读取数据时,采用buffer数组依次读取
 */
public class ScatteringAndGathering {
    public static void main(String[] args) {
        try {
            ServerSocketChannel open = ServerSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
            // 绑定端口哀悼socket并启动
            open.socket().bind(inetSocketAddress);
            // 创建buffer数组
            ByteBuffer[] byteBuffers = new ByteBuffer[2];
            byteBuffers[0] = ByteBuffer.allocate(5);
            byteBuffers[1] = ByteBuffer.allocate(3);
            // 等待客户端连接(telnet)
            SocketChannel accept = open.accept();
            while (true) {
                int byteRead = 0;
                while (byteRead < 8) {
                    long read = accept.read(byteBuffers);
                    byteRead += read;
                    System.out.println("byRead+=" + byteRead);
                    // 使用流打印，查看当前buffer的position和limit
                    Arrays.asList(byteBuffers).stream().map(byteBuffer -> "position = " + byteBuffer.position()
                            + " limit = " + byteBuffer.limit()).forEach(System.out::println);
                }
                // 反转所有的buffer
                Arrays.asList(byteBuffers).forEach(buffers -> {
                    buffers.flip();
                });
                // 将数据读取并显示
                long byteWriter = 0;
                while (byteWriter < 8) {
                    long write = accept.write(byteBuffers);
                    byteWriter += write;
                }
                // 将所有的buffer进行复位
                Arrays.asList(byteBuffers).forEach(buffers -> buffers.clear());
                System.out.println("byteRead" + byteRead + "byteWriter "+byteWriter + " SizeLength " + 8);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
