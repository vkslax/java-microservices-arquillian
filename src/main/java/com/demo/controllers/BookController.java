package com.demo.controllers;

import com.demo.beans.BookBean;
import com.demo.models.Book;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/book")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookController {

    @EJB
    BookBean bookBean;
    @GET
    public List<Book> getAllBooks()
    {
        return bookBean.getAllBooks();
    }
}
