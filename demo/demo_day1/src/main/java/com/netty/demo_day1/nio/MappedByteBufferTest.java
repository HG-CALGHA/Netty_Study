package com.netty.demo_day1.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 说明
 *  1.MappedByteBuffer 文件可在内存(堆外内存)中修改，操作系统不需要拷贝一次
 */
public class MappedByteBufferTest {
    public static void main(String[] args) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("C:\\Users\\86137\\Desktop\\studydata\\Netty\\Netty_Study\\File\\1.txt", "rw");
            // 获取管道
            FileChannel channel = randomAccessFile.getChannel();
            /**
             * 参数含义
             *  参数一 模式
             *  参数二 可直接修改的起始位置
             *  参数三 映射到内存的大小(不是索引,超过则报IndexOutOfBoundsException)，即将1.txt的多少字节可映射到内存中，也就是可修改的范围
             *  数据类型为 DirectByteBuffer
             */
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
            map.put(0,(byte) '3');
            map.put(3,(byte) '0');
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
