package org.chaoqi.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *MySql 会话工厂
 */
public final class MySqlSessionFactory {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MySqlSessionFactory.class);

    /**
     *
     */
    private MySqlSessionFactory(){
    }

    /**
     * MyBatis Sql 会话工厂类
     */
    static private SqlSessionFactory _sqlSessionFactory;

    static public void init() {
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(
                    Resources.getResourceAsStream("MyBatisConfig.xml")
            );

            //测试数据库连接
            SqlSession tempSession = _sqlSessionFactory.openSession();

            tempSession.getConnection().createStatement().execute("SELECT -1");

            tempSession.close();

            LOGGER.error("MySql 数据库连接测试成功");
        } catch (Exception ex) {
            //
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 创建 MySql 会话
     * @return
     */
    static public SqlSession openSession() {
        if (null == _sqlSessionFactory) {
            throw new RuntimeException("初始化 sqlSessionFactory 失败");
        }

        return _sqlSessionFactory.openSession(true);
    }
}
