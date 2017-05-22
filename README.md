# DRPC-Proxy 简介
DRPC-Proxy是基于使用storm的DRPC做RPC服务，解耦业务代码与storm框架代码的微框架；在使用某些场景下，有使用DRPC但不注重使用storm的流式计算的需求，通常情况下使用DRPCServer做为服务提供方接收请求，bolt中处理业务，ReturnResults返回结果，势必在bolt中会将业务代码与storm代码交织、耦合，为后期升级、重构、扩展留下难题。DRPC-Proxy是提供解耦的框架，服务消费方使用动态代理生调用DRPCClient与DRPCServer通讯，DRPCServer将请求匹配到对于的服务提供方的实现，最终结果由DRPCServer返回给消费方。
### DRPC-Proxy 特点
* 解耦storm与业务代码，开发过程中对storm无感知
* 使用简单，导入jar包，properties中添加相关服务的配置，pom.xml中添加依赖及profile
* 支持三种模式开发，脱离storm进行业务开发-rely模式，LocalDRPC模式，Remote模式
* 异常可远程抛出
* 对DRPC无封装，使用原生代码调用
* 集成AKKA，保证单线程下bolt对高并发的支持

### Module 说明

proxy : 基于接口的drpc调用

proxy-spring : 支持spring环境的基于接口的drpc调用

demo：

demo-customer : 服务消费者

demo-customer-spring : spring环境服务消费者

demo-server : 服务接口

demo-serviceimpl : 服务提供者

demo-serviceimpl-spring : spring环境服务提供者




