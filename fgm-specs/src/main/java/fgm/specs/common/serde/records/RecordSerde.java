package fgm.specs.common.serde.records;


import fgm.specs.data.StreamRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class RecordSerde implements Serde<StreamRecord> {
    private final RecordSerializer serializer;
    private final RecordDeserializer deserializer;

    public RecordSerde() {
        this.serializer = new RecordSerializer();
        this.deserializer = new RecordDeserializer();
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<StreamRecord> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<StreamRecord> deserializer() {
        return deserializer;
    }


}
