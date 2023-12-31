package org.univaq.swa.auleweb.aulewebrest.security;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import jakarta.ws.rs.core.UriInfo;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("auth")
public class AuthenticationRes {

    public AuthenticationRes() {

    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@Context UriInfo uriinfo,
            @FormParam("email") String email,
            @FormParam("password") String password) throws NoSuchAlgorithmException {
        try {
            if (AuthHelpers.getInstance().authenticateUser(email, password)) {

                String authToken = AuthHelpers.getInstance().issueToken(uriinfo, email);

                AuthHelpers.getInstance().updateToken(email, authToken);

                return Response.ok(authToken)
                        .cookie(new NewCookie.Builder("token").value(authToken).build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).build();
            } else {
                return Response.status(UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            Logger.getLogger(AuthenticationRes.class.getName()).log(Level.SEVERE, null, e);
        }
        return Response.status(UNAUTHORIZED).build();

    }

    @DELETE
    @Path("logout")
    @Logged
    public Response logout(@Context ContainerRequestContext req) {
        try {
            String token = (String) req.getProperty("token");
            AuthHelpers.getInstance().revokeToken(token);
        } catch (Exception e) {
            Logger.getLogger(AuthenticationRes.class.getName()).log(Level.SEVERE, null, e);
        }
        return Response.noContent()
                //eliminaimo anche il cookie con il token
                .cookie(new NewCookie.Builder("token").value("").maxAge(0).build())
                .build();

    }

    //Metodo per fare "refresh" del token senza ritrasmettere le credenziali
    @GET
    @Path("refresh")
    @Logged
    public Response refresh(@Context ContainerRequestContext req, @Context UriInfo uriinfo) {
        try {
            //proprietà iniettata nella request dal filtro di autenticazione
            String email = (String) req.getProperty("email");
            String newToken = AuthHelpers.getInstance().issueToken(uriinfo, email);
            AuthHelpers.getInstance().updateToken(email, newToken);
            return Response.ok(newToken)
                    .cookie(new NewCookie.Builder("token").value(newToken).build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + newToken).build();
        } catch (Exception e) {
            Logger.getLogger(AuthenticationRes.class.getName()).log(Level.SEVERE, null, e);
        }
        return Response.status(UNAUTHORIZED).build();
    }

}
