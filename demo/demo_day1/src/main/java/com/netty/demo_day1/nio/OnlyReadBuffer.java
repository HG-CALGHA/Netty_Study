package com.netty.demo_day1.nio;

import java.nio.ByteBuffer;

public class OnlyReadBuffer {

    public static void main(String[] args) {
        // 创建Buffer
        ByteBuffer buffer = ByteBuffer.allocate(64);
        // 循环添加数据
        for(int i=0;i<64;i++){
            buffer.put((byte) i);
        }

        // 反转
        buffer.flip();
        // 得到只读buffer HeapByteBufferR 只读类型
        ByteBuffer byteBuffer = buffer.asReadOnlyBuffer();
        // 查看buffer类型 HeapByteBufferR 只读类型
        System.out.println(byteBuffer.getClass());
        // 读取数据
        while (byteBuffer.hasRemaining()){
            System.out.println(byteBuffer.get());
        }
        // 验证是否只读 出现java.nio.ReadOnlyBufferException代表该buffer只具有读取权限
        byteBuffer.put((byte) 123);
    }
}
