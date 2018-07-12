package moolah.exceptions.web;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class BaseWebApplicationException extends WebApplicationException {

    public static Map<String, String> toMap(String message, Response.Status status) {
        Map<String, String> map = new HashMap<>();
        map.put("status", Integer.toString(status.getStatusCode()));
        map.put("status_message", status.toString());
        map.put("message", message);
        return map;
    }

    public BaseWebApplicationException() {
    }

    public BaseWebApplicationException(String message) {
        super(message);
    }

    public BaseWebApplicationException(Response response) {
        super(response);
    }
}
