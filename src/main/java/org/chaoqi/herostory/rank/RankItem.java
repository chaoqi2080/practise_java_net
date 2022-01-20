package org.chaoqi.herostory.rank;

/**
 * 排行榜条目
 */
public class RankItem {
    /**
     * 排名 id
     */
    private int rankId;
    /**
     * 用户 id
     */
    private int userId;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 英雄形象
     */
    private String heroAvatar;
    /**
     * 胜利次数
     */
    private int win;

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }
}
