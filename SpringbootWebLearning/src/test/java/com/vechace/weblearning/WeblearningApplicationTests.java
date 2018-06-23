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
