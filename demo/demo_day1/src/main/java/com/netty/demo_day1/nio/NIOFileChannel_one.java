package com.netty.demo_day1.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NIOFileChannel_one
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/17 17:00
 * @Version 1.0
 */
public class NIOFileChannel_one {
    public static void main(String[] args) {
        String str = "hello";
        try {
            // 创建文件的输出流
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\CGH\\Desktop\\study\\Netty\\File\\1.txt");
            // 通过 fileOutputStream 获取对应的FileChannel
            // 实际数据类型为FileChannelImpl
            FileChannel channel = fileOutputStream.getChannel();
            // 创建缓冲区
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            // 将str 放入缓冲区(allocate)
//            for (Byte a: str.getBytes()) {
//                allocate.put(a);
//            }
            allocate.put(str.getBytes());
            // 反转读写方式,从写换成读
            /**
             * debug后会发现
             * position 从str的长度变化到0
             * limit 从原先的1024变化到已读取到的数据长度
             */
            allocate.flip();
            // 将缓冲区的数据  写入通道
            channel.write(allocate);
            // 关闭流通道
            channel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
