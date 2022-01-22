package org.chaoqi.herostory.gameserver.rank;

import com.alibaba.fastjson.JSONObject;
import org.chaoqi.herostory.gameserver.async.AsyncOperationProcessor;
import org.chaoqi.herostory.gameserver.async.IAsyncOperation;
import org.chaoqi.herostory.gameserver.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 */
public final class RankService {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    /**
     * 单例对象
     */
    static private final RankService _instance = new RankService();

    /**
     * 私有化类构造器
     */
    private RankService() {}

    /**
     * 获取单例对象
     * @return
     */
    static public RankService getInstance() {
        return _instance;
    }

    /**
     * 刷新排名
     * @param winnerId 赢家 id
     * @param loserId  输家 id
     */
    public void refreshRank(int winnerId, int loserId) {
        if (winnerId <= 0 || loserId <= 0) {
            return;
        }

        try(Jedis redis = RedisUtil.getRedis()) {
            //执行 hincrby "User_Id" "Win" 1
            //执行 hincrby "User_Id" "Lose" 1
            //增加用户的胜利和失败次数
            redis.hincrBy("User_" + winnerId, "Win", 1);
            redis.hincrBy("User_" + loserId, "Lose", 1);

            //执行 hget "User_Id" "Win"
            //看看玩家赢了多少次
            String winStr = redis.hget("User_" + winnerId, "Win");
            int winInt = Integer.parseInt(winStr);

            //执行 zadd 修改排名
            redis.zadd("Rank", winInt, String.valueOf(winnerId));
        } catch (Exception ex) {
            //记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取排行榜
     * @param callback
     */
    public void getRank(final Function<List<RankItem>, Void> callback) {
        if (null == callback) {
            return;
        }

        AsyncGetRank asyncOp = new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        };

        //执行异步操作
        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    /**
     * 异步方式获取排行榜
     */
    static private class AsyncGetRank implements IAsyncOperation {
        /**
         * 排名条目列表
         */
        private List<RankItem> _rankItemList = null;

        /**
         * 获取排名条目列表
         * @return 排名条目列表
         */
        List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try(Jedis redis = RedisUtil.getRedis()) {
                //获取字符串集合
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);

                List<RankItem> rankItemList = new ArrayList<>();
                int i = 0;

                for (Tuple t : valSet) {
                    //获取用户 id
                    int userId = Integer.parseInt(t.getElement());

                    //获取用户信息
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if (null == jsonStr) {
                        //跳过用户信息为空的
                        continue;
                    }

                    RankItem newItem = new RankItem();
                    newItem.setRankId(++i);
                    newItem.setUserId(userId);
                    newItem.setWin((int) t.getScore());

                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    newItem.setUserName(jsonObject.getString("userName"));
                    newItem.setHeroAvatar(jsonObject.getString("heroAvatar"));

                    rankItemList.add(newItem);
                }

                _rankItemList = rankItemList;
            } catch (Exception ex) {
                //记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
