package org.univaq.swa.auleweb.aulewebrest.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JavaLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser,
            DeserializationContext deserializationContext)
            throws IOException {

        String dateAsString = jsonParser.getText();

        LocalDateTime dateTime = LocalDateTime.parse(dateAsString, formatter);

        return dateTime;
    }

}
