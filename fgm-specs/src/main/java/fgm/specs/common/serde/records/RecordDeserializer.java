package fgm.specs.common.serde.records;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fgm.specs.common.serde.common.CamelCaseNamingStrategy;
import fgm.specs.data.StreamRecord;
import org.apache.kafka.common.serialization.Deserializer;
import java.util.Map;

public class RecordDeserializer implements Deserializer<StreamRecord> {
    private final ObjectMapper objectMapper;

    public RecordDeserializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());

        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().build();
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(StreamRecord.class, new StreamRecordInterfaceDeserializer());
        objectMapper.registerModule(module);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        //No configuration needed
    }

    @Override
    public StreamRecord deserialize(String topic, byte[] data){
        try {
            JsonDeserializer<StreamRecord> deserializer = new StreamRecordInterfaceDeserializer();
            DeserializationContext ctxt = objectMapper.getDeserializationContext();
            JsonParser jsonParser = objectMapper.getFactory().createParser(data);
            return deserializer.deserialize(jsonParser, ctxt);
        } catch (Exception e){
            throw new RuntimeException("Failed to deserialize Vector", e);
        }
    }

    @Override
    public void close(){
        //No configuration needed
    }


}
