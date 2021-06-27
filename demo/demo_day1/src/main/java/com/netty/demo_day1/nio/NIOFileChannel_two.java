package com.netty.demo_day1.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NIOFileChannel_two
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/17 17:50
 * @Version 1.0
 */
public class NIOFileChannel_two {
    public static void main(String[] args) {
        // 创建文件的输入流
        File file = new File("C:\\Users\\CGH\\Desktop\\study\\\\Netty\\File\\1.txt");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            // 通过fileInputStream获取FileChannel(实际类型为FileChannelImpl)
            FileChannel channel = fileInputStream.getChannel();
            // 创建缓冲区 为防止内存浪费因此使用file.length()获取文本文件长度
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
            // 将通道的数据读取到缓冲区
            channel.read(byteBuffer);
            // 将缓冲区数据转换为字符串并打印
            System.out.println(new String(byteBuffer.array()));
            // 关闭流
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
