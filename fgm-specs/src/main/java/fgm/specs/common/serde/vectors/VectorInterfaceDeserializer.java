package fgm.specs.common.serde.vectors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import fgm.specs.common.cfg.implementation.config.FastAGMSCfgImpl;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.common.vector.FastAGMS;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import fgm.specs.factory.IFgmFactory;
import fgm.specs.factory.implementation.DependencyInjection;

public class VectorInterfaceDeserializer extends JsonDeserializer<DriftVectorMessage> {

    @Override
    public DriftVectorMessage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().build();
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
        JsonNode node = mapper.readTree(jsonParser);
        if (node.get("state_vector").has("type")) {
            String type = node.get("state_vector").get("type").asText();
            if ("FastAGMS".equals(type)) {
                DriftVectorMessage driftVectorMessage = new DriftVectorMessage();
                driftVectorMessage.setCurrentRound(mapper.readValue(node.get("current_round").toString(), Integer.class));
                driftVectorMessage.setSiteId(mapper.readValue(node.get("site_id").toString(), String.class));
                driftVectorMessage.setCurrentSubround(mapper.readValue(node.get("current_subround").toString(), Integer.class));
                try {
                    IFgmFactory<?> factory = DependencyInjection.getFactory();
                    FastAGMS fastAGMS = new FastAGMS(((FastAGMSCfgImpl<?>)factory.getBaseCfg()).getSketchDepth(), ((FastAGMSCfgImpl<?>)factory.getBaseCfg()).getSketchBuckets(), DependencyInjection.getFactory().toStateVectorRecord());
                    fastAGMS.setCounts(mapper.readValue(node.get("state_vector").get("counts").toString(), Double[][].class));
                    driftVectorMessage.setStateVector(fastAGMS);
                    return driftVectorMessage;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
