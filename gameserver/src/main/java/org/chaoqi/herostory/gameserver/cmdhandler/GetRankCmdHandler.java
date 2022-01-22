package org.chaoqi.herostory.gameserver.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.chaoqi.herostory.gameserver.msg.GameMsgProtocol;
import org.chaoqi.herostory.gameserver.rank.RankItem;
import org.chaoqi.herostory.gameserver.rank.RankService;

import java.util.Collections;

/**
 * 获取排行榜指令处理器
 */
public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd msg) {
        if (null == ctx || null == msg) {
            return;
        }

        RankService.getInstance().getRank((rankItemList) -> {
            if (null == rankItemList) {
                rankItemList = Collections.emptyList();
            }

            GameMsgProtocol.GetRankResult.Builder resultBuilder = GameMsgProtocol.GetRankResult.newBuilder();

            for (RankItem currItem : rankItemList) {
                if (null == currItem) {
                    continue;
                }

                GameMsgProtocol.GetRankResult.RankItem.Builder
                        currItemBuilder = GameMsgProtocol.GetRankResult.RankItem.newBuilder();
                currItemBuilder.setRankId(currItem.getRankId());
                currItemBuilder.setUserId(currItem.getUserId());
                currItemBuilder.setUserName(currItem.getUserName());
                currItemBuilder.setHeroAvatar(currItem.getHeroAvatar());
                currItemBuilder.setWin(currItem.getWin());

                resultBuilder.addRankItem(currItemBuilder);
            }

            GameMsgProtocol.GetRankResult result = resultBuilder.build();
            ctx.writeAndFlush(result);

            return null;
        });
    }
}
