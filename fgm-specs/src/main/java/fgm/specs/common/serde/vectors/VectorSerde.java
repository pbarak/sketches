package fgm.specs.common.serde.vectors;

import fgm.specs.common.messages.DriftVectorMessage;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class VectorSerde implements Serde<DriftVectorMessage> {
    private final DriftVectorMessageSerializer serializer;
    private final DriftVectorMessageDeserializer deserializer;

    public VectorSerde() {
        this.serializer = new DriftVectorMessageSerializer();
        this.deserializer = new DriftVectorMessageDeserializer();
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<DriftVectorMessage> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<DriftVectorMessage> deserializer() {
        return deserializer;
    }


}
