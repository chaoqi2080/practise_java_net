package org.chaoqi.herostory.gameserver;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.chaoqi.herostory.gameserver.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别处理器
 */
public class GameMsgRecognizer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);
    /**
     * 消息编号 -> 消息对象字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgObjMap = new HashMap<>();

    /**
     * 消息类型 -> 消息编号
     */
    static private final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    static public void init() {
        LOGGER.info("=== 初始化消息id 和消息类型 ===");
        //获取所有消息类
        Class<?>[] clazzArray = GameMsgProtocol.class.getDeclaredClasses();

        for (Class<?> innerClazz : clazzArray) {
            //不是 GeneratedMessageV3 子类
            if (null == innerClazz || !(GeneratedMessageV3.class.isAssignableFrom(innerClazz))) {
                continue;
            }

            //
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                if (null == msgCode) {
                    continue;
                }

                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                //msgCode 和 innerClazz 不匹配
                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }

                try {
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    if (null == returnObj) {
                        continue;
                    }

                    LOGGER.info(
                            "{} => {}",
                            innerClazz.getName(),
                            msgCode.getNumber()
                    );

                    _msgCodeAndMsgObjMap.put(msgCode.getNumber(), (GeneratedMessageV3)returnObj);
                    _msgClazzAndMsgCodeMap.put(innerClazz, msgCode.getNumber());

                    //找到匹配了，跳出循环
                    break;
                } catch (Exception ex) {
                    //
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * 根据消息编号获取消息构建器
     * @param msgCode
     * @return
     */
    static public Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 defaultInstance = _msgCodeAndMsgObjMap.get(msgCode);
        if (null == defaultInstance) {
            return null;
        } else {
            return defaultInstance.newBuilderForType();
        }
    }

    /**
     * 根据消息类型获取消息编码
     * @param msgClazz
     * @return
     */
    static public int getMsgCodeByClazz(Class<?> msgClazz) {
        if (null == msgClazz) {
            return -1;
        }

        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if (null == msgCode) {
            return -1;
        } else {
            return msgCode;
        }
    }
}
