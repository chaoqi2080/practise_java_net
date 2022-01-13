# practise_java_net

## 2022-01-13 初级水平的能力要求
1. 收到消息处理流程 BinaryFrame -> ByteBuf -> byte[] -> msg
2. 回消息处理流程 msg -> byte[] -> ByteBuf -> BinaryFrame
3. Broadcaster 把 channel 管理起来，作为消息广播的介质
4. UserManager 管理所有的用户信息
5. ICmdHandler 抽象出 handle 处理接口，CmdHandlerFactory 工厂根据消息类型生成对应的处理器
```java
static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
    if (null == msgClazz) {
        return null;
    }

    return _handlerMap.get(msgClazz);
}
```
6. 入口根据 msgCode 解析出对应的消息内容
```java
static public Message.Builder getBuilderByMsgCode(int msgCode) {
    if (msgCode < 1) {
        return null;
    }

    GeneratedMessageV3 defaultInstance = _msgCodeAndDefaultBuilderMap.get(msgCode);
    if (null == defaultInstance) {
        return null;
    } else {
        return defaultInstance.newBuilderForType();
    }
}

//入口使用
//获取消息处理器
Message.Builder defaultBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
if (null == defaultBuilder) {
    LOGGER.error("遗漏了未处理的消息 = {}", msgCode);
    return;
}
defaultBuilder.clear();
defaultBuilder.mergeFrom(msgBody);

Message cmd = defaultBuilder.build();

if (null != cmd) {
    ctx.fireChannelRead(cmd);
}
```
7. 出口根据消息类写入对应的 msgCode
 ```java
static public int getMsgCodeByClazz(Class<?> msgClazz) {
    if (null == msgClazz) {
        return -1;
    }

    Integer msgCode = _clazzAndMsgCodeMap.get(msgClazz);
    if (null == msgCode) {
        return -1;
    } else {
        return msgCode;
    }
}

//
int msgCode = GameMsgRecognizer.getMsgCodeByClazz(msg.getClass());
if (-1 == msgCode) {
    LOGGER.error(
    "无法识别的消息类型， msgClazz = {}",
    msg.getClass().getSimpleName()
);

super.write(ctx, msg, promise);
    return;
}
//...
//消息编码
byteBuffer.writeShort((short)msgCode);

```