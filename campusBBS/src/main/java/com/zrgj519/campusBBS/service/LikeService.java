package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired      //  由配置类定义的RedisTemplate完成自动注入
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param userId        点赞者的id
     * @param entityType    点赞内容的类型
     * @param entityId      点赞内容的id
     * @param entityUserId  点赞内容的发布者id
     */
    public void like(int userId , int entityType, int entityId , int entityUserId){
        // 获取到两个操作对象表的key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
       // 点赞操作包含两个对数据的操作：增加内容的的点赞数量，增加发布者收到的赞。应该进行事物管理
        // 使用Redis的**编程式**事物管理
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 判断用户是否已经点过赞(查询key对应集合中是否有该userId)？赞：取消赞
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事物
                operations.multi();
                if(isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    // 因为redis是单线程的，所以单个命令也是原子的
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                // 提交事物
                return operations.exec();
            }
        });
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType , int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某人对某实体的点赞状态
     * @param userId        某人
     * @param entityType    某实体
     * @param entityId      某实体Id
     * @return              1代表点赞，0代表没点赞，（以后可开发，踩：-1）
     */
    public int findEntityLikeStatus(int userId , int entityType ,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        // 判读该用户是否获得过赞，没有（null）
        // 此处要使用Integer进行转型，因为结果返回的是Object类型，可能为null，如果使用int，为null时会爆空指针异常
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }

    public double findTagLikeCount(int entityTypePost, String postTag) {
        return 0;
    }

    public int findTagLikeByExcept(String userTag) {
        return 0;
    }
}
