package com.netty.demo_day1.nio;

import java.nio.IntBuffer;

/**
 * @ClassName BasicBuffer
 * @Description TODO
 * @Author CGH
 * @Date 2021/6/16 19:40
 * @Version 1.0
 */
public class BasicBuffer {
    public static void main(String[] args) {
        // 创建Buffer 大小为5.可存放5个int
        IntBuffer buffer = IntBuffer.allocate(5);

        buffer.put(4,10);
        //向buffer中存放数据
        for(int i = 0;i < buffer.capacity();i++){
            buffer.put(10+i);
        }
        // 读取数据
        // flip buffer转换，读写切换
        /*
        *   limit = position; // 获取数据的总数
            position = 0; // 回到数据的第一位
            mark = -1;
            *
        * */
        buffer.flip();
        while (buffer.hasRemaining()){
            System.out.println(buffer.get());
        }
    }
}
