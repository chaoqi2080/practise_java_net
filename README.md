# practise_java_net

GateServer -> GameServer 只有一个 socket 连接
1. WebBinaryMsg -> client message + sessionId 方便知道回包需要传给具体的哪一个真正的客户端
2. GameServer 内部消息处理和传输
   1. InterMessage (remoteSessionId + GenerateMessageV3) 网络层到业务层的消息转换。包装了自定义 ctx 和解析出业务直接处理的消息。
   2. MyCmdContext (remoteSessionId + nettyCtx + userId), 只提供需要的接口，对业务层屏蔽网络传输细节。
   

这是用 java 写游戏服务器的一个实践，请不要用于商业用途。
