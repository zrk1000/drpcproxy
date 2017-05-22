# DRPC-Proxy
## DRPC-Proxy是基于使用storm的DRPC做RPC服务，解耦业务代码与storm框架代码的微框架；在使用某些场景下，有使用DRPC但不注重使用storm的流式计算的需求，通常情况下使用DRPCServer做为服务提供方接收请求，bolt中处理业务，ReturnResults返回结果，势必在bolt中会将业务代码与storm代码交织、耦合，为后期升级、重构、扩展留下难题。
### 特点：


module说明：

proxy : 基于接口的drpc调用

proxy-spring : 支持spring环境的基于接口的drpc调用

demo：

demo-customer : 服务消费者

demo-customer-spring : spring环境服务消费者

demo-server : 服务接口

demo-serviceimpl : 服务提供者

demo-serviceimpl-spring : spring环境服务提供者




