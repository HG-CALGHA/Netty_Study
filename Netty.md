### :sleeping: Netty

## 1 Netty 介绍

1. Netty是由JBOSS提供的一个Java开源框架，现为Github上的独立项目
2. Netty是**异步**的，基于事件驱动的网络应用框架(图见 图解N-1)，用于**快速开发高性能，高可靠性**的**网络IO**程序
3. Netty主要针对在**TCP协议**下，面向**Clients端的高并发**应用，或Peer to Peer场景下的**大量数据持续传输**的应用
4. Netty本质是一个NIO框架，适用于**服务器通讯相关的多种应用**场景
5. 要透彻理解Netty需要先学习NIO，这样我们才能阅读Netty的源码

## 2 Netty的应用场景 

1. 在**分布式系统**中，各个**节点之间**需要远程服务调用，**高性能的RPC框架**必不可少，Netty作为**异步高性能的通信框架**，往往**作为基础的通信组件被RPC框架使用**
2. 典型应用 阿里分布式服务框架Dubbo的RPC框架使用Dubbo协议进行节点通信，dubbo协议默认使用Netty作为基础通信组件,用于实现各进程节点之间的内部通信

## 3 IO模型

1. IO模型简单的理解：使用什么样的管道进行数据的发送和接收，很大程度上决定看程序通信的性能

2. Java共支持3种网络编程模型/IO模式：BIO，NIO，AIO

   | IO模型          | IO模型解释                                                   | 适用场景                                                     |
   | --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
   | Java BIO        | 同步并阻塞(传统阻塞型),服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需启动一个线程进行处理，如这个连接不做任何事情会造成不必要的线程开销(图见 图解N-2) | BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序简单易理解 |
   | Java NIO        | 同步非堵塞，服务器实现模式为一个线程处理多个请求(连接),即客户端发送连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求就进行处理(图见 图解N-3) | BIO方式适用于**连接数目多且连接较短(轻操作)**的架构,比如聊天服务器，弹幕系统，服务器间通讯等。JDK1.4开始支持，编程较为复杂 |
   | Java AIO(NIO.2) | 异步非堵塞，AIO引入异步通道的概念，采用了Proactor模式，简化了程序编写，有效的请求才启动线程，它的特点是先由操作系统完成后才通知服务端线程启动线程去处理，一般适用于连接数较多且连接时间较长的应用,但未广泛使用 | AIO方式适用于**连接数目较多且连接数目较长(重操作)**的架构，比如相册服务器，充分调用OS参与办法操作，编程较为复杂，JDK7开始支持 |

## 4 Java BIO

### Java BIO 介绍

1. Java BIO 就是**传统的Java io 编程**，其相关的类和接口在Java.io中
2. BIO(blocking I/O) **同步阻塞**，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需启动一个线程进行处理，如这个连接不做任何事情会造成不必要的线程开销,可通过线程池机制改善(实现多客户连接服务器)
3. BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序简单易理解

### Java BIO 工作机制 编程简单流程

1. 服务器启动ServerSocketfasf
2. 客户端启动Socket对服务器进行通信，默认情况下服务器端需要对每个客户建立一个线程与之通讯
3. 客户端发出请求后，会先咨询服务器是否有线程响应，如没有则等待或被拒绝(超过处理的连接数时被拒绝)
4. 如有响应，客户端线程会等待请求结束后，在继续执行

### Java BIO 实例

> - 代码详见demo_day1
> - telnet无法使用
>   - windows键 进入 设置 找到应用(卸载,默认应用,可选功能) 进入 找到相关设置 [程序和功能] 进入 找到 启动或关闭Windows功能 进入 勾上并启用telnet

### Java BIO 问题分析

1. 每个请求都需要创建独立的线程，与对应的客户端进行数据read，业务处理，数据write
2. 当并发数较大时，需**创建大量线程来处理连接**，系统资源占用较大
3. 连接建立后，如当前线程暂无数据可读则线程阻塞在read操作上，造成线程资源浪费

