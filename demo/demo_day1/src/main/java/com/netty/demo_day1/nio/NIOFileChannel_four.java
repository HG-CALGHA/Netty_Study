package com.netty.demo_day1.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NIOFileCHannel_four
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/18 9:28
 * @Version 1.0
 */
public class NIOFileChannel_four {
    public static void main(String[] args) {
        File file1 = new File("C:\\Users\\CGH\\Desktop\\study\\Netty\\Photo\\CC.jpg");
        File file2 = new File("C:\\Users\\CGH\\Desktop\\study\\Netty\\Photo\\CC_Copy.jpg");

        try {
            // 创建流对象
            FileInputStream fileInputStream = new FileInputStream(file1);
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            // 创建流通道
            FileChannel inputStreamChannel = fileInputStream.getChannel();
            FileChannel outputStreamChannel = fileOutputStream.getChannel();
            // 拷贝
            outputStreamChannel.transferFrom(inputStreamChannel,0,inputStreamChannel.size());
            // 关闭流
            inputStreamChannel.close();
            outputStreamChannel.close();
            fileInputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
