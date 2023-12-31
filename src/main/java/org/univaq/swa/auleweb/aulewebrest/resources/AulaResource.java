package org.univaq.swa.auleweb.aulewebrest.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.univaq.swa.auleweb.aulewebrest.exceptions.RESTWebApplicationException;
import org.univaq.swa.auleweb.aulewebrest.model.Aula;

public class AulaResource {

    private final Aula a;

    AulaResource(Aula a) {
        this.a = a;
    }

    private Map<String, Object> createAulaBase(Aula a) {
        Map<String, Object> aula = new HashMap<>();
        aula.put("nome", a.getNome());
        aula.put("luogo", a.getLuogo());
        aula.put("edificio", a.getEdificio());
        aula.put("piano", a.getPiano());
        aula.put("email_responsabile", a.getEmailResponsabile());
        return aula;
    }

    @GET
    @Produces("application/json")
    public Response getItem() {
        try {
            Map<String, Object> aulaBase = createAulaBase(a);
            return Response.ok(aulaBase)
                    .build();
        } catch (Exception e) {
            throw new RESTWebApplicationException(e);
        }
    }

    @Path("attrezzature")
    public AttrezzatureResource getAttrezzatura() throws SQLException {
        return new AttrezzatureResource(a);
    }

}
