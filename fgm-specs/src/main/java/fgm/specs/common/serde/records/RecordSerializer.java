package fgm.specs.common.serde.records;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import fgm.specs.common.serde.common.CamelCaseNamingStrategy;
import fgm.specs.data.StreamRecord;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class RecordSerializer implements Serializer<StreamRecord>{

    private final ObjectMapper objectMapper;

        public RecordSerializer() {
            this.objectMapper = new ObjectMapper();
            this.objectMapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());
            PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().build();
            this.objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
        }

        @Override
        public void configure(Map<String, ?> configure, boolean isKey){
            //No configuration needed
        }

        @Override
        public byte[] serialize(String topic, StreamRecord data) {
            try{
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize Vector", e);
            }
        }

        @Override
        public void close() {
            // No cleanup needed
        }
    }
