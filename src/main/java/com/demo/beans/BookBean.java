package com.demo.beans;

import com.demo.models.Book;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class BookBean {

    public List<Book> getAllBooks()
    {
        return new ArrayList<>();
    }

    public Book addBook(String name, String author, String year, BigDecimal price)
    {
        Book book = new Book();
        book.setName(name);
        book.setAuthor(author);
        book.setYear(year);
        book.setPrice(price);
        if (book.getYear().compareTo("2000") != 0)
        {
            book.setId((int)Math.random() * 10 + 1);
        }
        return book;
    }
}
