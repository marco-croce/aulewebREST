package org.univaq.swa.auleweb.aulewebrest.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.univaq.swa.auleweb.aulewebrest.model.Aula;

public class AttrezzatureResource {

    private final Aula a;

    AttrezzatureResource(Aula a) {
        this.a = a;
    }

    @GET
    @Produces("application/json")
    public Response getCollection() {

        return Response.ok(a.getAttrezzatura()).build();
    }

}
