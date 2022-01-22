package org.chaoqi.herostory.gameserver.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.chaoqi.herostory.gameserver.async.AsyncOperationProcessor;
import org.chaoqi.herostory.gameserver.login.LoginService;
import org.chaoqi.herostory.gameserver.model.MoveState;
import org.chaoqi.herostory.gameserver.model.User;
import org.chaoqi.herostory.gameserver.model.UserManager;
import org.chaoqi.herostory.gameserver.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

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

        LOGGER.info(
                "当前线程 = {}",
                Thread.currentThread().getName()
        );

        //从db 查找用户
        LoginService.getInstance().userLogin(userName, password, (userEntity) -> {
            GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

            LOGGER.info(
                    "当前线程 = {}",
                    Thread.currentThread().getName()
            );

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
            return null;
        });
    }
}
