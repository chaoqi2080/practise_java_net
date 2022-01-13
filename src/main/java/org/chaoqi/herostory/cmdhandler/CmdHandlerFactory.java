package org.chaoqi.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.chaoqi.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 *命令处理器工厂类
 *
 */
public final class CmdHandlerFactory {
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
        _handlerMap.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
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
