package org.univaq.swa.auleweb.aulewebrest.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import org.univaq.swa.auleweb.aulewebrest.model.Attrezzatura;

@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public ObjectMapperContextResolver() {
        this.mapper = createObjectMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper localMapper = new ObjectMapper();

        localMapper.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule customSerializer = new SimpleModule("CustomSerializersModule");

        // configurazione serializzatori custom
        
        // LocalDateTime
        customSerializer.addSerializer(LocalDateTime.class, new JavaLocalDateTimeSerializer());
        customSerializer.addDeserializer(LocalDateTime.class, new JavaLocalDateTimeDeserializer());

        // attrezzatura
        customSerializer.addSerializer(Attrezzatura.class, new AttrezzaturaSerializer());
        customSerializer.addDeserializer(Attrezzatura.class, new AttrezzaturaDeserializer());

        localMapper.registerModule(customSerializer);

        return localMapper;
    }

}
