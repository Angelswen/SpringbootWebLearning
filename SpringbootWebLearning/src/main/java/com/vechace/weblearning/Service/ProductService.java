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
