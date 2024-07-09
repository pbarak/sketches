package fgm.specs.common;

public enum MessageType {
    increaseOfCMessage("INCREASE_OF_C_MESSAGE"),
    phiMessage("PHI_MESSAGE"),
    xiMessage("XI_MESSAGE"),
    startNewRoundMessage("START_NEW_ROUND_MESSAGE"),
    statNewSubroundMessage("START_NEW_SUBROUND_MESSAGE"),
    worldCupRecordForStatistics("WORLD_CUP_RECORD");

    private String messageType;
    MessageType(String message) {
        messageType = message;
    }

    public String getMessageType() {
        return messageType;
    }
}
