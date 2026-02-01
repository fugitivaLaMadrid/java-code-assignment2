package com.fulfilment.application.monolith.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
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
        int code = 500;

        if (exception instanceof WebApplicationException wae) {
            code = wae.getResponse().getStatus();
        }

        // Only log server-side errors (5xx) to avoid clutter
        if (code >= 500) {
            LOGGER.error("Failed to handle request", exception);
        } else {
            LOGGER.warnf("Handled client error (%d): %s", code, exception.getMessage());
        }

        ObjectNode json = objectMapper.createObjectNode();
        json.put("exceptionType", exception.getClass().getName());
        json.put("code", code);
        json.put("error", exception.getMessage() != null ? exception.getMessage() : "N/A");

        return Response.status(code).entity(json).build();
    }
}
