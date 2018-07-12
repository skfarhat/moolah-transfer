package moolah.exceptions.web;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
public class BadRequestException extends BaseWebApplicationException {

    private static final Response.Status STATUS = Response.Status.BAD_REQUEST;

    /**
     * Create a HTTP 400 (Bad Request) exception.
     */
    public BadRequestException() {
        this("");
    }

    /**
     * Create a HTTP 400 (Bad Request) exception.
     * @param message the String that is the entity of the 400 response.
     */
    public BadRequestException(String message) {
        super(Response.status(STATUS).entity(toMap(message, STATUS))
                .type(MediaType.APPLICATION_JSON).build());
    }
}
