package org.univaq.swa.auleweb.aulewebrest;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.univaq.swa.auleweb.aulewebrest.jackson.ObjectMapperContextResolver;
import org.univaq.swa.auleweb.aulewebrest.security.CORSFilter;
import org.univaq.swa.auleweb.aulewebrest.security.AuthenticationRes;
import org.univaq.swa.auleweb.aulewebrest.security.AuthLoggedFilter;
import org.univaq.swa.auleweb.aulewebrest.resources.AuleResource;
import org.univaq.swa.auleweb.aulewebrest.resources.EventiResource;
import org.univaq.swa.auleweb.aulewebrest.resources.EventiRicorrentiResource;
import org.univaq.swa.auleweb.aulewebrest.resources.GruppiResource;

@ApplicationPath("rest")
public class RESTApp extends Application {

    private final Set<Class<?>> classes;

    public RESTApp() {
        HashSet<Class<?>> c = new HashSet<>();
        //root resources

        c.add(EventiResource.class);
        c.add(EventiRicorrentiResource.class);
        c.add(AuleResource.class);
        c.add(GruppiResource.class);
        c.add(AuthenticationRes.class);

        // provider jackson per servizio di (de)serializzazione
        c.add(JacksonJsonProvider.class);

        // (de)serializzazione custom
        c.add(ObjectMapperContextResolver.class);

        // autenticazione
        c.add(AuthLoggedFilter.class);

        // gestione degli header CORS
        c.add(CORSFilter.class);

        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

}
