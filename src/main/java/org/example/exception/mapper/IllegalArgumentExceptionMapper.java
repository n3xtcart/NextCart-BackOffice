package org.example.exception.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        String message = exception.getMessage() != null ? exception.getMessage() : "Argomento non valido.";
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorMessage(message))
                .build();
    }
}