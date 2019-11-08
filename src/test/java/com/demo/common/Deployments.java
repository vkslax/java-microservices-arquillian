package com.demo.common;

import lombok.NoArgsConstructor;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.Before;

import javax.ws.rs.client.Client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@NoArgsConstructor
public class Deployments {
    @ArquillianResource
    private URL base;

    /*
    @Inject
    @ConfigProperty(name = "firebase.auth.endpoint")
    String firebaseUrl;

    @Inject
    @ConfigProperty(name = "firebase.apikey")
    String firebaseAPIKey;
    */

    protected static WebTarget API;

    @Before
    public void setUpClass() throws MalformedURLException
    {
        //ClientConfig config = new ClientConfig();
        //config.register(ErrorFilter.class);
        Client client = ClientBuilder.newClient();
        API = client.target(URI.create(new URL(base, "api").toExternalForm()));
    }



    @Deployment
    public static WebArchive createDeployment()
    {
        return ShrinkWrap.create(WebArchive.class)
                //.addAsResource("META-INF/persistence.xml")
                //.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsWebInfResource("glassfish-resources.xml")
                .addPackages(true, "com.demo");
    }

    protected <T> Entity<T> entity(T request)
    {
        return Entity.entity(request, MediaType.APPLICATION_JSON_TYPE);
    }
}
