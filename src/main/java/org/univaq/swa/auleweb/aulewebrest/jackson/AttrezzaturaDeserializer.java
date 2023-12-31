package org.univaq.swa.auleweb.aulewebrest.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.univaq.swa.auleweb.aulewebrest.model.Attrezzatura;

public class AttrezzaturaDeserializer extends JsonDeserializer<Attrezzatura> {

    @Override
    public Attrezzatura deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        Attrezzatura a = new Attrezzatura();

        JsonNode node = jp.getCodec().readTree(jp);

        if (node.has("numero_seriale")) {
            a.setNumeroSeriale(node.get("numero_seriale").toString());
        }

        if (node.has("descrizione")) {
            a.setDescrizione(node.get("descrizione").toString());
        }

        return a;

    }

}
