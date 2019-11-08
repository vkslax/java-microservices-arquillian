package com.demo.models;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class Book {
    private Integer id;

    private String name;

    private String author;

    private String year;

    private BigDecimal price;

}
