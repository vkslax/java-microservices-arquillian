package com.demo.beans;

import com.demo.models.Book;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class BookBean {

    public List<Book> getAllBooks()
    {
        return new ArrayList<>();
    }
}
