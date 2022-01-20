package org.chaoqi.herostory.model;

/**
 * 移动状态
 */
public class MoveState {
    /**
     * 起始位置 X
     */
    private float fromX;

    /**
     * 起始位置 Y
     */
    private float fromY;

    /**
     * 目标位置 X
     */
    private float toX;
    /**
     * 目标位置 Y
     */
    private float toY;

    /**
     * 移动开始时间
     */
    private long startTime;

    public MoveState() {
        fromX = 0;
        fromY = 0;
        toX = 0;
        toY = 0;
        startTime = System.currentTimeMillis();
    }

    public float getFromX() {
        return fromX;
    }

    public void setFromX(float fromX) {
        this.fromX = fromX;
    }

    public float getFromY() {
        return fromY;
    }

    public void setFromY(float fromY) {
        this.fromY = fromY;
    }

    public float getToX() {
        return toX;
    }

    public void setToX(float toX) {
        this.toX = toX;
    }

    public float getToY() {
        return toY;
    }

    public void setToY(float toY) {
        this.toY = toY;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
