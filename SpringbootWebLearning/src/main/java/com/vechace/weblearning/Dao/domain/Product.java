package com.vechace.weblearning.Dao.domain;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1435515995276255188L;

    private long id;
    private String name;
    private long price;
    private int quantity;

    //setter and getter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

