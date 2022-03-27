package org.chaoqi.herostory.gameserver.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.chaoqi.herostory.gameserver.GameMsgHandler;
import org.chaoqi.herostory.gameserver.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *命令处理器工厂类
 *
 */
public final class CmdHandlerFactory {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);
    /**
     * 命令处理器字典
     */
    private static Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    /**
     * 私有化构造器
     */
    private CmdHandlerFactory() {
    }

    static public void init() {
        LOGGER.info("=== 完成命令与处理器的关联 ===");
        //获取包名称
        String packageName = CmdHandlerFactory.class.getPackageName();
        //列出 ICmdHandler 的所有子类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(packageName, true, ICmdHandler.class);

        for (Class<?> handlerClazz : clazzSet) {
            //跳过所有的抽象类
            if (null == handlerClazz ||
                    0 != (handlerClazz.getModifiers() & Modifier.ABSTRACT)) {
                continue;
            }

            //获取所有方法
            Method[] methodArray =  handlerClazz.getMethods();
            //消息类型
            Class<?> msgClazz = null;

            for (Method curMethod : methodArray) {
                //剔除不是 handle 的方法
                if (null == curMethod ||
                        !curMethod.getName().equals("handle")) {
                    continue;
                }

                //获取函数参数类型数组
                Class<?>[] paramTypeArray = curMethod.getParameterTypes();

                //如果参数数量不是2，则跳出
                if (paramTypeArray.length < 2 ||
                        paramTypeArray[1] == GeneratedMessageV3.class ||
                        !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[1])) {
                    continue;
                }

                //获取第二个参数的类型
                msgClazz = paramTypeArray[1];
                break;
            }

            if (null == msgClazz) {
                continue;
            }

            try {
                //创建命令处理器实例
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = (ICmdHandler<? extends GeneratedMessageV3>)handlerClazz.getDeclaredConstructor().newInstance();

                LOGGER.info(
                        "{} <==> {}",
                        msgClazz.getSimpleName(),
                        cmdHandler.getClass().getSimpleName()
                        );

                _handlerMap.put(msgClazz, cmdHandler);
            } catch (Exception ex) {
                //
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 创建命令处理器
     * @param msgClazz
     * @return
     */
    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (null == msgClazz) {
            return null;
        }

        return _handlerMap.get(msgClazz);
    }
}
