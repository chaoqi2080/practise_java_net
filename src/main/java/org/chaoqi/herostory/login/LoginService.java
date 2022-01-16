package org.chaoqi.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.chaoqi.herostory.MySqlSessionFactory;
import org.chaoqi.herostory.async.AsyncOperationProcessor;
import org.chaoqi.herostory.async.IAsyncOperation;
import org.chaoqi.herostory.login.db.IUserDao;
import org.chaoqi.herostory.login.db.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public final class LoginService {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    /**
     * 私有化类构造器
     */
    private LoginService(){

    }

    /**
     * 单例对象
     */
    static private final LoginService _instance = new LoginService();

    /**
     * 获取单例对象
     * @return
     */
    static public LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登陆
     * @param userName
     * @param password
     * @return
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName || null == password) {
            return;
        }

        IAsyncOperation asyncOp = new AsyncGetUserEntity(userName, password){
            @Override
            public void doFinish() {
                if (null != callback) {
                    //查询到的用户实体同步使用消息处理器处理
                    callback.apply(this.getUserEntity());
                }
            }

            @Override
            public int getBindId() {
                return userName.hashCode();
            }
        };

        //异步获取用户实体
        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    /**
     * 异步获取 UserEntity 的内部类
     */
    private class AsyncGetUserEntity implements IAsyncOperation {
        private String _userName;
        private String _password;
        private UserEntity _userEntity;

        public AsyncGetUserEntity(String _userName, String _password) {
            this._userName = _userName;
            this._password = _password;
        }

        public UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {

                LOGGER.info(
                        "当前线程 = {}",
                        Thread.currentThread().getName()
                );

                // 获取 DAO
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);
                // 获取用户实体
                UserEntity userEntity = dao.getByUserName(_userName);

                if (null != userEntity) {
                    if (!_password.equals(userEntity.getPassword())) {
                        throw new RuntimeException("密码错误");
                    }
                } else {
                    userEntity = new UserEntity();
                    userEntity.setUserName(_userName);
                    userEntity.setPassword(_password);
                    userEntity.setHeroAvatar("Hero_Shaman");

                    dao.insertInto(userEntity);
                }

                _userEntity = userEntity;
            } catch (Exception ex) {
                //
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
