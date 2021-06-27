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

## 2 Netty 线程模型

### Netty 线程模型简述

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

1. Netty模型是基于Reactor模型中的主从Reactor多线程的基础上进行改进的，其中主从Reactor多线程模型有多个Reactor Netty模型图 详见(图解 N-6 图解 N-7)

   - 模型图简述
     1. BossGroup线程维护Selector，只关注Accept
     2. 当接收到Accept事件，获取到对应的SocketChannel,封装成NIOSocketChannel并注册到WorkerGroup线程并进行维护
     3. 当Worker线程监听到Selector中通道发生监听的事件就进行处理(handler)，注handler已加入通道

2. Netty模型 详细版  详见(图解N-8)

   - 模型图简述
     1. Netty抽象出两组线程池，BossGroup专门负责接收客户端的连接，WorkerGroup专门负责网络的读写
     2. BossGroup和WorkerGroup类型都为NioEventLoopGroup
     3. NioEventLoopGroup相当于一个不断循环的事件组，这组中含有多个事件循环，每个事件循环是NioEventLoop
     4. NioEventLoop表示一个不断循环的执行任务的线程，每个NioEventLoop都有一个selector用于监听绑定在其上的socket的网络通讯
     5. NioEventLoopGroup 可有多个线程(可自由设置)，即含有多个NioEventLoop
     6. 每个Boss NioEventLoop循环执行的步骤可分为3步
        1. 轮询accept事件
        2. 处理accept事件，与client建立连接，生成NioScoketChannel，并将其注册到某个worker NIOEventLoop上的selector
        3. 处理任务队列的任务 即runAllTasks
     7. 每个Worker NIOEventLoop循环执行的步骤可分为3步‘
        1. 轮询read/write事件
        2. 处理I/O事件,即read/write事件，在对应NIOScoketChannel处理
        3. 处理任务队列的任务,即runAllTasks
     8. 每个Worker NIOEventLoop处理业务时，会使用pipeline(管道),pipeline中包含了channel，即通过pipeline可获取到对应通道,管道中维护了很多的处理器
- 案例 详见(demo_day1 netty simple )

### Netty 模型 Task的使用场景

1. 用户自定义普通任务  案例详见demo_day1 netty simple NettyHandler channelRead()
2. 用户自定义定时任务 案例详见demo_day1 netty simple NettyHandler channelRead()
3. 非当前Reactor线程调用Channel的各种方法(思路: 可根据用户标识，寻得对应的Channel引用)，通过writer方法向该用户发送推送消息

### Netty模型 总结

1. Netty抽象出两组数组，BossGroup 只处理连接请求，WorkerGroup 只处理读写请求(业务处理)
2. NioEventLoop表示一个不断循环执行处理任务的线程，每个NioEventLoop都有一个Selector，用于监听绑定的socket通道
3. NioEventLoop内部采用串行化设计，从消息读取->解码->处理->编码->发送，始终由IO线程NioEventLoop负责
   - NioEventLoopGroup下包含多个NioEventLoop
   - 每个NioEventLoop中包含一个Selector，一个taskQueue
   - 每个NioEventLoop的Selector上可注册监听多个NioChannel
   - 每个NioChannel只会绑定在唯一的NioEventLoop上
   - 每个NioChannel都绑定一个自己的ChannelPipeline

## 3 Netty 异步模型

### Netty 异步模型简述

1. 异步概念与同步相对，当一个异步过程调用发出后，调用者无法立刻获取结果，实际处理这个调用的组件 完成后，通过状态，通知和回调来知会调用者
2. Netty的I/O操作是异步的，包括Bind，Write，Connect等操作来返回一个简单的ChannelFuture
3. 调用者不能立刻获取结果，而是通过Future-Listener机制，用户可方便的主动获取或通知机制获取操作结果
4. Netty异步模型是建立在future和callback(回调)之上的，Future的核心思想是 假设一个方法fun，计算耗时，无法等待其完成再进行下一步，因此可以在调用fun时返回Future，后续通过Future对象去监控fun的处理结果(Future-Listener机制)

### Netty 异步模型 Future说明

1. 表示异步执行的结果，可通过它提供的方法来检测执行是否完成
2. ChannelFuture接口:public interface ChannelFuture extends Future<Void> 可添加监听器，当监听事件发生时，就会 触发监听器

### Netty 异步模型 工作原理图(详见 图解N-9)

1. 在使用 Netty 进行编程时，拦截操作和转换出入站数据只需要您提供 callback 或利用future 即可。这使得**链式操作**简单、高效, 并有利于编写可重用的、通用的代码；
2. Netty 框架的目标就是让你的业务逻辑从网络基础应用编码中分离出来、解脱出来。

### Netty 异步模型 Future-Listener机制

1. 当Future对象刚刚好创建时，处于非完成状态，调用者可通过返回的ChannelFuture来获取操作的状态，注册 监听函数来执行完成后的操作

2. 常见操作

   - isDone方法 判断当前操作是否完成(仅操作是否结束)
   - isSuccess 判断当前已完成的操作是否成功
   - getCause 获取当前已完成的操作的失败原因
   - isCancelled 注册监听器，当操作已完成(isDone 方法返回完成),将会知会指定的监听器；如果Future对象已完成则通知指定的监听器

3. 举例说明

   ```java
   ChannelFuture sync = serverBootstrap.bind(6668).sync();
               sync.addListener(new ChannelFutureListener() {
                   @Override
                   public void operationComplete(ChannelFuture future) throws Exception {
                       if (sync.isSuccess()){
                           System.out.println("监听成功");
                       }else {
                           System.out.println("失败");
                       }
                   }
               });
   ```

   - 小结 相比传统的阻塞I/O，执行I/O操作后线程会被阻塞，直到操作完成，异步处理不会，线程可在I/O操作期间执行别的程序，在高并发下拥有更好的稳定性和更高的吞吐量

### Netty 异步模型 HTTP服务实例

- 实例要求
  - 服务器在6668端口进行监听，浏览器发出的请求 http;//localhost:6668/
  - 服务器可回复信息给客户端“hello 我是服务器”并对特定请求资源进行过滤
- 详见 demo_day1 netty  http

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

### N-7 Netty模型 进阶版

![image-20210626152219527](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210626152219527.png)

### N-8 Netty模型 详细版

![image-20210626152922786](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210626152922786.png)

### N-9 Netty 异步模型

![image-20210627151428449](Netty%E6%AD%A3%E5%BC%8F%E5%AD%A6%E4%B9%A0.assets/image-20210627151428449.png)

