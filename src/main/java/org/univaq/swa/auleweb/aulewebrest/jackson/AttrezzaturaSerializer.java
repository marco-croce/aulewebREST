package org.univaq.swa.auleweb.aulewebrest.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.univaq.swa.auleweb.aulewebrest.model.Attrezzatura;

public class AttrezzaturaSerializer extends JsonSerializer<Attrezzatura> {

    @Override
    public void serialize(Attrezzatura a, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("numero_seriale", a.getNumeroSeriale());
        jgen.writeStringField("descrizione", a.getDescrizione());
        jgen.writeEndObject();

    }

}
