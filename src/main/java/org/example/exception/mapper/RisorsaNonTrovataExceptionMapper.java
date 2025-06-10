package org.example.exception.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.example.exception.EccezioneRisorsaNonTrovata;

@Provider
public class RisorsaNonTrovataExceptionMapper implements ExceptionMapper<EccezioneRisorsaNonTrovata> {
    @Override
    public Response toResponse(EccezioneRisorsaNonTrovata exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorMessage(exception.getMessage()))
                .build();
    }
}