package org.chaoqi.herostory.gameserver.login.db;

public interface IUserDao {
    /**
     * 根据用户名称获取实体
     * @param userName
     * @return
     */
    UserEntity getByUserName(String userName);

    /**
     * 添加用户实体
     * @param newEntity
     */
    void insertInto(UserEntity newEntity);
}
