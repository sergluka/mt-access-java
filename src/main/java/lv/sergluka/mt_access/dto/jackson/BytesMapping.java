package lv.sergluka.mt_access.dto.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Base64;

public class BytesMapping {

    public static class Deserializer extends StdDeserializer<byte[]> {

        public Deserializer() {
            super(byte[].class);
        }

        @Override
        public byte[] deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {

            return Base64.getDecoder().decode(parser.getValueAsString());
        }
    }

    public static class Serialize extends StdSerializer<byte[]> {

        public Serialize() {
            super(byte[].class);
        }

        @Override
        public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {

            gen.writeString(Base64.getEncoder().encodeToString(value));
        }
    }
}
