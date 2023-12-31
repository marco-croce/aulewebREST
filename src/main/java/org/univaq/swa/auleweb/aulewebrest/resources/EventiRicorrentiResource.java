package org.univaq.swa.auleweb.aulewebrest.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.univaq.swa.auleweb.aulewebrest.exceptions.RESTWebApplicationException;

// PATH -> /rest/eventiricorrenti
@Path("eventiricorrenti")
public class EventiRicorrentiResource {

    Class c = Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/auleweb?serverTimezone=Europe/Rome", "aulewebsite", "aulewebpass");

    public EventiRicorrentiResource() throws SQLException, ClassNotFoundException {

    }

    private Map<String, Object> createEventoRicorrente(ResultSet rs) {
        try {
            Map<String, Object> evento = new HashMap<>();

            evento.put("ID", rs.getInt("ID"));
            evento.put("data_inizio", LocalDateTime.parse(rs.getDate("data_inizio").toLocalDate()
                    .atTime(rs.getTime("data_inizio").toLocalTime()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
            evento.put("data_fine", LocalDateTime.parse(rs.getDate("data_fine").toLocalDate()
                    .atTime(rs.getTime("data_fine").toLocalTime()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
            evento.put("nome", rs.getString("nome"));
            evento.put("descrizione", rs.getString("descrizione"));
            evento.put("email_responsabile", rs.getString("email_responsabile"));
            evento.put("tipologia", rs.getString("tipologia"));
            evento.put("nome_corso", rs.getString("nome_corso"));
            evento.put("tipo_ricorrenza", rs.getString("tipo_ricorrenza"));
            if (rs.getDate("data_fine_ricorrenza") != null) {
                evento.put("data_fine_ricorrenza",
                        rs.getDate("data_fine_ricorrenza").toLocalDate().format(DateTimeFormatter.ISO_DATE));
            } else {
                evento.put("data_fine_ricorrenza", "");
            }

            return evento;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
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
    @Path("{eventoRic}")
    @Produces("application/json")
    public Response getItem(@PathParam("eventoRic") int idEventoRic, @Context UriInfo uriInfo) throws SQLException {
        try ( PreparedStatement sEvento = con.prepareStatement("SELECT * FROM evento_ricorrente WHERE ID=?")) {
            sEvento.setInt(1, idEventoRic);
            try ( ResultSet rs = sEvento.executeQuery()) {
                if (rs.next()) {
                    try ( PreparedStatement sEventoMaster = con.prepareStatement("SELECT * FROM evento WHERE ID=?")) {
                        sEventoMaster.setInt(1, rs.getInt("ID_master"));
                        try ( ResultSet rsMaster = sEventoMaster.executeQuery()) {
                            if (rsMaster.next()) {
                                try ( PreparedStatement sAula = con.prepareStatement("SELECT * FROM aula a WHERE a.ID=?")) {
                                    sAula.setInt(1, rsMaster.getInt("ID_aula"));
                                    try ( ResultSet rsAula = sAula.executeQuery()) {
                                        if (rsAula.next()) {
                                            try ( PreparedStatement sAttrezzatura = con.prepareStatement(
                                                    "SELECT att.* FROM aula a JOIN attrezzatura att ON a.ID = att.ID_aula WHERE a.ID = ?")) {
                                                sAttrezzatura.setInt(1, rsMaster.getInt("ID_aula"));
                                                Map<String, Object> evento = createEventoRicorrente(rsMaster);
                                                // Cambio della data di inizio e della data di fine
                                                evento.put("data_inizio", LocalDateTime.parse(rs.getDate("data_inizio").toLocalDate()
                                                        .atTime(rs.getTime("data_inizio").toLocalTime()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                                                evento.put("data_fine", LocalDateTime.parse(rs.getDate("data_fine").toLocalDate()
                                                        .atTime(rs.getTime("data_fine").toLocalTime()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                                                Map<String, Object> aula = createAula(rsAula);
                                                List<Map<String, Object>> attrezzature = new ArrayList<>();
                                                try ( ResultSet rsAttrezzatura = sAttrezzatura.executeQuery()) {
                                                    while (rsAttrezzatura.next()) {
                                                        attrezzature.add(createAttrezzatura(rsAttrezzatura));
                                                    }
                                                    aula.put("attrezzatura", attrezzature);
                                                    evento.put("aula", aula);
                                                    return Response.ok(evento).build();
                                                }
                                            }
                                        } else {
                                            throw new RESTWebApplicationException(404, "Aula non trovata");
                                        }
                                    }
                                }
                            } else {
                                throw new RESTWebApplicationException(404, "Evento master non trovato");
                            }
                        }
                    }
                } else {
                    throw new RESTWebApplicationException(404, "Evento ricorrente non trovato");
                }
            } catch (Exception e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }

}
