package com.finplant.mt_remote.dto.jackson;

import java.io.IOException;
import java.time.Month;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MonthMapping {

    public static class Deserializer extends StdDeserializer<Month> {

        public Deserializer() {
            super(Month.class);
        }

        @Override
        public Month deserialize(JsonParser parser, DeserializationContext ctx)
              throws IOException {

            return Month.of(parser.getIntValue());
        }
    }

    public static class Serialize extends StdSerializer<Month> {

        public Serialize() {
            super(Month.class);
        }

        @Override
        public void serialize(Month value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(value.getValue());
        }
    }
}
