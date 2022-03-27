package org.chaoqi.herostory.gameserver.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.chaoqi.herostory.gameserver.InternalMessage;

public class MyCmdHandlerContext {
    private ChannelHandlerContext _realCtx;
    private int _userId;
    private int _remoteSessionId;

    public MyCmdHandlerContext(ChannelHandlerContext ctx) {
        _realCtx = ctx;
    }

    public int getUserId() {
        return _userId;
    }

    public void setUserId(int userId) {
        this._userId = userId;
    }

    public int getRemoteSessionId() {
        return _remoteSessionId;
    }

    public void setRemoteSessionId(int remoteSessionId) {
        this._remoteSessionId = remoteSessionId;
    }

    public void writeAndFlush(GeneratedMessageV3 msg) {
        InternalMessage innerMsg = new InternalMessage();
        innerMsg.setMsgObj(msg);
        innerMsg.setRemoteSessionId(_remoteSessionId);

        _realCtx.writeAndFlush(innerMsg);
    }
}
