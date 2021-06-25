### :candy:  Netty

## 1 Netty的概述

### Java NIO存在的问题

1. NIO的类库和API复杂且使用麻烦，需掌握Selector，ServerSocketChannel，SocketChannel，ByteBuffer等
2. 需具备额外技能，如熟悉Java多线程编程，多线程和网络编程
3. 开发难度和工作量大
4. JDK NIO的BUG 较多且版本更替情况下也没能根本解决

### Netty 简述

1. 由JBOSS提供的Java开源框架，Netty**提供异步的，基于事件驱动的网络应用程序框架，可快速开发高性能高可靠的网络IO程序**
2. Netty**简化和流程化了NIO的开发过程**
3. Netty是**目前最流行的NIO框架**

### Netty 优点

1. 设计优雅 适用于各种参数类型的统一API阻塞和非阻塞的Socket 基于灵活且可扩展的事件模型，可清晰分离关注点；高度可定制的线程模型 - 单线程 一个或多个线程池
2. 使用方便 详细记录Javadoc且无其他依赖项
3. 高性能，高吞吐，延迟低；减少资源消耗，最小化不必要的内存复制
4. 安全 拥有完整的SSL/TLS和StartTLS支持
5. 社区活跃，不断更新 可及时发现并修复bug

### Netty 线程模型

1. 目前存在的线程模型有
   - 传统的阻塞I/O服务模型
   - Reactor模式
2. 据Reactor的数量和处理资源池线程的数量不同，有三种典型实现
   - 单Reactor单线程
   - 单Reactor多线程
   - 主从Reactor多线程
3. Netty线程模式(Netty 基于主从Reactor多线程模型做了一定的改进，其中主从Reactor多线程模型中有多个Reactor)

### Netty 传统阻塞I/O服务模型

- 模型特点
  1. 采用阻塞IO模式获取输入的数据
  2. 每个连接都需要独立的线程完成数据的输入业务处理，数据返回
- 问题分析
  1. 当并发数较大时，就会创建大量的线程，占用系统资源
  2. 连接创建后，如果当前线程暂时没有数据可读该线程会阻塞在read操作，造成线程资源的浪费
- 工作原理图 详见 图解 N-1 
  - 黄色的框为对象
  - 蓝色的框为线程
  - 白色的框为方法(API)

### Netty Reactor模式(反应器模式，分发者模式(Dispatcher)，通知者模式(notifier))

1. 基于IO复用模型 多个连接共用一个阻塞对象，应用程序只需在一个阻塞对象等待，无需阻塞等待所有连接，当某个连接有新数据可处理时，操作系统会通知应用程序，线程从阻塞状态返回，开始进行业务处理
2. 基于线程池复用线程资源 不必为每个连接创建线程，将连接完成后的业务处理任务分配给线程进行处理，同时一个线程可处理多个连接的业务
3. 模型特点
   - Reactor模型通过一个或多个输入同时传递给服务处理器的模式(基于事件驱动)
   - 服务器端程序处理传入多个请求并将他们同步分配到相应的处理线程，因此也叫Dispatcher模式
   - Reactor模式使用IO复用监听事件，收到事件后，会分发到某个线程(进程)，这是处理网络服务器高并发的关键
4. Reactor模式中核心组成
   - Reactor在一个单独的线程中运行，负责监听和分发事件，分发到适当的处理程序对IO事件做出反应
   - Handlers处理程序执行I/O事件要完成的实际事件，类似于客户想要与之交谈的公司中的实际官员，Reactor通过调度适当的处理程序来响应I/O事件,处理程序执行非阻塞操作
5. 工作原理图 详见 图解 N-2
   - 黄色的框为对象
   - 蓝色的框为线程
   - 白色的框为方法(API)

### Netty Reactor模式 单Reactor单线程

1. 方案说明
   1. Select是I/O复用模型介绍的标准网络编程API，可实现应用程序通过一个阻塞对象监听多路连接请求
   2. Reactor对象通过Select监听客户端请求事件,收到事件后通过Dispatch进行分发
   3. 如果是建立连接请求事件，则由Acceptor通过Accept处理连接请求，然后创建一个Handler对象处理完成后的后续业务处理
   4. 如不是建立连接事件，则Reactor会分发调用连接对应的Handler来响应
   5. Handler会完成Read - 业务处理 - Send完整的业务流程
   6. 结合实际 服务端用一个线程通过多路复用搞定所有的IO操作(包括连接,读,写等)，编码简单，清晰明了，当客户端连接数过多将无法支撑，NIO就属于这种模型
