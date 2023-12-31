package org.univaq.swa.auleweb.aulewebrest.resources;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fortuna.ical4j.data.CalendarOutputter;
import org.univaq.swa.auleweb.aulewebrest.exceptions.RESTWebApplicationException;
import org.univaq.swa.auleweb.aulewebrest.model.Attrezzatura;
import org.univaq.swa.auleweb.aulewebrest.model.Aula;
import org.univaq.swa.auleweb.aulewebrest.model.Evento;
import org.univaq.swa.auleweb.aulewebrest.model.TipologiaEvento;
import org.univaq.swa.auleweb.aulewebrest.model.TipologiaRicorrenza;
import org.univaq.swa.auleweb.aulewebrest.result.ICalendarWriter;
import org.univaq.swa.auleweb.aulewebrest.security.Logged;

// PATH -> /rest/eventi
@Path("eventi")
public class EventiResource {

    Class c = Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/auleweb?serverTimezone=Europe/Rome", "aulewebsite", "aulewebpass");

    // Iniettiamo la ServletContext
    @Context
    private ServletContext servletContext;

    public EventiResource() throws SQLException, ClassNotFoundException {

    }
//calendar
    private Evento createEvento(Map<String, Object> evento) {
        Map<String, Object> aula = (Map<String, Object>) evento.get("aula");
        List<Map<String, Object>> attrezzature = (List<Map<String, Object>>) aula.get("attrezzatura");

        Evento e = new Evento();
        e.setDataInizio((LocalDateTime) evento.get("data_inizio"));
        e.setDataFine((LocalDateTime) evento.get("data_fine"));
        e.setNome((String) evento.get("nome"));
        e.setDescrizione((String) evento.get("descrizione"));
        e.setEmailResponsabile((String) evento.get("email_responsabile"));
        e.setTipologia(TipologiaEvento.valueOf((String) evento.get("tipologia")));
        if (e.getTipologia() == TipologiaEvento.ESAME
                || e.getTipologia() == TipologiaEvento.PARZIALE
                || e.getTipologia() == TipologiaEvento.LEZIONE) {
            e.setNomeCorso((String) evento.get("nome_corso"));
        }
        if ((String) evento.get("tipo_ricorrenza") != null) {
            e.setRicorrenza(TipologiaRicorrenza.valueOf((String) evento.get("tipo_ricorrenza")));
            e.setDataFineRicorrenza(LocalDate.parse((String) evento.get("data_fine_ricorrenza")));
        }
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

        e.setAula(a);

        return e;
    }
//creo json
    private Map<String, Object> createEvento(ResultSet rs) {
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
    @Path("{evento}")
    @Produces("application/json")
    public Response getItem(@PathParam("evento") int idEvento, @Context UriInfo uriInfo) throws SQLException {
        try ( PreparedStatement sEvento = con.prepareStatement("SELECT * FROM evento WHERE ID=?")) {
            sEvento.setInt(1, idEvento);
            try ( ResultSet rs = sEvento.executeQuery()) {
                if (rs.next()) {
                    try ( PreparedStatement sAula = con.prepareStatement("SELECT * FROM aula a WHERE a.ID=? ")) {
                        sAula.setInt(1, rs.getInt("ID_aula"));
                        try ( ResultSet rsAula = sAula.executeQuery()) {
                            if (rsAula.next()) {
                                try ( PreparedStatement sAttrezzatura = con.prepareStatement(
                                        "SELECT att.* FROM aula a JOIN attrezzatura att ON a.ID = att.ID_aula WHERE a.ID = ?")) {
                                    sAttrezzatura.setInt(1, rs.getInt("ID_aula"));
                                    Map<String, Object> evento = createEvento(rs);
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
                    throw new RESTWebApplicationException(404, "Evento non trovato");
                }
            } catch (Exception e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }

    @GET
    @Produces("application/json")
    public Response getCollection(@Context UriInfo uriinfo, @QueryParam("aula") int IDaula,
            @QueryParam("from") String from, @QueryParam("to") String to) throws SQLException {
        String query = "SELECT ID FROM evento WHERE ID_aula=? AND DATEDIFF(DATE(data_inizio),?) >= 0 AND DATEDIFF(DATE(data_inizio),?) <= 0";
        String queryRic = "SELECT er.ID FROM evento e JOIN evento_ricorrente er ON er.ID_master = e.ID WHERE "
                + "e.ID_aula=? AND DATEDIFF(DATE(er.data_inizio),?) >= 0 AND DATEDIFF(DATE(er.data_inizio),?) <= 0";

        List<String> eventi = new ArrayList();

        try ( PreparedStatement ps = con.prepareStatement(query);  PreparedStatement psRic = con.prepareStatement(queryRic)) {
            ps.setInt(1, IDaula);
            ps.setString(2, LocalDate.parse(from).format(DateTimeFormatter.ISO_DATE));
            ps.setString(3, LocalDate.parse(to).format(DateTimeFormatter.ISO_DATE));

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    URI uri = uriinfo.getBaseUriBuilder()
                            .path(EventiResource.class)
                            .path(EventiResource.class, "getItem")
                            .build(rs.getInt("ID"));
                    eventi.add(uri.toString());
                }
            }

            psRic.setInt(1, IDaula);
            psRic.setString(2, LocalDate.parse(from).format(DateTimeFormatter.ISO_DATE));
            psRic.setString(3, LocalDate.parse(to).format(DateTimeFormatter.ISO_DATE));

            try ( ResultSet rsRic = psRic.executeQuery()) {
                while (rsRic.next()) {
                    URI uri = uriinfo.getBaseUriBuilder()
                            .path(EventiRicorrentiResource.class)
                            .path(EventiRicorrentiResource.class, "getItem")
                            .build(rsRic.getInt("ID"));
                    eventi.add(uri.toString());
                }
            }

        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }
        return Response.ok(eventi).build();
    }

    @GET
    @Path("attuali")
    @Produces("application/json")
    public Response getCollectionAttuali(@Context UriInfo uriinfo) throws SQLException {
        String query = "SELECT ID "
                + "FROM evento "
                + "WHERE data_inizio <= DATE_ADD(NOW(), INTERVAL 3 HOUR) AND data_fine >= NOW()";
        String queryRic = "SELECT ID "
                + "FROM evento_ricorrente "
                + "WHERE data_inizio <= DATE_ADD(NOW(), INTERVAL 3 HOUR) AND data_fine >= NOW()";

        List<String> eventi = new ArrayList();

        try ( PreparedStatement ps = con.prepareStatement(query);  PreparedStatement psRic = con.prepareStatement(queryRic)) {

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    URI uri = uriinfo.getBaseUriBuilder()
                            .path(EventiResource.class)
                            .path(EventiResource.class, "getItem")
                            .build(rs.getInt("ID"));
                    eventi.add(uri.toString());
                }
            }

            try ( ResultSet rsRic = psRic.executeQuery()) {
                while (rsRic.next()) {
                    URI uri = uriinfo.getBaseUriBuilder()
                            .path(EventiRicorrentiResource.class)
                            .path(EventiRicorrentiResource.class, "getItem")
                            .build(rsRic.getInt("ID"));
                    eventi.add(uri.toString());
                }
            }

        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }
        return Response.ok(eventi).build();
    }

    @GET
    @Path("export")
    @Produces("text/calendar")
    public Response getCollectionTimeInterval(@Context UriInfo uriinfo, @QueryParam("from") String from,
            @QueryParam("to") String to) throws IOException {
        String query = "SELECT ID FROM evento WHERE DATEDIFF(DATE(data_inizio),?) >= 0 AND DATEDIFF(DATE(data_inizio),?) <= 0";
        String queryRic = "SELECT * FROM evento_ricorrente WHERE DATEDIFF(DATE(data_inizio),?) >= 0 AND DATEDIFF(DATE(data_inizio),?) <= 0";

        List<Evento> eventi = new ArrayList();

        try ( PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, LocalDate.parse(from).format(DateTimeFormatter.ISO_DATE));
            ps.setString(2, LocalDate.parse(to).format(DateTimeFormatter.ISO_DATE));
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> evento = (Map<String, Object>) getItem(rs.getInt("ID"), uriinfo).getEntity();
                    eventi.add(createEvento(evento));
                }
            }
        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }

        try ( PreparedStatement psRic = con.prepareStatement(queryRic)) {
            psRic.setString(1, LocalDate.parse(from).format(DateTimeFormatter.ISO_DATE));
            psRic.setString(2, LocalDate.parse(to).format(DateTimeFormatter.ISO_DATE));
            try ( ResultSet rsRic = psRic.executeQuery()) {
                while (rsRic.next()) {
                    // Evento master
                    Map<String, Object> evento = (Map<String, Object>) getItem(rsRic.getInt("ID_master"), uriinfo).getEntity();
                    // Cambio della data di inizio e della data di fine
                    evento.put("data_inizio", LocalDateTime.parse(rsRic.getDate("data_inizio").toLocalDate()
                            .atTime(rsRic.getTime("data_inizio").toLocalTime()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                    evento.put("data_fine", LocalDateTime.parse(rsRic.getDate("data_fine").toLocalDate()
                            .atTime(rsRic.getTime("data_fine").toLocalTime()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                    eventi.add(createEvento(evento));
                }
            }
        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }

        ICalendarWriter calWriter = new ICalendarWriter();

        String fileURL = servletContext.getRealPath("").concat("/ics/calendar.ics");

        File file = new File(fileURL);

        FileOutputStream fout = new FileOutputStream(file);

        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(calWriter.export(eventi), fout);

        return Response.ok(file, "text/calendar")
                .header("Content-Disposition", "attachment;filename=calendar.ics")
                .build();

    }

    @POST
    @Logged
    @Consumes("application/json")
    @Produces("application/json")
    public Response addItem(@Context UriInfo uriinfo, Map<String, Object> evento) {
        String addEvento = "INSERT "
                + "INTO evento (data_inizio,data_fine,nome,descrizione,email_responsabile,ID_aula,tipologia,nome_corso,tipo_ricorrenza,data_fine_ricorrenza) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";

        try ( PreparedStatement ps = con.prepareStatement(addEvento, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, LocalDateTime.parse((String) evento.get("data_inizio"))
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setString(2, LocalDateTime.parse((String) evento.get("data_fine"))
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setString(3, (String) evento.get("nome"));
            ps.setString(4, (String) evento.get("descrizione"));
            ps.setString(5, (String) evento.get("email_responsabile"));
            ps.setInt(6, (Integer) ((Map<String, Object>) evento.get("aula")).get("ID"));
            ps.setString(7, ((TipologiaEvento) evento.get("tipologia")).toString());
            ps.setString(8, (String) evento.get("nome_corso"));
            ps.setString(9, ((TipologiaRicorrenza) evento.get("tipo_ricorrenza")).toString());
            ps.setString(10,
                    LocalDate.parse((String) evento.get("data_fine_ricorrenza")).format(DateTimeFormatter.ISO_DATE));

            ps.executeUpdate();

            try ( ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int idEvento = keys.getInt(1);
                URI uri = uriinfo.getBaseUriBuilder()
                        .path(EventiResource.class)
                        .path(EventiResource.class, "getItem")
                        .build(idEvento);
                return Response.created(uri).build();
            }
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }

    }

    @PUT
    @Logged
    @Consumes("application/json")
    @Path("{evento}")
    public Response updateItem(@PathParam("evento") int idEvento, Map<String, Object> newEvento,
            @Context UriInfo uriInfo) throws SQLException {

        String uEvento = "UPDATE "
                + "evento SET data_inizio=?,data_fine=?,nome=?,descrizione=?,email_responsabile=?,ID_aula=?,tipologia=?,nome_corso=?,tipo_ricorrenza=?,data_fine_ricorrenza=? "
                + "WHERE ID=?";

        try ( PreparedStatement ps = con.prepareStatement(uEvento)) {
            ps.setString(1, LocalDateTime.parse((String) newEvento.get("data_inizio"))
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setString(2, LocalDateTime.parse((String) newEvento.get("data_fine"))
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setString(3, (String) newEvento.get("nome"));
            ps.setString(4, (String) newEvento.get("descrizione"));
            ps.setString(5, (String) newEvento.get("email_responsabile"));
            ps.setInt(6, (Integer) ((Map<String, Object>) newEvento.get("aula")).get("ID"));
            ps.setString(7, (String) newEvento.get("tipologia"));
            if (TipologiaEvento.valueOf((String) newEvento.get("tipologia")) != TipologiaEvento.ESAME
                    && TipologiaEvento.valueOf((String) newEvento.get("tipologia")) != TipologiaEvento.PARZIALE
                    && TipologiaEvento.valueOf((String) newEvento.get("tipologia")) != TipologiaEvento.LEZIONE) {
                ps.setNull(8, java.sql.Types.VARCHAR);
            } else {
                ps.setString(8, (String) newEvento.get("nome_corso"));
            }
            if (TipologiaRicorrenza
                    .valueOf((String) newEvento.get("tipo_ricorrenza")) != TipologiaRicorrenza.GIORNALIERA
                    && TipologiaRicorrenza
                            .valueOf((String) newEvento.get("tipo_ricorrenza")) != TipologiaRicorrenza.SETTIMANALE
                    && TipologiaRicorrenza
                            .valueOf((String) newEvento.get("tipo_ricorrenza")) != TipologiaRicorrenza.MENSILE) {
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setNull(10, java.sql.Types.DATE);
            } else {
                ps.setString(9, (String) newEvento.get("tipo_ricorrenza"));
                ps.setString(10, ((String) newEvento.get("data_fine_ricorrenza")));
            }

            ps.setInt(11, idEvento);

            ps.executeUpdate();
        }

        return Response.noContent().build();
    }

}
