# DRPC-Proxy 简介
DRPC-Proxy是基于使用storm的DRPC做RPC服务，解耦业务代码与storm框架代码的微框架；在使用某些场景下，有使用DRPC但不注重使用storm的流式计算的需求，通常情况下使用DRPCServer做为服务提供方接收请求，bolt中处理业务，ReturnResults返回结果，势必在bolt中会将业务代码与storm代码交织、耦合，为后期升级、重构、扩展留下难题。DRPC-Proxy是提供解耦的框架，服务消费方使用动态代理生调用DRPCClient与DRPCServer通讯，DRPCServer将请求匹配到对于的服务提供方的实现，最终结果由DRPCServer返回给消费方。
### DRPC-Proxy 特点
* 解耦storm与业务代码，开发过程中对storm无感知
* 使用简单，导入jar包，properties中添加相关服务的配置，pom.xml中添加依赖及profile
* 支持三种模式开发，脱离storm进行业务开发-rely模式，LocalDRPC模式，Remote模式
* 提供spring环境下的支持，无spring亦可
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

### 用法1(非spring环境)

### 服务接口API
```
 public interface UserService {
    User getUser(String name) throws MyException;
}
```
### 服务提供者
```
public class UserServiceImpl implements UserService {
    public User getUser(String name) throws MyException{
        User user = new User();
        if("tom".equals(name)){
            user.setAge(12);
            user.setId(111L);
            user.setName("tom");
        }else {
            throw new MyOnlyException("业务异常");
        }
        return user;
    }
```
#### drpcproxy-provider.properties
```
service.impls=\
#  com.zrk1000.demo.serviceimpl.TestServiceImpl,\
  com.zrk1000.demo.serviceimpl.UserServiceImpl
drpc.spout.num=1
drpc.dispatch.bolt.num=1
drpc.result.bolt.num=1
drpc.spout.name=spout_name
drpc.topology.name=topology_name
```
#### 启动脚本
```
storm jar provider.jar  com.zrk1000.proxy.ConfigMain drpcSpoutName topologyName
```
### 服务消费者
```
public class Runner {
  public static void main(String[] args) {
      ServiceImplFactory.init();
      UserService userService = ServiceImplFactory.newInstance(UserService.class);
      User user = userService.getUser("tom");
      System.out.println("------------user:"+user.toString());
  }
}
```
#### drpcproxy-consumer.properties
```
drpc.client.config.storm.thrift.transport=org.apache.storm.security.auth.SimpleTransportPlugin
drpc.client.config.storm.nimbus.retry.times=3
drpc.client.config.storm.nimbus.retry.interval.millis=10000
drpc.client.config.storm.nimbus.retry.intervalceiling.millis=60000
drpc.client.config.drpc.max_buffer_size=104857600
drpc.client.host=192.168.1.81
drpc.client.port=3772
drpc.client.timeout=50000

topology.mapping.config.zrk1000-service-provider=\
#    com.zrk1000.demo.service.TestService,\
    com.zrk1000.demo.service.UserService

#topology.mapping.config.zrk1000-service-provider-spring=\
#    com.zrk1000.demo.service.UserService

#remote,local,rely
drpc.pattern=${profiles.pattern}


```

### 用法2(spring环境-springboot)

