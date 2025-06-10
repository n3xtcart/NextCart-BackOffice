package org.example.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.example.dto.TipologiaDTO;
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.exception.mapper.ErrorMessage;
import org.example.service.ServizioTipologia;

import java.net.URI;
import java.util.List;

@Path("/api/v1/tipologie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Gestione Tipologie", description = "API per la gestione delle tipologie di prodotto.")
@RolesAllowed("admin")
@SecurityRequirement(name = "bearerAuth")
public class TipologiaResource {

    @Inject
    ServizioTipologia servizioTipologia;

    @GET
    @Operation(summary = "Lista tutte le tipologie")
    @APIResponse(responseCode = "200", description = "Elenco tipologie recuperato",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TipologiaDTO[].class)))
    public Response trovaTutte() {
        List<TipologiaDTO> dtos = servizioTipologia.trovaTutte();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Trova tipologia per ID")
    @APIResponse(responseCode = "200", description = "Tipologia trovata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TipologiaDTO.class)))
    @APIResponse(responseCode = "404", description = "Tipologia non trovata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response trovaPerId(
            @Parameter(description = "ID della tipologia da recuperare", required = true)
            @PathParam("id") Long id) {
        return servizioTipologia.trovaPerId(id)
                .map(dto -> Response.ok(dto).build())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Tipologia non trovata con ID: " + id));
    }

    @POST
    @Operation(summary = "Crea una nuova tipologia")
    @RequestBody(description = "Dati della nuova tipologia", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TipologiaDTO.class)))
    @APIResponse(responseCode = "201", description = "Tipologia creata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TipologiaDTO.class)))
    @APIResponse(responseCode = "400", description = "Dati input non validi")
    @APIResponse(responseCode = "409", description = "Nome tipologia già esistente")
    public Response crea(@Valid TipologiaDTO dto) {
        if (dto.getId() != null) {
            throw new WebApplicationException("L'ID deve essere nullo per la creazione.", Response.Status.BAD_REQUEST);
        }
        TipologiaDTO salvata = servizioTipologia.salva(dto);
        URI location = UriBuilder.fromResource(TipologiaResource.class).path("/{id}").build(salvata.getId());
        return Response.created(location).entity(salvata).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Aggiorna una tipologia esistente")
    @RequestBody(description = "Dati aggiornati della tipologia", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TipologiaDTO.class)))
    @APIResponse(responseCode = "200", description = "Tipologia aggiornata")
    @APIResponse(responseCode = "400", description = "Dati input non validi / ID mismatch")
    @APIResponse(responseCode = "404", description = "Tipologia non trovata")
    @APIResponse(responseCode = "409", description = "Nome tipologia già in uso")
    public Response aggiorna(
            @Parameter(description = "ID della tipologia da aggiornare", required = true)
            @PathParam("id") Long id,
            @Valid TipologiaDTO dto) {
        if (dto.getId() != null && !id.equals(dto.getId())) {
            throw new WebApplicationException("L'ID nel path (" + id + ") non corrisponde all'ID nel corpo (" + dto.getId() + ").", Response.Status.BAD_REQUEST);
        }
        dto.setId(id);
        try {
            TipologiaDTO aggiornata = servizioTipologia.modifica(dto);
            return Response.ok(aggiornata).build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Elimina una tipologia")
    @APIResponse(responseCode = "204", description = "Tipologia eliminata")
    @APIResponse(responseCode = "404", description = "Tipologia non trovata")
    @APIResponse(responseCode = "409", description = "Impossibile eliminare, tipologia in uso")
    public Response elimina(
            @Parameter(description = "ID della tipologia da eliminare", required = true)
            @PathParam("id") Long id) {
        try {
            servizioTipologia.elimina(id);
            return Response.noContent().build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        } catch (EccezioneAccessoDati e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.CONFLICT);
        }
    }
}