package org.first;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello/{name}")
public class FirstResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("name") String name ) {
        return "Hello " + name + "!";
    }
}