### 服务接口API
```
 public interface UserService {
    UserDto getUser(Long id);
    UserDto getUserByName(String name);
    List<UserDto> getUsers(String name);
    List<UserDto> getUsersByGroup(Long groupId);
    Integer getCount();
}
```
### 服务提供者
```
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    public UserDto getUser(Long id) {
        return convert(userRepository.findOne(id));
    }
    public UserDto getUserByName(String name) {
        return convert(userRepository.findTopByName(name));
    }
    public List<UserDto> getUsers(String name) {
        if(name==null)
            return converts(userRepository.findAll());
        return converts(userRepository.findByName(name));
    }
    public List<UserDto> getUsersByGroup(Long groupId) {
        return converts(userRepository.findTopByGroupId(groupId));
    }
    public Integer getCount() {
        return userRepository.countBy();
    }
    //转换为dto
    private UserDto convert(User user){
        UserDto dto = null;
        if(user!=null){
            dto = new UserDto();
            BeanUtils.copyProperties(user,dto);
        }
        return dto;
    }
    //转换为List<dto>
    private List<UserDto> converts(List<User> users){
        List<UserDto> userDtos = new ArrayList<UserDto>();
        if(users!=null)
            for (User user : users)
                userDtos.add(convert(user));
        return userDtos;
    }
}
```
#### drpcproxy-provider.properties
```
#业务所在的包名，使用AnnotationConfigApplicationContext创建spring上下文环境 ，建议使用springboot，可支持基于xml构建上下文
service.impls=com.zrk1000.demo
drpc.spout.num=1
drpc.dispatch.bolt.num=1
drpc.result.bolt.num=1
drpc.spout.name=spout_name
drpc.topology.name=topology_name


```
#### 启动脚本
```
storm jar provider.jar  com.zrk1000.proxy.SpringMain drpcSpoutName topologyName
```
### 服务消费者
```
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public UserDto getUser(@PathVariable("id") Long id){
        return userService.getUser(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public UserDto getUser(@RequestParam(required = true) String name){
        return userService.getUserByName(name);
    }

    @RequestMapping(value="/list",method = RequestMethod.GET)
    public List<UserDto> getUsers(@RequestParam(required = false) String name){
        return userService.getUsers(name);
    }

    @RequestMapping(value="/group",method = RequestMethod.GET)
    public List<UserDto> getUsers(@RequestParam(required = false) Long groupId){
        return userService.getUsersByGroup(groupId);
    }

    @RequestMapping(value="/count",method = RequestMethod.GET)
    public Integer count(){
        return userService.getCount();
    }

}
```
#### drpcproxy-consumer.properties
```
drpc.client.config.storm.thrift.transport=org.apache.storm.security.auth.SimpleTransportPlugin
drpc.client.config.storm.nimbus.retry.times=3
drpc.client.config.storm.nimbus.retry.interval.millis=10000
drpc.client.config.storm.nimbus.retry.intervalceiling.millis=60000
drpc.client.config.drpc.max_buffer_size=104857600
drpc.client.host=192.168.1.81
drpc.client.port=3772
drpc.client.timeout=5000



topology.mapping.config.zrk1000-service-provider-spring=\
#  com.zrk1000.demo.service.GroupService,\
  com.zrk1000.demo.service.UserService

```
#### StormConfig
```
@Profile({"local","remote"})
@Configuration
@ServiceScan(basePackages = "com.zrk1000.demo.service",
//        excludeClasses = {UserService.class},
        rpcHandleBeanRef="stormDrpcHandle")
public class StormConfig {

    @Profile("local")
    @Scope("singleton")
    @Bean("stormDrpcHandle")
    public RpcHandle getStormLocalRpcHandle(){
        StormLocalDrpcHandle drpcHandle = null;
        try {
            Set<String> serviceImpls = ServiceImplFactory.loadServiceImpls();
            SpringBoltHandle springBoltHandle = new SpringBoltHandle(serviceImpls.toArray(new String[serviceImpls.size()]));
            drpcHandle = new StormLocalDrpcHandle(springBoltHandle);
        } catch (IOException e) {
            throw new RuntimeException("初始化stormDrpcHandle失败");
        }
        return  drpcHandle;
    }

    @Bean
    @ConfigurationProperties("drpc.client")
    public DrpcClientConfig getDrpcClientConfig(){
        return new DrpcClientConfig();
    }

    @Bean
    @ConfigurationProperties("topology.mapping")
    public TopologyMapping getTopologyMapping(){
        return new TopologyMapping();
    }




    @Profile("remote")
    @Bean("stormDrpcHandle")
    public RpcHandle getStormRemoteRpcHandle(DrpcClientConfig clientConfig,TopologyMapping topologyMapping){
        Config config = new Config();
        config.putAll(clientConfig.getConfig());
        return  new StormRemoteDrpcHandle(config,clientConfig.getHost(),clientConfig.getPort(),clientConfig.getTimeout(),topologyMapping.getConfig());
    }
    
    class DrpcClientConfig{
        private String host;
        private Integer port;
        private Integer timeout;
        private Map<String,String> config ;
        //getter setter
    }

    class TopologyMapping {
        Map<String, Set<String>> config;
        //getter setter
    }

}
```


