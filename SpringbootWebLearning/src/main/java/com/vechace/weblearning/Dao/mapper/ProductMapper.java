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
