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
import org.example.dto.ProdottoDTO;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.exception.mapper.ErrorMessage;
import org.example.service.ServizioProdotto;

import java.net.URI;
import java.util.List;

@Path("/api/v1/prodotti")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Gestione Prodotti", description = "API per la gestione completa dei prodotti.")
@RolesAllowed("admin")
@SecurityRequirement(name = "bearerAuth")
public class ProdottoResource {

    @Inject
    ServizioProdotto servizioProdotto;

    @GET
    @Operation(summary = "Lista tutti i prodotti",
            description = "Restituisce un elenco completo di tutti i prodotti presenti nel sistema.")
    @APIResponse(responseCode = "200", description = "Elenco prodotti recuperato",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdottoDTO[].class)))
    public Response trovaTutti() {
        List<ProdottoDTO> prodotti = servizioProdotto.trovaTutti();
        return Response.ok(prodotti).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Trova un prodotto per ID",
            description = "Restituisce i dettagli di un singolo prodotto specificato dal suo ID.")
    @APIResponse(responseCode = "200", description = "Prodotto trovato",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdottoDTO.class)))
    @APIResponse(responseCode = "404", description = "Prodotto non trovato",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response trovaPerId(
            @Parameter(description = "ID del prodotto da recuperare", required = true)
            @PathParam("id") Long id) {
        return servizioProdotto.trovaPerId(id)
                .map(prodotto -> Response.ok(prodotto).build())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Prodotto non trovato con ID: " + id));
    }

    @GET
    @Path("/categoria/{categoriaId}")
    @Operation(summary = "Trova prodotti per ID categoria",
            description = "Restituisce un elenco di prodotti appartenenti a una specifica categoria.")
    @APIResponse(responseCode = "200", description = "Elenco prodotti per categoria recuperato",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdottoDTO[].class)))
    @APIResponse(responseCode = "404", description = "Categoria non trovata (se si volesse validare l'ID categoria qui)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response trovaPerCategoria(
            @Parameter(description = "ID della categoria per cui filtrare i prodotti", required = true)
            @PathParam("categoriaId") Long categoriaId) {

        List<ProdottoDTO> prodotti = servizioProdotto.trovaPerIdCategoria(categoriaId);
        return Response.ok(prodotti).build();
    }

    @POST
    @Operation(summary = "Crea un nuovo prodotto",
            description = "Aggiunge un nuovo prodotto al sistema. Richiede ID validi per categoria, tipologia e unità di misura.")
    @RequestBody(description = "Dati del nuovo prodotto da creare (l'ID del prodotto deve essere nullo)", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdottoDTO.class)))
    @APIResponse(responseCode = "201", description = "Prodotto creato con successo",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdottoDTO.class)))
    @APIResponse(responseCode = "400", description = "Dati di input non validi (es. ID prodotto fornito, ID relazioni mancanti/non validi)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    @APIResponse(responseCode = "404", description = "Una delle entità referenziate (categoria, tipologia, unità misura) non trovata",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorMessage.class)))
    public Response crea(@Valid ProdottoDTO prodottoDTO) {
        if (prodottoDTO.getId() != null) {
            throw new WebApplicationException("L'ID del prodotto deve essere nullo per la creazione.", Response.Status.BAD_REQUEST);
        }
        try {
            ProdottoDTO prodottoSalvato = servizioProdotto.salva(prodottoDTO);
            URI location = UriBuilder.fromResource(ProdottoResource.class).path("/{id}").build(prodottoSalvato.getId());
            return Response.created(location).entity(prodottoSalvato).build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Aggiorna un prodotto esistente",
            description = "Modifica i dati di un prodotto esistente identificato dal suo ID.")
    @RequestBody(description = "Dati aggiornati del prodotto", required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdottoDTO.class)))
    @APIResponse(responseCode = "200", description = "Prodotto aggiornato con successo",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdottoDTO.class)))
    @APIResponse(responseCode = "400", description = "Dati di input non validi o ID nel path e corpo non corrispondenti")
    @APIResponse(responseCode = "404", description = "Prodotto o una delle sue entità referenziate non trovata")
    public Response aggiorna(
            @Parameter(description = "ID del prodotto da aggiornare", required = true)
            @PathParam("id") Long id,
            @Valid ProdottoDTO prodottoDTO) {
        if (prodottoDTO.getId() != null && !id.equals(prodottoDTO.getId())) {
            throw new WebApplicationException("L'ID nel path (" + id + ") non corrisponde all'ID nel corpo (" + prodottoDTO.getId() + ").", Response.Status.BAD_REQUEST);
        }
        prodottoDTO.setId(id);
        try {
            ProdottoDTO prodottoAggiornato = servizioProdotto.modifica(prodottoDTO);
            return Response.ok(prodottoAggiornato).build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Elimina un prodotto",
            description = "Rimuove un prodotto dal sistema in base al suo ID.")
    @APIResponse(responseCode = "204", description = "Prodotto eliminato con successo")
    @APIResponse(responseCode = "404", description = "Prodotto non trovato per l'eliminazione")
    public Response elimina(
            @Parameter(description = "ID del prodotto da eliminare", required = true)
            @PathParam("id") Long id) {
        try {
            servizioProdotto.elimina(id);
            return Response.noContent().build();
        } catch (EccezioneRisorsaNonTrovata e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.NOT_FOUND);
        }
    }
}