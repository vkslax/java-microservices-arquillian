package com.demo.beans;

import com.demo.models.Book;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.math.BigDecimal;
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

    @Test
    public void testCreateBook ()
    {
        Book createdBook = bookBean.addBook(
                "A Song of Ice and Fire",
                "George R.R. Martin",
                "2000",
                BigDecimal.TEN
        );

        assertNotNull(createdBook);
        assertNotNull(createdBook.getId());
    }
}
