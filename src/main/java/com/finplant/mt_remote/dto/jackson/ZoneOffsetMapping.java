package com.finplant.mt_remote.dto.jackson;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneOffset;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ZoneOffsetMapping {

    public static class Deserializer extends StdDeserializer<ZoneOffset> {

        public Deserializer() {
            super(ZoneOffset.class);
        }

        @Override
        public ZoneOffset deserialize(JsonParser parser, DeserializationContext ctx)
              throws IOException {

            return ZoneOffset.ofHours(parser.getIntValue());
        }
    }

    public static class Serialize extends StdSerializer<ZoneOffset> {

        public Serialize() {
            super(ZoneOffset.class);
        }

        @Override
        public void serialize(ZoneOffset value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(Duration.ofSeconds(value.getTotalSeconds()).toHours());
        }
    }
}
