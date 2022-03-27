package org.chaoqi.herostory.gameserver;

import com.google.protobuf.GeneratedMessageV3;

public class InternalMessage {
    private GeneratedMessageV3 _msg;
    private int _remoteSessionId;

    public GeneratedMessageV3 getMsg() {
        return _msg;
    }

    public void setMsgObj(GeneratedMessageV3 _msgObj) {
        this._msg = _msgObj;
    }

    public int getRemoteSessionId() {
        return _remoteSessionId;
    }

    public void setRemoteSessionId(int _remoteSessionId) {
        this._remoteSessionId = _remoteSessionId;
    }
}
