package fgm.specs.common.serde.records;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import fgm.specs.data.StreamRecord;
import fgm.specs.data.implementation.stream.WorldCupRecord;

import java.io.IOException;

public class StreamRecordInterfaceDeserializer extends JsonDeserializer<StreamRecord> {

    private static final String WORLD_CUP_RECORD = "WorldCupRecord";

    @Override
    public StreamRecord deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().build();
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
        JsonNode node = mapper.readTree(jsonParser);
        if (node.get("record_type").asText().equals(WORLD_CUP_RECORD)) {
            WorldCupRecord wc = new WorldCupRecord();
            wc.setClientID(Long.parseLong(node.get("client_i_d").asText()));
            wc.setServer(Long.parseLong(node.get("time_stamp").asText()));
            wc.setServer(Long.parseLong(node.get("object_i_d").asText()));
            wc.setServer(Long.parseLong(node.get("size").asText()));
            wc.setServer(Long.parseLong(node.get("type").asText()));
            wc.setServer(Long.parseLong(node.get("server").asText()));
            wc.setServer(Long.parseLong(node.get("status").asText()));
            wc.setServer(Long.parseLong(node.get("method").asText()));
            return wc;
        }
        return null;
    }
}
