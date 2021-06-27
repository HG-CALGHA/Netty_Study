package com.netty.demo_day1.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NIOFileChannel_three
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/17 19:54
 * @Version 1.0
 */
public class NIOFileChannel_three {
    public static void main(String[] args) {
        File file1 = new File("C:\\Users\\CGH\\Desktop\\study\\Netty\\File\\1.txt");
        File file2 = new File("C:\\Users\\CGH\\Desktop\\study\\Netty\\File\\2.txt");

        try {
            // 创建读写流的通道
            FileInputStream fileInputStream = new FileInputStream(file1);
            FileChannel fileChannel = fileInputStream.getChannel();
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            FileChannel channel = fileOutputStream.getChannel();
            // 创建缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(512);
            // 读取数据，循环读取直到read为-1时代表数据读完
            while (true){
                // 复位参数
                byteBuffer.clear();
                int read = fileChannel.read(byteBuffer);
                if (read == -1) break;
                // 反转缓冲区参数，从读转为写
                byteBuffer.flip();
                // 将数据写入文件
                channel.write(byteBuffer);
            }

            // 关闭流
            fileInputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
