package fgm.specs.coordinator.collectorsinterfaces;

import fgm.specs.common.messages.IncreaseOfCMessage;

public interface IncreaseOfCsCollectorInterface {
     void notifyNodesForSubroundEnded();
     void collectIncreaseOfCs(IncreaseOfCMessage increaseOfCMessage);
    }
