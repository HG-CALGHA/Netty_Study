package com.netty.demo_day1.nio;

import java.nio.ByteBuffer;

public class NIOByteBufferPutGet {
    public static void main(String[] args) {
        // 创建buffer
        ByteBuffer allocate = ByteBuffer.allocate(64);
        // 类型化方式放入数据
        allocate.putInt(10);
        allocate.putLong(10000);
        allocate.putDouble(10.24);
        allocate.putChar('a');
        allocate.putFloat(13);
        allocate.putShort((short) 4);
        // 反转数据
        allocate.flip();
        // 读取数据
        System.out.println(allocate.getInt());
        System.out.println(allocate.getLong());
        System.out.println(allocate.getDouble());
        System.out.println(allocate.getChar());
        System.out.println(allocate.getFloat());
        // 如果不安类型顺序读取会出现数据错乱或BufferUnderflowException异常
        /**
         * 测试BufferUnderflowException
         * System.out.println(allocate.getLong());
         */
        System.out.println(allocate.getShort());
    }
}
