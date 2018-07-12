package moolah.exceptions.web;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotFoundException extends BaseWebApplicationException {

    private static final Response.Status STATUS = Response.Status.NOT_FOUND;

    /**
     * Create a HTTP 404 (Not Found) exception.
     */
    public NotFoundException() {
        this("");
    }

    /**
     * Create a HTTP 404 (Not Found) exception.
     * @param message the String that is the entity of the 404 response.
     */
    public NotFoundException(String message) {
        super(Response.status(STATUS).entity(toMap(message, STATUS))
                .type(MediaType.APPLICATION_JSON).build());
    }

}