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
- [x] **Added netty heartbeat mechanism**
- [x] **Integrate spring annotations to realize service registration and consumption**
- [x] **Customize the communication protocol between client and server


