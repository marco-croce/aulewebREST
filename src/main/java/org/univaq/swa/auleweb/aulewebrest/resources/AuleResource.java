package org.univaq.swa.auleweb.aulewebrest.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.univaq.swa.auleweb.aulewebrest.exceptions.RESTWebApplicationException;
import org.univaq.swa.auleweb.aulewebrest.model.Attrezzatura;
import org.univaq.swa.auleweb.aulewebrest.model.Aula;
import org.univaq.swa.auleweb.aulewebrest.security.Logged;

// PATH -> /rest/aule
@Path("aule")
public class AuleResource {

    Class c = Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/auleweb?serverTimezone=Europe/Rome", "aulewebsite", "aulewebpass");

    public AuleResource() throws SQLException, ClassNotFoundException {

    }

    private Aula createAula(Map<String, Object> aula) {
        List<Map<String, Object>> attrezzature = (List<Map<String, Object>>) aula.get("attrezzatura");

        Aula a = new Aula();

        a.setNome((String) aula.get("nome"));
        a.setLuogo((String) aula.get("luogo"));
        a.setEdificio((String) aula.get("edificio"));
        a.setPiano((Integer) aula.get("piano"));
        a.setCapienza((Integer) aula.get("capienza"));
        a.setEmailResponsabile((String) aula.get("email_responsabile"));
        a.setNumeroPreseRete((Integer) aula.get("numero_prese_rete"));
        a.setNumeroPreseElettriche((Integer) aula.get("numero_prese_elettriche"));
        a.setNote((String) aula.get("note"));

        for (Map<String, Object> attr : attrezzature) {
            Attrezzatura attrezzatura = new Attrezzatura();
            attrezzatura.setNumeroSeriale((String) attr.get("numero_seriale"));
            attrezzatura.setDescrizione((String) attr.get("descrizione"));
            a.addAttrezzatura(attrezzatura);
        }

        return a;
    }

    private Map<String, Object> createAula(ResultSet rs) {
        try {
            Map<String, Object> aula = new HashMap<>();

            aula.put("ID", rs.getInt("ID"));
            aula.put("nome", rs.getString("nome"));
            aula.put("luogo", rs.getString("luogo"));
            aula.put("edificio", rs.getString("edificio"));
            aula.put("piano", rs.getInt("piano"));
            aula.put("capienza", rs.getInt("capienza"));
            aula.put("email_responsabile", rs.getString("email_responsabile"));
            aula.put("numero_prese_rete", rs.getInt("numero_prese_rete"));
            aula.put("numero_prese_elettriche", rs.getInt("numero_prese_elettriche"));
            aula.put("note", rs.getString("note"));

            return aula;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    private Map<String, Object> createAttrezzatura(ResultSet rs) {
        try {
            Map<String, Object> attrezzatura = new HashMap<>();

            attrezzatura.put("ID", rs.getInt("ID"));
            attrezzatura.put("numero_seriale", rs.getString("numero_seriale"));
            attrezzatura.put("descrizione", rs.getString("descrizione"));

            return attrezzatura;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Produces("application/json")
    public Response getCollection(@Context UriInfo uriinfo) throws SQLException {
        PreparedStatement sAule = con.prepareStatement("SELECT ID FROM aula");
        List<String> aule = new ArrayList();
        try ( ResultSet rs = sAule.executeQuery()) {
            while (rs.next()) {
                URI uri = uriinfo.getBaseUriBuilder()
                        .path(AuleResource.class)
                        .path(AuleResource.class, "getItem")
                        .build(rs.getInt("ID"));
                aule.add(uri.toString());
            }
        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }
        return Response.ok(aule).build();
    }

    @Path("{aula}")
    public AulaResource getItem(
            @PathParam("aula") int idAula,
            @Context UriInfo uriInfo
    ) throws SQLException {

        try ( PreparedStatement sAula = con.prepareStatement("SELECT * FROM aula WHERE ID = ?")) {
            sAula.setInt(1, idAula);
            try ( ResultSet rs = sAula.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> aula = createAula(rs);
                    List<Map<String, Object>> attrezzatura = new ArrayList<>();
                    try ( PreparedStatement sAttrezzatura = con.prepareStatement("SELECT att.* FROM aula a JOIN attrezzatura att ON a.ID = att.ID_aula WHERE a.ID = ?")) {
                        sAttrezzatura.setInt(1, idAula);
                        try ( ResultSet rsAttrezzatura = sAttrezzatura.executeQuery()) {
                            while (rsAttrezzatura.next()) {
                                Map<String, Object> attr = createAttrezzatura(rsAttrezzatura);
                                attrezzatura.add(attr);
                            }
                        }

                        aula.put("attrezzatura", attrezzatura);
                    }
                    Aula a = createAula(aula);

                    return new AulaResource(a);
                } else {
                    throw new RESTWebApplicationException(404, "Aula non trovata");
                }
            } catch (SQLException e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }

    @POST
    @Logged
    @Consumes("application/json")
    @Produces("application/json")
    public Response addItem(@Context UriInfo uriinfo,
            Map<String, Object> aula) {
        String addAula = "INSERT INTO aula (nome, luogo, edificio, piano, capienza, email_responsabile, numero_prese_rete, numero_prese_elettriche, note)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try ( PreparedStatement ps = con.prepareStatement(addAula, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, (String) aula.get("nome"));
            ps.setString(2, (String) aula.get("luogo"));
            ps.setString(3, (String) aula.get("edificio"));
            ps.setInt(4, (Integer) aula.get("piano"));
            ps.setInt(5, (Integer) aula.get("capienza"));
            ps.setString(6, (String) aula.get("email_responsabile"));
            ps.setInt(7, (Integer) aula.get("numero_prese_rete"));
            ps.setInt(8, (Integer) aula.get("numero_prese_elettriche"));
            ps.setString(9, (String) aula.get("note"));

            ps.executeUpdate();

            try ( ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                int idAula = rs.getInt("ID");
                URI uri = uriinfo.getBaseUriBuilder()
                        .path(AuleResource.class)
                        .path(AuleResource.class, "getItem")
                        .build(idAula);
                return Response.created(uri).build();
            }
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

}
