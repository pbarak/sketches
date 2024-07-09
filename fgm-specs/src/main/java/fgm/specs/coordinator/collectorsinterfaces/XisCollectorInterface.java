package fgm.specs.coordinator.collectorsinterfaces;

import fgm.specs.common.messages.DriftVectorMessage;

public interface XisCollectorInterface {
    void updateGlobalEstimate(DriftVectorMessage stateMessage);
    void notifyNodesForNewRound();
}