2. 方案的优缺点
   1. 优点 模型简单，无多线程,进程通信,竞争问题，全在一个线程中完成
   2. 缺点 
      1. 性能问题 只有一个线程，无法完全发挥多核CPU的性能。Handler在处理某个连接上的业务时，整个进程无法处理其他连接事件，容易达到性能瓶颈
      2. 可靠性问题，线程意外终止或进入死循环，会导致整个通信模块不可用且不能接收和处理外部消息，组成节点故障
   3. 使用场景 客户端数量有限，业务处理非常快速
3. 方案示意图 详见(图解 N-3)

### Netty Reactor模式 单Reactor多线程

1. 方案说明	
   1. Reactor对象通过select监控客户端请求事件，收到事件后，通过dispatch进行分发
   2. 如建立连接请求，则Acceptor通过accept处理连接并创建Handler对象，处理连接对象的所有事件
   3. 若非连接请求，则由reactor分发调用连接对应handler来处理
   4. handler 只负责响应事件，不做具体的业务处理，通过read读取数据后，会分发给后面的worker线程池的某个线程去处理业务
   5. worker线程池会分配独立的线程完成真正的业务逻辑，并返回结果至handler
   6. handler收到响应后，通过send将结果返回给client
2. 方案优缺点
   1. 优点 可充分利用多核cpu的处理能力
   2. 缺点多线程数据共享和访问较复杂，reactor处理所有的事件的监听和响应，在单线程运行，在高并发场景容易出现性能瓶颈
3. 方案示意图 详见(图解N-4)

### Netty Reactor模式 主从Reactor多线程

1. 方案说明
   1. Reactor主线程MainReactor对象通过select监听连接事件，收到事件后，通过Acceptor处理连接事件
   2. 当Acceptor处理连接事件后，MainReactor将连接分配给SubReactor
   3. subreactor将连接加入连接队列进行监听，并创建Handler进行各类事件处理
   4. 当有新事件发生时，subreactor就会调用对应的handler处理
   5. handler通过read读取数据，分发给后面的worker现场处理
   6. worker线程池分配独立的worker线程进行业务处理，并返回结果
   7. headler收到响应结果后，通过send返回到client
   8. Reactor主线程可对应多个Reactor子线程，即Main Reactor可关联多个SubReactor
2. 方案优缺点
   1. 优点	父线程与子线程的数据交互简单职责明确，父线程只需接受新连接，子线程完成后续的业务处理，父线程与子线程的数据交互简单，Reactor主线程只需把新连接传给子线程，子线程无需返回数据
   2. 缺点 编程复杂度较高
3. 方案示意图 详见(图解N-5)
   - 针对单Reactor多线程模型中Reactor在单线程中运行，高并发场景下容易达到性能瓶颈，可让Reactor在多线程中运行

### Netty Reactor模型的优点

1. 响应快 不必为单个同步时间所阻塞，虽Reactor本身依然是同步的
2. 可最大程度的避免复杂的多线程及同步问题且避免了多线程/进程的切换开销
3. 扩展性好 可方便的通过增加的Reactor实例个数来充分利用CPU
4. 复用性好 Reactor模型本身与具体事件处理逻辑无关，具有很高的复用性

### Netty模型

1. Netty模型是基于Reactor模型中的主从Reactor多线程的基础上进行改进的，其中主从Reactor多线程模型有多个Reactor Netty模型图 详见(图解 N-6)
2. 模型图简述
   1. BossGroup线程维护Selector，只关注Accept
   2. 当接收到Accept事件，获取到对应的SocketChannel,封装成NIOSocketChannel并注册到WorkerGroup线程并进行维护
   3. 当Worker线程监听到Selector中通道发生监听的事件就进行处理(handler)，注handler已加入通道

## N 图解 

### N-1 传统阻塞IO服务模型

![image-20210623154708663](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210623154708663.png)

### N-2 Reactor 服务模型

![image-20210623160621495](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210623160621495.png)

### N-3 Reactor 单Reactor单线程

![image-20210624091858611](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210624091858611.png)

### N-4 Reactor 单Reactor多线程

![image-20210624095456109](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210624095456109.png)

### N-5 Reactor 主从Reactor多线程

![image-20210625092035694](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210625092035694.png)

### N-6 Netty模型 简易版

![image-20210625095827048](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210625095827048.png)

