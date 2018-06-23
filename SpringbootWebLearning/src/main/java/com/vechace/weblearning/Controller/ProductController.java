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
