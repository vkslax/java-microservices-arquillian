package com.demo.beans;

import com.demo.controllers.BookController;
import com.demo.models.Book;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;
import static org.junit.Assert.*;

//Step 3. Add tests, long method
@RunWith(Arquillian.class)
public class BookBeanTest {

    @EJB
    BookBean bookBean;

    @Deployment
    public static WebArchive createDeployment()
    {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(BookBean.class);
    }

    @Test
    public void testGetAllBooksBean ()
    {
        List<Book> bookList = bookBean.getAllBooks();
        assertNotNull(bookList);
    }
}
