package org.example.resource;

import it.nextre.nextcart.dto.CategoriaDTO;
import it.nextre.nextcart.service.ServizioCategoria;
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
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.exception.mapper.ErrorMessage;

import java.net.URI;
import java.util.List;

@Path("/api/v1/categorie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Gestione Categorie", description = "API per la creazione, lettura, aggiornamento ed eliminazione delle categorie di prodotti.")
@RolesAllowed("admin")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaResource {

    @Inject
    ServizioCategoria servizioCategoria;

    @GET
    @Operation(summary = "Lista tutte le categorie",
            description = "Restituisce un elenco di tutte le categorie disponibili nel sistema.")
    @APIResponse(responseCode = "200", description = "Elenco categorie recuperato con successo",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CategoriaDTO[].class)))
    public Response trovaTutte() {
        List<CategoriaDTO> categorie = servizioCategoria.trovaTutte();
        return Response.ok(categorie).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Trova una categoria per ID",
            description = "Restituisce i dettagli di una singola categoria specificata dal suo ID.")
    @APIResponse(responseCode = "200", description = "Categoria trovata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CategoriaDTO.class)))
    @APIResponse(responseCode = "404", description = "Categoria non trovata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response trovaPerId(
            @Parameter(description = "ID della categoria da recuperare", required = true)
            @PathParam("id") Long id) {
        return servizioCategoria.trovaPerId(id)
                .map(categoria -> Response.ok(categoria).build())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Categoria non trovata con ID: " + id));
    }

    @POST
    @Operation(summary = "Crea una nuova categoria",
            description = "Aggiunge una nuova categoria al sistema. L'ID deve essere nullo nel corpo della richiesta.")
    @RequestBody(description = "Dati della nuova categoria da creare", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CategoriaDTO.class)))
    @APIResponse(responseCode = "201", description = "Categoria creata con successo",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CategoriaDTO.class)))
    @APIResponse(responseCode = "400", description = "Dati di input non validi o ID fornito per nuova categoria",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    @APIResponse(responseCode = "409", description = "Categoria con lo stesso nome già esistente",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response crea(@Valid CategoriaDTO categoriaDTO) {
        if (categoriaDTO.getId() != null) {
            throw new WebApplicationException("L'ID deve essere nullo per la creazione di una nuova categoria.", Response.Status.BAD_REQUEST);
        }
        CategoriaDTO categoriaSalvata = servizioCategoria.salva(categoriaDTO);
        URI location = UriBuilder.fromResource(CategoriaResource.class).path("/{id}").build(categoriaSalvata.getId());
        return Response.created(location).entity(categoriaSalvata).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Aggiorna una categoria esistente",
            description = "Modifica i dati di una categoria esistente identificata dal suo ID.")
    @RequestBody(description = "Dati aggiornati della categoria", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CategoriaDTO.class)))
    @APIResponse(responseCode = "200", description = "Categoria aggiornata con successo",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CategoriaDTO.class)))
    @APIResponse(responseCode = "400", description = "Dati di input non validi o ID nel path e corpo non corrispondenti",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    @APIResponse(responseCode = "404", description = "Categoria non trovata per l'aggiornamento",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    @APIResponse(responseCode = "409", description = "Nome categoria già in uso da altra categoria",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response aggiorna(
            @Parameter(description = "ID della categoria da aggiornare", required = true)
            @PathParam("id") Long id,
            @Valid CategoriaDTO categoriaDTO) {
        if (categoriaDTO.getId() != null && !id.equals(categoriaDTO.getId())) {
            throw new WebApplicationException("L'ID nel path (" + id + ") non corrisponde all'ID nel corpo della richiesta (" + categoriaDTO.getId() + ").", Response.Status.BAD_REQUEST);
        }
        categoriaDTO.setId(id);
        try {
            CategoriaDTO categoriaAggiornata = servizioCategoria.modifica(categoriaDTO);
            return Response.ok(categoriaAggiornata).build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Elimina una categoria",
            description = "Rimuove una categoria dal sistema in base al suo ID.")
    @APIResponse(responseCode = "204", description = "Categoria eliminata con successo")
    @APIResponse(responseCode = "404", description = "Categoria non trovata per l'eliminazione",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    @APIResponse(responseCode = "409", description = "Impossibile eliminare, categoria in uso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response elimina(
            @Parameter(description = "ID della categoria da eliminare", required = true)
            @PathParam("id") Long id) {
        try {
            servizioCategoria.elimina(id);
            return Response.noContent().build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        } catch (EccezioneAccessoDati e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.CONFLICT);
        }
    }
}