## 5 Java NIO

### java NIO 基本介绍

1. Java NIO 全称Java non-blocking IO，是指JDK提供的新API 从JDK1.4开始，Java提供了一系列改进的输入/输出的新特性，被统称为NIO即New IO，是**同步非阻塞**的
2. NIO相关类被放在java.nio包及子包下，并且对原java.io包中的很多类进行了改写
3. NIO的核心共三部分：**Channel(通道),Buffer(缓冲区),Selector(选择器)**
4. NIO是面对缓冲区或者面向块编程的，数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后移动，这就增加了处理过程中的灵活性，使用它可以提供非阻塞式的高伸缩网络 
5. Java NIO的非阻塞模式，使一个线程从某通道发送请求或读取数据，但它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，而不是保持**线程堵塞**，所以直至数据变的可以读取之前，该线程可继续做其他的事情，非阻塞写也是如此，一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情
6. NIO可以做到用一个线程来处理多个操作的，假设有1W个请求过来，会根据实际情况对线程继续分配，可能50个也可能100个线程来处理，不会像之前的阻塞IO那样，非得分配1W个
7. HTTP2.0使用了多路复用的技术，可以做到同一个连接并发处理多个请求，而且并发请求的数量比HTTP1.1大好几个数量级

### java NIO Buffer的使用

-  代码详见demo_day1

### java NIO NIO与BIO的区别

1. BIO以流的方式处理数据，而NIO以块的方式处理数据，块I/O的效率比流I/O高很多
2. BIO为阻塞的，NIO为非阻塞的
3. BIO基于字节流和字符流进行操作，而NIO基于Channel(管道)和Buffer(缓冲区)进行操作，数据总是从管道读取到缓冲区中，或从缓冲区写入管道。Selector(选择器)用于监听多个通道的事件(如连接请求，数据到达等)，因此使用单个线程就可以监听多个客户端

### java NIO 三大核心原理关系说明(关系图请看图解 N-3)

1. 每个channl都对应一个buffer
2. selector对应一个Thread(线程)但可对应多个channl(连接),Thread可以对应多个selector
3. channel会注册到该selector中
4. 程序切换至那个channel是由事件决定的，Event是一个很重要的概念
5. selector会根据不同的事件在各个通道上切换
6. buffer虽是内存块但底层是一个数组
7. 数据的读取写入是通过buffer，这个和BIO不同，BIO中要么是输入流要么是输出流，不能双向，但是NIO的buffer可以读也可以写的，需要通过flip切换
8. channel是双向的，可以返回底层操作系统的情况，比如linux，底层操作系统通道就是双向的

## 6 Java Buffer

### Java Buffer 介绍

- **缓冲区(buffer)**：缓冲区的本质上是一个可读写的内存块，可理解为一个**容器对象(含数组)**，该对象**提供了一组方法**，可更为**轻松的使用内存块**，**缓冲区对象内置了一些机制**，能够**跟踪和记录缓冲区的状态变化**情况。**channel提供**了从**文件,网络读取数据的渠道**，但读取或写入数据**必须经过Buffer**，(图见 图解 N-4)
- Buffer类中的四大属性
  1. **mark：**标识
  2. **position：**标识当前数组的索引，即下一个要被读写的元素的索引，每次读写缓冲区数据时都会改变其值为下次读写做准备
  3. **limit：**缓冲区的当前终点，不能对缓冲区超过极限的位置进行读写操作，但极限是可修改的，即最大读取/写入的数据量
  4. **capacity：**容量，即最大数据存储量，在缓冲区创建时设立且创建后无法改变
- Buffer是一个顶层父类，是个抽象类，层级关系详见(图解 N-5)，常用的buffer子类为
  1. **ByteBuffer** 存储字节数据到缓冲区
  2. **ShortBuffer** 存储字符串数据到缓冲区
  3. **CharBuffer** 存储字符数据到缓冲区
  4. **IntBuffer** 存储整形数据到缓冲区
  5. **LongBuffer** 存储长整型数据到缓冲区
  6. **DoubleBuffer** 存储小数到缓冲区
  7. **FloatBuffer**  存储小数到缓冲区
