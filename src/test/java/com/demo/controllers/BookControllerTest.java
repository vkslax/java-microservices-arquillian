package com.demo.controllers;

import com.demo.common.Deployments;
import com.demo.common.ReuseDeployment;
import com.demo.models.Book;
import javax.ws.rs.core.GenericType;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.HttpHeaders;
import java.util.List;

import static org.junit.Assert.*;

//Step x. Add tests extending Deployments
@RunWith(Arquillian.class)
@ReuseDeployment(Deployments.class)
public class BookControllerTest extends Deployments {
    @Test
    public void testGetAllBooksBean ()
    {
        //firebaseLogin("CREDENTIALS");

        List<Book> bookList = API.path("/book")
                .request()
                //.header(HttpHeaders.AUTHORIZATION, firebaseUser.getJwt())
                .get(new GenericType<List<Book>>() {});
        assertNotNull(bookList);
    }
}
