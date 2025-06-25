// src/main/java/org/example/resource/ProdottoResource.java
package org.example.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.nextre.nextcart.dto.ProdottoDTO;
import it.nextre.nextcart.service.ServizioProdotto;
import org.example.exception.EccezioneRisorsaNonTrovata;

import java.net.URI;
import java.util.List;

@Path("/api/v1/prodotti")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
public class ProdottoResource {

    @Inject
    ServizioProdotto servizioProdotto;

    @GET
    public Response trovaTutti() {
        List<ProdottoDTO> prodotti = servizioProdotto.trovaTutti();
        return Response.ok(prodotti).build();
    }

    @GET
    @Path("/{id}")
    public Response trovaPerId(@PathParam("id") Long id) {
        return servizioProdotto.trovaPerId(id)
                .map(p -> Response.ok(p).build())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Prodotto non trovato con ID: " + id));
    }

    @GET
    @Path("/categoria/{categoriaId}")
    public Response trovaPerCategoria(@PathParam("categoriaId") Long categoriaId) {
        if (categoriaId == null || categoriaId <= 0) {
            throw new WebApplicationException("ID categoria non valido.", Response.Status.BAD_REQUEST);
        }
        List<ProdottoDTO> prodotti = servizioProdotto.trovaPerIdCategoria(categoriaId);
        return Response.ok(prodotti).build();
    }

    @POST
    public Response crea(@Valid ProdottoDTO dto, @Context UriInfo uriInfo) {
        if (dto.getId() != null) {
            throw new WebApplicationException("L'ID del prodotto deve essere nullo per la creazione.", Response.Status.BAD_REQUEST);
        }
        ProdottoDTO salvato = servizioProdotto.salva(dto);
        URI location = uriInfo.getAbsolutePathBuilder().path(salvato.getId().toString()).build();
        return Response.created(location).entity(salvato).build();
    }

    @PUT
    @Path("/{id}")
    public Response aggiorna(@PathParam("id") Long id, @Valid ProdottoDTO dto) {
        if (dto.getId() != null && !id.equals(dto.getId())) {
            throw new WebApplicationException(
                    "L'ID nel path (" + id + ") non corrisponde all'ID nel corpo (" + dto.getId() + ").",
                    Response.Status.BAD_REQUEST
            );
        }
        dto.setId(id);
        ProdottoDTO aggiornato = servizioProdotto.modifica(dto);
        return Response.ok(aggiornato).build();
    }

    @DELETE
    @Path("/{id}")
    public Response elimina(@PathParam("id") Long id) {
        servizioProdotto.elimina(id);
        return Response.noContent().build();
    }
}
