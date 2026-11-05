package com.iemes.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/** Keeps JDBC DATETIME values in the millisecond format expected by the existing pages. */
public class IemesObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public IemesObjectMapper() {
        SimpleModule module = new SimpleModule("iemes-datetime");
        module.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider)
                    throws IOException {
                generator.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
        });
        registerModule(module);
    }
}
