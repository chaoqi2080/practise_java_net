package org.chaoqi.herostory.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *用户管理
 */
public final class UserManager {
    /**
     * 用户字典
     */
    static private final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    /**
     * 私有化构造器
     */
    private UserManager() {}

    /**
     * 增加用户
     * @param u
     */
    static public void addUser(User u) {
        if (null == u) {
            return;
        }

        _userMap.putIfAbsent(u.getUserId(), u);
    }

    /**
     * 根据用户id 获取用户
     * @param userId
     * @return
     */
    static public User getByUserId(int userId) {
        return _userMap.get(userId);
    }

    /**
     *移除用户
     * @param userId
     */
    static public void removeByUserId(int userId) {
        _userMap.remove(userId);
    }

    /**
     * 获取用户列表
     * @return
     */
    static public Collection<User> listUser() {
        return _userMap.values();
    }
}
