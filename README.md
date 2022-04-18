# **z-rpc**
### Description
---
#### &nbsp;&nbsp; z-rpc is an RPC framework based on netty + kryo + zookeeper (Nacos), which can realize the remote service call function similar to Dubbo. It mainly includes: service registration / discovery module, network transmission module, spring annotation module, etc
### Features
---
- [x] **Use netty to realize network transmission**
- [x] **Use SPI mechanism to realize dynamic configuration expansion (registry, serialization mechanism, load balancing and compression mechanism)**
- [x] **Use the open source serialization mechanism kyro (or others) to replace the serialization mechanism of JDK**
- [x] **Use zookeeper or nacos to manage relevant service address information**
- [x] **Use the completable future wrapper to accept the results returned by the client**
- [x] **Added netty heartbeat mechanism**: Ensure that the connection between the client and the server is not broken and avoid reconnection
- [x] **Integrate spring annotations to realize service registration and consumption**
- [x] **Customize the communication protocol between client and server**
---
### 描述
---
#### &nbsp;&nbsp; Z-RPC 是一款基于 Netty+Kryo+Zookeeper（Nacos）实现的 RPC 框架，通过该框架可以实现与 Dubbo 类似的远程服务调用功能，主要包括：服务注册/发现模块、网络传输模块、Spring 注解模块等
### 特点
---
- [x] **使用Netty实现网络传输**
- [x] **运用 SPI 机制实现动态配置扩展（注册中心、序列化机制、负载均衡、压缩机制）**
- [x] **使用开源的序列化机制 Kyro（也可以用其它的）替代 JDK 自带的序列化机制**
- [x] **使用 Zookeeper 或 Nacos 管理相关服务地址信息**
- [x] **使用 CompletableFuture 包装接受客户端返回结果**
- [x] **增加 Netty 心跳机制**: 保证客户端和服务端的连接不被断掉，避免重连
- [x] **通过自定义注解实现服务注册、消费**
- [x] **自定义客户端和服务端通信协议**


