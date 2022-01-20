package org.chaoqi.herostory.mq;

/**
 * 战果消息
 */
public class VictorMsg {
    /**
     * 赢家 id
     */
    private int winnerId;
    /**
     * 输家 id
     */
    private int loserId;

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public int getLoserId() {
        return loserId;
    }

    public void setLoserId(int loserId) {
        this.loserId = loserId;
    }
}
