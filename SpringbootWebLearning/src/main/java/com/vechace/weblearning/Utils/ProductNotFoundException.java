package com.vechace.weblearning.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 自定义异常：ProductNotFound异常，继承RuntimeException类
 * Create by vechace on 2018/6/23
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(long productId){
        super("Couldn't find the product '"+productId+"'.");
    }
}
