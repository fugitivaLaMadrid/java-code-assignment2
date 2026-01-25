package com.fulfilment.application.monolith.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
public class ErrorMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = Logger.getLogger(ErrorMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof NotFoundException) {
            // Avoid logging framework-internal 404s (Swagger UI calls, etc.)
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        LOGGER.error("Failed to handle request", exception);

        int code = exception instanceof WebApplicationException webApplicationException
                ? webApplicationException.getResponse().getStatus()
                : 500;

        ObjectNode json = objectMapper.createObjectNode();
        json.put("exceptionType", exception.getClass().getName());
        json.put("code", code);
        json.put("error", exception.getMessage());

        return Response.status(code).entity(json).build();
    }
}
