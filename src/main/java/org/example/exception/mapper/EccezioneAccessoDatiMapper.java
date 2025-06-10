package org.example.exception.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.example.exception.EccezioneAccessoDati;
import org.hibernate.exception.ConstraintViolationException;

@Provider
public class EccezioneAccessoDatiMapper implements ExceptionMapper<EccezioneAccessoDati> {
    @Override
    public Response toResponse(EccezioneAccessoDati exception) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        String message = "Errore interno del server durante l'accesso ai dati.";

        Throwable cause = exception.getCause();
        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) cause;
            if (cve.getSQLState() != null && (cve.getSQLState().equals("23505") || cve.getSQLState().equals("23000") || (cve.getMessage() != null && cve.getMessage().toLowerCase().contains("duplicate entry")))) {
                status = Response.Status.CONFLICT;
                message = "Violazione di un vincolo di unicità: la risorsa o un campo univoco esiste già.";
                if (exception.getMessage() != null && !exception.getMessage().startsWith("Errore")) {
                    message = exception.getMessage();
                }
            } else {
                status = Response.Status.BAD_REQUEST;
                message = "Violazione di un vincolo del database.";
                if (exception.getMessage() != null && !exception.getMessage().startsWith("Errore")) {
                    message = exception.getMessage();
                }
            }
        } else if (exception.getMessage() != null && !exception.getMessage().startsWith("Errore")) {
            message = exception.getMessage();
            if (message.toLowerCase().contains("già esistente")) {
                status = Response.Status.CONFLICT;
            }
        }
        return Response.status(status).entity(new ErrorMessage(message)).build();
    }
}