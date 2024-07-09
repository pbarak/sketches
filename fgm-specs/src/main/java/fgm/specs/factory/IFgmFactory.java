package fgm.specs.factory;

import fgm.specs.common.GlobalStatistic;
import fgm.specs.common.Vector;
import fgm.specs.common.cfg.BaseConfiguration;
import fgm.specs.common.messages.DriftVectorMessage;
import fgm.specs.common.messages.IncreaseOfCMessage;
import fgm.specs.common.messages.PhiMessage;
import fgm.specs.common.state.FgmNodeState;
import fgm.specs.data.StreamRecord;
import fgm.specs.data.WindowRecord;
import fgm.specs.function.QueryFunction;
import fgm.specs.function.SafeFunction;
import fgm.specs.site.convertor.IToStateVectorConvertor;;
import fgm.specs.site.state.LocalStatistic;
import fgm.specs.site.convertor.IToWindowRecordConvertor;
import org.apache.kafka.clients.producer.Producer;


public interface IFgmFactory<IMPL extends Vector> {
    LocalStatistic<IMPL> getLocalStatistic();
    GlobalStatistic<IMPL> getGlobalStatistic();
    SafeFunction<IMPL> getSafeFunction();
    QueryFunction<IMPL> getQueryFunction();
    FgmNodeState<IMPL> getFgmNodeState();
    Producer<String, DriftVectorMessage> getXiMessageProducer();
    Producer<String, PhiMessage> getPhiMessageProducer();
    Producer<String, IncreaseOfCMessage> getIncreaseOfCMessageProducer();
    BaseConfiguration<IMPL> getBaseCfg();
    <IMPL0 extends WindowRecord, IMPL2 extends StreamRecord> IToWindowRecordConvertor<IMPL0, IMPL2> getToWindowRecordConvertor();
    <K extends Long> IToStateVectorConvertor<K> toStateVectorRecord();

}
