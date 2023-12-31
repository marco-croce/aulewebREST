package org.univaq.swa.auleweb.aulewebrest.resources;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.univaq.swa.auleweb.aulewebrest.exceptions.RESTWebApplicationException;
import org.univaq.swa.auleweb.aulewebrest.result.CSVReader;
import org.univaq.swa.auleweb.aulewebrest.result.CSVWriter;
import org.univaq.swa.auleweb.aulewebrest.security.Logged;

// PATH -> /rest/gruppi
@Path("gruppi")
public class GruppiResource {

    Class c = Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/auleweb?serverTimezone=Europe/Rome", "aulewebsite", "aulewebpass");

    // Iniettiamo la ServletContext
    @Context
    private ServletContext servletContext;
    // Iniettiamo la HttpServletRequest
    @Context
    private HttpServletRequest servletRequest;

    public GruppiResource() throws SQLException, ClassNotFoundException {

    }

    private Map<String, Object> createGruppo(ResultSet rs) {
        try {
            Map<String, Object> gruppo = new HashMap<>();
            gruppo.put("ID", rs.getString("ID"));
            gruppo.put("nome", rs.getString("nome"));
            gruppo.put("descrizione", rs.getString("descrizione"));
            return gruppo;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Produces("application/json")
    public Response getCollection(@Context UriInfo uriinfo) throws SQLException {

        PreparedStatement sGruppi = con.prepareStatement("SELECT ID FROM gruppo");
        List<String> gruppi = new ArrayList();

        try ( ResultSet rs = sGruppi.executeQuery()) {
            while (rs.next()) {
                URI uri = uriinfo.getBaseUriBuilder()
                        .path(GruppiResource.class)
                        .path(GruppiResource.class, "getItem")
                        .build(rs.getInt("ID"));
                gruppi.add(uri.toString());
            }
        } catch (SQLException e) {
            throw new RESTWebApplicationException(e);
        }
        return Response.ok(gruppi).build();
    }

    @GET
    @Path("{gruppo: [1-9][0-9]*}")
    @Produces("application/json")
    public Response getItem(
            @PathParam("gruppo") int idGruppo,
            @Context UriInfo uriInfo
    ) throws SQLException {

        try ( PreparedStatement sGruppo = con.prepareStatement("SELECT * FROM gruppo WHERE ID = ?")) {
            sGruppo.setInt(1, idGruppo);
            try ( ResultSet rs = sGruppo.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> gruppo = createGruppo(rs);
                    List<String> aule = new ArrayList();
                    try ( PreparedStatement sAule = con.prepareStatement("SELECT * FROM gruppo_aula WHERE ID_gruppo=?")) {
                        sAule.setInt(1, idGruppo);
                        try ( ResultSet rsAule = sAule.executeQuery()) {
                            while (rsAule.next()) {
                                aule.add(uriInfo.getBaseUriBuilder()
                                        .path(AuleResource.class)
                                        .path(AuleResource.class, "getItem")
                                        .build(rsAule.getInt("ID_aula")).toString());
                            }
                        }
                    }
                    gruppo.put("aule", aule);
                    return Response.ok(gruppo).build();
                } else {
                    throw new RESTWebApplicationException(404, "Gruppo non trovato");
                }
            } catch (SQLException e) {
                throw new RESTWebApplicationException(e);
            }
        }
    }

    @POST
    @Logged
    @Consumes("application/json")
    @Path("{gruppo}/{aula}")
    public Response aggiungiAula(@Context UriInfo uriinfo,
            @PathParam("gruppo") int idGruppo,
            @PathParam("aula") int idAula) {
        String addAula = "INSERT INTO gruppo_aula (ID_aula, ID_gruppo) VALUES (?, ?)";
        try ( PreparedStatement ps = con.prepareStatement(addAula)) {
            ps.setInt(1, idGruppo);
            ps.setInt(2, idAula);
            ps.executeUpdate();

            return Response.noContent().build();

        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Logged
    @Produces("text/csv")
    @Path("configuration")
    public Response getConfiguration() throws IOException {
        String query = "SELECT * FROM gruppo_aula";
        List<List<String>> confs = new ArrayList();
        try ( PreparedStatement ps = con.prepareStatement(query)) {
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    List<String> conf = new ArrayList();
                    String query2 = "SELECT * FROM aula WHERE ID=?";
                    String query3 = "SELECT * FROM gruppo WHERE ID=?";
                    try ( PreparedStatement ps2 = con.prepareStatement(query2)) {
                        ps2.setInt(1, rs.getInt("ID_aula"));
                        try ( ResultSet rs2 = ps2.executeQuery()) {
                            if (rs2.next()) {
                                conf.add(rs2.getString("edificio"));
                                conf.add(rs2.getString("luogo"));
                                conf.add(rs2.getString("piano"));
                                conf.add(rs2.getString("nome"));
                            } else {
                                throw new RESTWebApplicationException(404, "Aula non trovata");
                            }
                        }
                    }
                    try ( PreparedStatement ps3 = con.prepareStatement(query3)) {
                        ps3.setInt(1, rs.getInt("ID_gruppo"));
                        try ( ResultSet rs3 = ps3.executeQuery()) {
                            if (rs3.next()) {
                                conf.add(rs3.getString("nome"));
                            } else {
                                throw new RESTWebApplicationException(404, "Gruppo non trovato");
                            }
                        }
                    }
                    confs.add(conf);
                }
            }

            CSVWriter w = new CSVWriter();
            w.csv_gruppi(confs, servletContext.getRealPath(""));

            String fileURL = servletContext.getRealPath("").concat("/csv/gruppi.csv");

            File file = new File(fileURL);

            return Response.ok(file, "text/csv")
                    .header("Content-Disposition", "attachment;filename=gruppi.csv")
                    .build();

        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @POST
    @Logged
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("configuration")
    public Response loadConfiguration(@Context UriInfo uriinfo) throws IOException, ServletException {
        // Otteniamo la "part" di un ipotetico input type="file" con name="fileconf" presente nell'applicazione web
        Part fileConf = servletRequest.getPart("fileconf");
        CSVReader csvReader = new CSVReader();

        // Contenuto del file .csv
        List<List<String>> confs = csvReader.csv_gruppi(fileConf, servletContext.getRealPath(""));
        // Rimozione riga di intestazione
        confs.remove(0);
        String edificio, luogo, piano, nomeAula, nomeGruppo;

        // Iterazione sulle "righe" del file .csv --> coppie GRUPPO-AULA
        for (List<String> conf : confs) {
            edificio = conf.get(0);
            luogo = conf.get(1);
            piano = conf.get(2);
            nomeAula = conf.get(3);
            nomeGruppo = conf.get(4);

            // Query per ottenere gli ID
            String aula = "SELECT ID FROM aula WHERE nome=? AND edificio=? AND luogo=? AND piano=?";
            String gruppo = "SELECT ID FROM gruppo WHERE nome=?";
            // Query per aggiungere un'aula al gruppo
            String query = "INSERT INTO gruppo_aula (ID_aula, ID_gruppo) VALUES (?, ?)";

            int aulaID, gruppoID;

            try ( PreparedStatement psA = con.prepareStatement(aula)) {
                psA.setString(1, nomeAula);
                psA.setString(2, edificio);
                psA.setString(3, luogo);
                psA.setInt(4, Integer.parseInt(piano));
                try ( ResultSet rsA = psA.executeQuery()) {
                    if (rsA.next()) {
                        aulaID = rsA.getInt("ID");
                    } else {
                        throw new RESTWebApplicationException(404, "Aula non trovata");
                    }
                }
                try ( PreparedStatement psG = con.prepareStatement(gruppo)) {
                    psG.setString(1, nomeGruppo);
                    try ( ResultSet rsG = psG.executeQuery()) {
                        if (rsG.next()) {
                            gruppoID = rsG.getInt("ID");
                        } else {
                            throw new RESTWebApplicationException(404, "Gruppo non trovato");
                        }
                    }
                    try ( PreparedStatement ps = con.prepareStatement(query)) {
                        ps.setInt(1, aulaID);
                        ps.setInt(2, gruppoID);
                        ps.executeUpdate();
                    }
                }

            } catch (SQLException ex) {
                throw new RESTWebApplicationException(ex);
            }
        }

        return Response.noContent().build();
    }

}
