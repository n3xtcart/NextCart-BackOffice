package org.example.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.example.dto.RichiestaLoginDTO;
import org.example.dto.UtenteDTO;
import org.example.exception.EccezioneAutenticazione;
import org.example.exception.mapper.ErrorMessage;
import org.example.service.ServizioAutenticazione;
import org.example.util.TokenUtils;

import java.util.Map;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticazione", description = "API per l'autenticazione degli utenti amministratori")
public class AuthResource {

    @Inject
    ServizioAutenticazione servizioAutenticazione;

    @Inject
    JsonWebToken jwt;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    private static final long DEFAULT_TOKEN_DURATION_SECONDS = 3600L;

    @OPTIONS
    @Path("/login")
    @PermitAll
    public Response handleLoginPreflight() {
        System.out.println("AuthResource: Richiesta OPTIONS a /api/v1/auth/login ricevuta esplicitamente.");

        return Response.ok().build();
    }


    @OPTIONS
    @PermitAll
    public Response handleBaseAuthPreflight() {
        System.out.println("AuthResource: Richiesta OPTIONS generica a /api/v1/auth ricevuta esplicitamente.");
        return Response.ok().build();
    }


    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Effettua il login amministratore",
            description = "Autentica un utente amministratore e restituisce un token JWT in caso di successo.")
    @RequestBody(description = "Credenziali di login dell'utente",
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RichiestaLoginDTO.class)))
    @APIResponse(responseCode = "200", description = "Login riuscito",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = LoginResponse.class)))
    @APIResponse(responseCode = "401", description = "Credenziali non valide",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    @APIResponse(responseCode = "400", description = "Richiesta malformata (es. validazione fallita)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response login(@Valid RichiestaLoginDTO richiestaLogin) {
        System.out.println("AuthResource: Ricevuta richiesta POST a /api/v1/auth/login");
        try {
            UtenteDTO utenteAutenticato = servizioAutenticazione.login(richiestaLogin);

            if (!"admin".equalsIgnoreCase(utenteAutenticato.getRuolo())) {
                System.err.println("AuthResource: Tentativo di login per utente non admin: " + utenteAutenticato.getEmail());
                throw new EccezioneAutenticazione("Accesso negato. L'utente non ha i privilegi di amministratore.");
            }

            System.out.println("AuthResource: Login per admin " + utenteAutenticato.getEmail() + " riuscito. Generazione token...");
            String token = TokenUtils.generateToken(utenteAutenticato, issuer, DEFAULT_TOKEN_DURATION_SECONDS);
            System.out.println("AuthResource: Token generato.");

            LoginResponse loginResponse = new LoginResponse(token, utenteAutenticato);
            return Response.ok(loginResponse).build();

        } catch (EccezioneAutenticazione e) {
            System.err.println("AuthResource: EccezioneAutenticazione: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            System.err.println("AuthResource: IllegalArgumentException: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("AuthResource: Errore imprevisto durante il login: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorMessage("Errore interno del server durante il tentativo di login."))
                    .build();
        }
    }

    @GET
    @Path("/me")
    @RolesAllowed("admin")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Informazioni sull'utente corrente",
            description = "Restituisce informazioni sull'utente amministratore attualmente autenticato.")
    @APIResponse(responseCode = "200", description = "Informazioni utente recuperate",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UtenteDTO.class)))
    @APIResponse(responseCode = "401", description = "Non autorizzato o token non valido/mancante")
    public Response me() {
        if (jwt == null || jwt.getName() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorMessage("Token JWT non valido o mancante.")).build();
        }
        UtenteDTO utenteInfo = new UtenteDTO(null, jwt.getName(), String.join(", ", jwt.getGroups()));
        return Response.ok(utenteInfo).build();
    }

    public static class LoginResponse {
        public String token;
        public UtenteDTO user;

        public LoginResponse(String token, UtenteDTO user) {
            this.token = token;
            this.user = user;
        }
    }
}