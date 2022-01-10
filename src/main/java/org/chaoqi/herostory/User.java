package org.chaoqi.herostory;

public class User {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 用户形象
     */
    private String userAvatar;

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
}