- Buffer相关方法详见(图解 N-6)标红为常用方法

### Java Buffer ByteBuffer

- 从前面看出对于Java中的基本数据类型(boolean除外),都有一个Buffer类型与之相对应，但**最常用**的是**ByteBuffer类(二进制数据)**
- 主要方法详见(图解 N-7)标红为常用方法

## 7 Java Channel

### Java Channel 介绍

1. NIO的通道与流的区别

   1. **通道可同时进行读写，而流只能读或只能写**
   2. 通道可以**实现异步读写数据**
   3. 通道**可从缓冲区读取数据也可向缓冲区写入数据**(图解 N-8)

2. **BIO中的stream是单向的**，列如FileInputStream对象只能进行读取数据的操作，**而NIO的通道是双向的可读可写**

3. **Channel在NIO中是**一个**接口** public interface Channel extends Closeable {}

4. 常用的Channel类

   | Channel类                                   | 作用               | 父类                                        |
   | ------------------------------------------- | ------------------ | ------------------------------------------- |
   | **FileChannel**                             | 用于文件的数据读写 | AbstractInterruptibleChannel                |
   | **DatagramChannel**                         | 用于UDP的数据读写  | SelectableChannel.AbstractSelectableChannel |
   | **ServerSocketChannel**(类似于ServerSocker) | 用于TCP的数据读写  | NetworkChannel                              |
   | **SocketChannel**(类似于Socker)             | 用于TCP的数据读写  | NetworkChannel                              |

###  java Channel FileChannel

1. 主要用于对文本文件进行IO操作
2. 常见方法
   1. public int read(ByteBuffer dst) 从管道读取数据并放到缓冲区中
   2. public int write(ByteBuffer src) 将缓冲区的数据写到通道中
   3. public long transferFrom(ReadableByteChannel src,long position,long count) 从目标通道中复制数据到当前通道
   4. public long transferTo(long position,long count,WritableByteChannel target) 把数据从当前通道复制给目标通道

### Java Channel 实例

- 将数据写入文本文件 详见 demo_day1 NIOFileChannel_one 具体流程图详见(图解 N-9)
- 读取文本文件数据  详见 demo_day1 NIOFileChannel_two 具体流程图详见(图解 N-10)
- 单buffer的读取和写入 详见 demo_day1 NIOFileChannel_three 具体流程图详见(图解 N-11)
- 文件拷贝 详见 demo_day1 NIOFileChannel_four

## 8 Java Buffer与Channel的注意事项与细节

- ByteBuffer 

## N 图解

### N-1 网络应用框架

![image-20210616095332140](Netty.assets/image-20210616095332140.png)

### N-2 BIO 模型

![image-20210616102616319](Netty.assets/image-20210616102616319.png)

### N-3 NIO 模型

![image-20210617124021630](Netty.assets/image-20210617124021630.png)

### N-4 Buffer 模型

![image-20210617091816932](Netty.assets/image-20210617091816932.png)

### N-5 Buffer 层级树

![image-20210617093704962](Netty.assets/image-20210617093704962.png)

### N-6 Buffer 相关方法

![image-20210617095233782](Netty.assets/image-20210617095233782.png)

### N-7 Buffer ByteBuffer相关方法

![image-20210617101632330](Netty.assets/image-20210617101632330.png)

### N-8 Channel 模型

![image-20210617102428779](Netty.assets/image-20210617102428779.png)

### N-9 FileChannel  写入文件

![image-20210618092703979](Netty.assets/image-20210618092703979.png)

### N-10 FileChannel 读取文件



### N-11 FileChannel 单Buffer完成读写操作

![image-20210618092639372](Netty.assets/image-20210618092639372.png)