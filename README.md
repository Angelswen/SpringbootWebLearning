# SpringbootWebLearning
Spirngboot+Mybatis+Redis Web应用开发案例

**开发环境**

Spring boot 1.5.15、Mybatis1.3.0、Redis3.2.1、jdk1.8、MySQL5.6、IntelliJ IDEA2018.3

**内容概述**

Spring boot是当今最为流行的Java web开发框架之一，支持基于注解的自动配置方式，并整合了业界各种开发框架；Mybatis是轻量级ORM框架，相比于hibernate使用起来更为方便（关系依赖较少）；redis是当前十分流行的分布式key-value型数据库，在web开发中，常用作缓存、分布式session等。

该仓库是基于以上技术的一个web开发案例，使用Spring boot快速构建web应用，采用Mybatis作为ORM框架，并使用redis作为Mybatis作为二年级缓存，以提升性能。单元测试采用h2内存数据库生成测试数据。

通过该案例，可以快速上手web应用开发，并在此基础之上，拓展更多功能。

**运行项目**

在IntelliJ IDEA里面将本案例git下来，待加载完相应库之后，启动redis服务器，再运行测试程序即可观察结果。redis启动后如下：

![启动redis](https://raw.githubusercontent.com/Angelswen/SpringbootWebLearning/master/imge/%E5%90%AF%E5%8A%A8redsi.png)

**系统分析**

以商品（Product）处理为例，根据需求分析，需要编写根据productId查询product信息的get接口、更新product信息的put接口，画出MVC分层设计结构的类图，如下：

(类图)

**Spring boot快速构建应用**

在IDEA里面使用Spring Initializer新建项目，选择web、mybatis、redis、mysql、h2模块，初始化成功后建立对应的包，如下：

新建项目：

![新建项目](https://raw.githubusercontent.com/Angelswen/SpringbootWebLearning/master/imge/%E6%96%B0%E5%BB%BAspringboot%E9%A1%B9%E7%9B%AE.png)

项目结构：

![项目结构](https://raw.githubusercontent.com/Angelswen/SpringbootWebLearning/master/imge/%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84.png)



**编写API接口**

1、实体类定义：product类定义如下，包括商品id、名称nama、价格price、数量quantity：
```
    package com.vechace.weblearning.Dao.domain;
    import java.io.Serializable;
    
    public class Product implements Serializable {
        private static final long serialVersionUID = 1435515995276255188L;
        private long id;
        private String name;
        private long price;
        private int quantity;
        //setter and getter
        }
```
2、控制层接口：ProductController，定义getProductInfo()方法与updataProductInfo方法，这两方法分别调用Service层对应方法来实现功能，如下：

    package com.vechace.weblearning.Controller;
    
    import com.vechace.weblearning.Dao.domain.Product;
    import com.vechace.weblearning.Service.ProductService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;
    
    /**
     * 控制层：处理http请求; http://localhost:8081/product/id
     * Created by vechace on 2018/6/23
     */
    @RestController
    @RequestMapping("/product")
    public class ProductController {
    
        @Autowired
        private ProductService productService;
    
        @GetMapping("{/id}")
        public Product getProductInfo(@PathVariable("id") Long productId){
            return productService.select(productId);
        }
    
        @PutMapping("/{id}")
        public Product updateProductInfo(@PathVariable("id") Long productId,@RequestBody Product newProduct){
            return  productService.update(productId,newProduct);
        }
    }
    

注解说明：

- @RestController：表示该类为Controller，并且提供Rest接口，即所有接口值以Json格式返回，该注解是@Controller与@ResponseBody的组合注解。

- @RequestMapping、@GetMapping、@PutMapping：表示接口的URL地址；标注在类上的@RequestMapping注解表示该类下的所有接口的URL都以/product开头；@GetMapping表示这是一个Get HTTP接口，@PutMapping表示这是一个Put HTTP接口。

- @PathVariable、@RequestBody：表示参数的映射关系。假设有个Get请求访问的是/product/1，那么该请求会由getProductInfo方法处理，其中URL里的1会被映射到productId中。同理，如果是Put请求的话，请求的body会被映射到newProduct对象中

3、Service层接口：ProductService，由控制层调用，向下调用dao层数据查询接口：

    package com.vechace.weblearning.Service;
    
    import com.vechace.weblearning.Dao.domain.Product;
    import com.vechace.weblearning.Dao.mapper.ProductMapper;
    import com.vechace.weblearning.Utils.ProductNotFoundException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    
    /**
     * 业务层：业务逻辑处理，接收控制层请求，向下dao层进行数据查询
     * Created by vechace on 2018/6/23
     */
    @Service
    public class ProductService {
        @Autowired
        private ProductMapper productMapper;
    
        public Product select(Long productId){
            return productMapper.select(productId);
        }
    
        public Product update(Long productId,Product newProduct){
    
            Product product = productMapper.select(productId);
            if(product ==null){
                throw new ProductNotFoundException(productId);
            }
            product.setName(newProduct.getName());
            product.setPrice(newProduct.getPrice());
            product.setQuantity(newProduct.getQuantity());
    
            productMapper.update(product);
            return  product;
        }
    }
    

注解说明：

- @Service：表示该类是一个Service，自动注入容器
- @Autowire：自动注入，这里表示在Service层中注入dao层的bean

**集成Mybatis开发**

1、配置Mybatis：

    #mybatis配置
    mybatis:
      # 配置映射类所在包名
      type-aliases-package: com.vechace.weblearning.Dao.domain
      # 配置mapper.xml文件所在位置
      mapper-locations:
        - mappers/ProductMapper.xml
    

2、编写dao层接口：ProductMapper接口，数据操作接口

    package com.vechace.weblearning.Dao.mapper;
    
    import com.vechace.weblearning.Dao.domain.Product;
    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.annotations.Param;
    
    /**
     * Dao层：Mybatis中的Mapper，执行SQL,对应ProductMapper.xml在目录resources/mappers/下
     * Created by vechace on 2018/6/23
     */
    @Mapper
    public interface ProductMapper {
    
        Product select(@Param("id") long id);
    
        void update(Product product);
    }
    

其中，ProductMapper接口加了@Mapper注解，表明这是一个Mapper，Spring boot在初始化时会自动注入该bean。

3、编写SQL语句，SQL写在XML文件中，当然也可以采用基于注解的SQL方式，本案例采用基于XML的mapper，如下：

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.vechace.weblearning.Dao.mapper.ProductMapper">
    
        <!-- 开启基于redis的二级缓存 -->
        <cache type="com.vechace.weblearning.Cache.RedisCache"/>
    
        <select id="select" resultType="Product">
            SELECT * FROM products WHERE id = #{id} LIMIT 1
        </select>
    
        <update id="update" parameterType="Product" flushCache="true">
            UPDATE products SET name = #{name}, price = #{price},quantity = #{quantity} WHERE id = #{id} LIMIT 1
        </update>
    </mapper>
    

mapper.xml文件可以放在resource目录下，并在mybatis配置时，声明位置信息，参见1中的配置：mapper-locations

**集成Redis开发**

1、配置redis：与配置一般数据库一样，redis也需要相应配置，在application.yml中配置如下：

      #配置Redis
      redis:
        # redis数据库索引（默认为0），我们使用索引为3的数据库，避免和其他数据库冲突
        database: 3
        # redis服务器地址：默认为localhost
        host: localhost
        # redis端口：默认为6379
        port: 6379
        # redis访问密码：默认为空
        password:
        # redis连接超时时间：单位为毫秒
        timeout: 0
        # redis连接池配置
        pool:
          # 最大可用连接数：默认为8，负数表示无限
          max-active: 8
          # 最大空闲连接数：默认为8，负数表示无限
          max-idle: 8
          # 最小空余连接数：默认为0，该值只有正数才有作用
          min-idle: 0
          # 从连接池中获取连接最大等待时间：默认为-1，单位为毫秒，负数表示无限
          max-wait: -1
    

2、使用redis实现缓存接口：新建RedisCache类实现org.apache.ibatis.cache.Cache接口，mybatis二级缓存可以自动地对数据库的查询作缓存，并且在更新数据时会自动更新缓存。

在执行两次相同数据操作时，第一次查询结果会缓存在Redis中，第二次操作则直接从Redis中获取数据，而不用到数据库中查询，由于Redis是基于内存的数据库，因此查询性能会很高（在内存上查数据比在数据库（磁盘）中要快）。

    public class RedisCache implements Cache {
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RedisCache.class);
    
        private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private final String id;
        private RedisTemplate redisTemplate;
    
        private static final long EXPIRE_TIME_IN_MINUTES = 30;
        public RedisCache(String id){
            if(id ==null){
                throw new IllegalArgumentException("Cache instance require an ID");
            }
            this.id = id;
        }
    
        @Override
        public String getId(){
            return id;
        }
    
        /**
         * Put query result to redis Cache
         * @param key
         * @param value
         */
        @Override
        public void putObject(Object key,Object value){
            RedisTemplate redisTemplate =getRedisTemplate();
            ValueOperations opsForValue = redisTemplate.opsForValue();
            opsForValue.set(key,value,EXPIRE_TIME_IN_MINUTES,TimeUnit.MINUTES);
            logger.debug("Put query result to redis ");
    
        }
        .....

篇幅问题，具体代码参考：[RedisCache](https://github.com/Angelswen/SpringbootWebLearning/blob/master/SpringbootWebLearning/src/main/java/com/vechace/weblearning/Cache/RedisCache.java)

RedisCache重写了以下方法，分别如下：

- String getId()：mybatis缓存操作对象的标识符，一个mapper对应一个mybatis的缓存操作对象。

- void putObject(Object key,Object value)：将查询结果存入redis缓存。

- Object getObject(Object key)：从redis中获取被缓存的查询结果。

- Object removeObject(Object key)：从缓存中删除对应的key、value。只有在回滚时触发。一般我们也可以不用实现，具体使用方式请参考：org.apache.ibatis.cache.decorators.TransactionalCache 。

- void clear()：发生更新时，清除缓存。

- int getSize()：可选实现。返回缓存的数量。

- ReadWriteLock getReadWriteLock()：可选实现，用于实现原子性的缓存操作。

关键点：

- 自己实现的二级缓存，须有一个带id的构造函数，否则会报错。

- 本案例使用spring封装的redisTemplate来操作redis，也可以使用jedis库，redisTemplate封装了底层的实现，在使用时可以不用关心redis连接的释放问题，对应jedis中的close()方法。

- 值得注意的是：这里不能通过@Autowire方式来引用redisTemplate，因为RedisCache不是Spring容器的bean，因此需要手动调用容器的getBean，实现代码参考这里：ApplicationContextHolder

- 案例采用的Redis序列化时默认的jdk序列化，所以数据实体类要说笑呢Serializable接口。

3、开启二级缓存：在对应的mapper.xml中开启

     <!-- 开启基于redis的二级缓存 -->
     <cache type="com.vechace.weblearning.Cache.RedisCache"/>

**测试**

**配置H2内存数据**

至此我们已经完成了所有代码的开发，接下来我们需要书写单元测试代码来测试我们代码的质量。我们刚才开发的过程中采用的是mysql数据库，而一般我们在测试时经常采用的是内存数据库。这里我们使用H2作为我们测试场景中使用的数据库。

要使用H2也很简单，只需要跟使用mysql时配置一下即可。在application.yml文件中：

    ---
    # 另起一个测试配置数据源
    spring:
      profiles: test
      datasource:
        url: jdbc:h2:mem:test
        username: root
        password: 123456
        driver-class-name: org.h2.Driver
        # 配置测试数据源SQL
        schema: classpath:schema.sql
        data: classpath:data.sql
    

为了避免和默认的配置冲突，我们用---另起一段，并且用profiles: test表明这是test环境下的配置。然后只要在我们的测试类中加上@ActiveProfiles(profiles = "test")注解来启用test环境下的配置，这样就能从mysql数据库切换到h2数据库。

在上述配置中，schema.sql用于存放我们的建表语句，data.sql用于存放insert的数据。这样当我们测试时，h2就会读取这两个文件，初始化我们所需要的表结构以及数据，然后在测试结束时销毁，不会对我们的mysql数据库产生任何影响。这就是内存数据库的好处。另外，也要在pom.xml中将h2的依赖的scope设置为test。

 **编写测试用例**

在Spring boot初始化过程中，会自动生成一个测试类：WeblearningApplicationTests

 Spring Boot提供了一些方便我们进行Web接口测试的工具类，比如TestRestTemplate。然后在配置文件中我们将log等级调成DEBUG，方便观察调试日志。具体的测试代码如下 ：

    package com.vechace.weblearning;
    
    import com.vechace.weblearning.Dao.domain.Product;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.context.embedded.LocalServerPort;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.web.client.TestRestTemplate;
    import org.springframework.test.context.ActiveProfiles;
    import org.springframework.test.context.junit4.SpringRunner;
    
    import java.util.Random;
    
    /**
     * 单元测试：模拟两次请求
     * Created by vechace on 2018/6/23
     */
    
    @RunWith(SpringRunner.class)
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @ActiveProfiles(profiles = "test")
    public class WeblearningApplicationTests {
    
        @LocalServerPort
        private int port;
    
        //测试模板
        @Autowired
        private TestRestTemplate restTemplate;
    
        @Test
        public void test() {
            long productId = 1;
            Product product = restTemplate.getForObject("http://localhost:" +port +"/product/" + productId,Product.class);
            if(product.getPrice() == 200){
                System.out.println("success");
            }
    
            Product newProduct = new Product();
            long newPrice = new Random().nextLong();
            int newQuantity = new Random().nextInt();
    
            newProduct.setName("newName");
            newProduct.setPrice(newPrice);
            newProduct.setQuantity(newQuantity);
            restTemplate.put("http://localhost:" +port+ "/product/" +productId,newProduct);
    
            Product testProduct = restTemplate.getForObject("http://localhost:" +port + "/product/" +productId,Product.class);
    
            if(testProduct.getPrice() == newPrice){
                System.out.println("success");
            }
    
        }
    
    }
    

测试说明：

- 首先调用get接口，此次操作结果会被存入redis。
- 然后再调用put接口更新该数据，此时的redis缓存会失效。
- 最后再次调用get接口，判断是否获取到了新的product对象。如果获取到老的对象，说明缓存失效的代码执行失败，代码存在错误，反之则说明代码无误。

 **测试结果**

在IDEA中运行测试用例，可以观察到测试结果，如下：

![测试结果](https://raw.githubusercontent.com/Angelswen/SpringbootWebLearning/master/imge/%E6%B5%8B%E8%AF%95%E7%BB%93%E6%9E%9C.png)


绿色显示测试通过。

---

参考资料：

1、Springboot开发文档：http://spring.io/projects/spring-boot

2、掘金文章：https://juejin.im/post/592c08292f301e006c60cae2#comment，内容有修改。

 

 

 
