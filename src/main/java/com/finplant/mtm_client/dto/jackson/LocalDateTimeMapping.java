package com.finplant.mtm_client.dto.jackson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LocalDateTimeMapping {

    public static class Deserializer extends StdDeserializer<LocalDateTime> {

        public Deserializer() {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx)
              throws IOException {

            long second = parser.getLongValue();
            if (second == 0) {
                return null;
            }

            return LocalDateTime.ofEpochSecond(second, 0, ZoneOffset.UTC);
        }
    }

    public static class Serialize extends StdSerializer<LocalDateTime> {

        public Serialize() {
            super(LocalDateTime.class);
        }

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(value.toEpochSecond(ZoneOffset.UTC));
        }
    }
}
