package org.chaoqi.herostory.model;

public class User {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 用户形象
     */
    private String userAvatar;

    /**
     * 用户当前血量
     */
    private int curHp;

    /**
     * 移动状态
     */
    private MoveState moveState;

    public MoveState getMoveState() {
        return moveState;
    }

    public void setMoveState(MoveState moveState) {
        this.moveState = moveState;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getCurHp() {
        return curHp;
    }

    public void setCurHp(int curHp) {
        this.curHp = curHp;
    }
}
