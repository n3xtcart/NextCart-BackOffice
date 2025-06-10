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
import org.example.dto.UnitaMisuraDTO;
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.exception.mapper.ErrorMessage;
import org.example.service.ServizioUnitaMisura;

import java.net.URI;
import java.util.List;

@Path("/api/v1/unita-misura")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Gestione Unità di Misura", description = "API per la gestione delle unità di misura dei prodotti.")
@RolesAllowed("admin")
@SecurityRequirement(name = "bearerAuth")
public class UnitaMisuraResource {

    @Inject
    ServizioUnitaMisura servizioUnitaMisura;

    @GET
    @Operation(summary = "Lista tutte le unità di misura")
    @APIResponse(responseCode = "200", description = "Elenco unità di misura recuperato",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UnitaMisuraDTO[].class)))
    public Response trovaTutte() {
        List<UnitaMisuraDTO> dtos = servizioUnitaMisura.trovaTutte();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Trova unità di misura per ID")
    @APIResponse(responseCode = "200", description = "Unità di misura trovata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UnitaMisuraDTO.class)))
    @APIResponse(responseCode = "404", description = "Unità di misura non trovata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response trovaPerId(
            @Parameter(description = "ID dell'unità di misura da recuperare", required = true)
            @PathParam("id") Long id) {
        return servizioUnitaMisura.trovaPerId(id)
                .map(dto -> Response.ok(dto).build())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Unità di misura non trovata con ID: " + id));
    }

    @POST
    @Operation(summary = "Crea una nuova unità di misura")
    @RequestBody(description = "Dati della nuova unità di misura", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UnitaMisuraDTO.class)))
    @APIResponse(responseCode = "201", description = "Unità di misura creata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UnitaMisuraDTO.class)))
    @APIResponse(responseCode = "400", description = "Dati input non validi")
    @APIResponse(responseCode = "409", description = "Nome unità di misura già esistente")
    public Response crea(@Valid UnitaMisuraDTO dto) {
        if (dto.getId() != null) {
            throw new WebApplicationException("L'ID deve essere nullo per la creazione.", Response.Status.BAD_REQUEST);
        }
        UnitaMisuraDTO salvata = servizioUnitaMisura.salva(dto);
        URI location = UriBuilder.fromResource(UnitaMisuraResource.class).path("/{id}").build(salvata.getId());
        return Response.created(location).entity(salvata).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Aggiorna un'unità di misura esistente")
    @RequestBody(description = "Dati aggiornati dell'unità di misura", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UnitaMisuraDTO.class)))
    @APIResponse(responseCode = "200", description = "Unità di misura aggiornata")
    @APIResponse(responseCode = "400", description = "Dati input non validi / ID mismatch")
    @APIResponse(responseCode = "404", description = "Unità di misura non trovata")
    @APIResponse(responseCode = "409", description = "Nome unità di misura già in uso")
    public Response aggiorna(
            @Parameter(description = "ID dell'unità di misura da aggiornare", required = true)
            @PathParam("id") Long id,
            @Valid UnitaMisuraDTO dto) {
        if (dto.getId() != null && !id.equals(dto.getId())) {
            throw new WebApplicationException("L'ID nel path (" + id + ") non corrisponde all'ID nel corpo (" + dto.getId() + ").", Response.Status.BAD_REQUEST);
        }
        dto.setId(id);
        try {
            UnitaMisuraDTO aggiornata = servizioUnitaMisura.modifica(dto);
            return Response.ok(aggiornata).build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Elimina un'unità di misura")
    @APIResponse(responseCode = "204", description = "Unità di misura eliminata")
    @APIResponse(responseCode = "404", description = "Unità di misura non trovata")
    @APIResponse(responseCode = "409", description = "Impossibile eliminare, unità di misura in uso")
    public Response elimina(
            @Parameter(description = "ID dell'unità di misura da eliminare", required = true)
            @PathParam("id") Long id) {
        try {
            servizioUnitaMisura.elimina(id);
            return Response.noContent().build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        } catch (EccezioneAccessoDati e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.CONFLICT);
        }
    }
}