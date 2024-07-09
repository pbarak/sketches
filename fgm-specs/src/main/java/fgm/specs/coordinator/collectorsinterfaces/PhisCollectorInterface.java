package fgm.specs.coordinator.collectorsinterfaces;

import fgm.specs.common.messages.PhiMessage;

public interface PhisCollectorInterface {
     void collectPhis(PhiMessage phiMessage);
     void notifyNodesForNewSubround(double theta);
     void notifyNodesForRoundEnded();
}
