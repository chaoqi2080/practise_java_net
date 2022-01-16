package org.chaoqi.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.chaoqi.herostory.login.LoginService;
import org.chaoqi.herostory.login.db.UserEntity;
import org.chaoqi.herostory.model.MoveState;
import org.chaoqi.herostory.model.User;
import org.chaoqi.herostory.model.UserManager;
import org.chaoqi.herostory.msg.GameMsgProtocol;

public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();
        if (null == userName || null == password) {
            return;
        }

        //从db 查找用户
        UserEntity userEntity = LoginService.getInstance().userLogin(userName, password);

        GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

        if (null == userEntity) {
            resultBuilder.setUserId(-1);
            resultBuilder.setUserName("");
            resultBuilder.setHeroAvatar("");
        } else {
            User newUser = new User();
            newUser.setUserId(userEntity.getUserId());
            newUser.setUserName(userEntity.getUserName());
            newUser.setUserAvatar(userEntity.getHeroAvatar());
            newUser.setCurHp(100);
            newUser.setMoveState(new MoveState());
            UserManager.addUser(newUser);

            //把当前用户id 绑定到 ctx
            ctx.attr (AttributeKey.valueOf("userId")).set(userEntity.getUserId());

            resultBuilder.setUserId(userEntity.getUserId());
            resultBuilder.setUserName(userEntity.getUserName());
            resultBuilder.setHeroAvatar(userEntity.getHeroAvatar());
        }

        GameMsgProtocol.UserLoginResult result = resultBuilder.build();
        ctx.writeAndFlush(result);
    }
}
