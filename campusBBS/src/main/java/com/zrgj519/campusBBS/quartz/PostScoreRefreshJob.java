package com.zrgj519.campusBBS.quartz;

import com.zrgj519.campusBBS.entity.Post;
import com.zrgj519.campusBBS.service.ElasticsearchService;
import com.zrgj519.campusBBS.service.LikeService;
import com.zrgj519.campusBBS.service.PostService;
import com.zrgj519.campusBBS.util.CampusBBSConstant;
import com.zrgj519.campusBBS.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job {
    private static final Logger logger  = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;
    @Autowired
    private PostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    // 项目纪元（开始计算的元时间）
    private static final Date epoch;
    static {
            try {
                epoch  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
            } catch (ParseException e) {
                throw new RuntimeException("初始化项目纪元失败",e);
            }
    }
    // 到达指定时间后，计算缓存中所有需要计算分数的帖子。
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        // 获取需要计算帖子分数的帖子id结合
        BoundSetOperations<String, Integer> operations = redisTemplate.boundSetOps(redisKey);
        if(operations.size()==0){
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数: " + operations.size());
        while(operations.size()>0){
            this.refreshScore(operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }

    public void refreshScore(int id){
        Post discussPost = discussPostService.find(id);
        if(discussPost == null){
            logger.error("该帖子不存在: id = " + id);
            return;
        }
        // 是否精华
        boolean wonderful = discussPost.getStatus() == 1;
        // 评论数量
        int commentCount = discussPost.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(CampusBBSConstant.ENTITY_TYPE_POST,id);

        // 公式：log（精华分+评论数*10+点赞数*2）+（发布时间-项目纪元）

        // 计算权重 (wonderful ? 75 : 0) +
        double w = commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 （w可能为0，所以和1取大） + 距离天数
        double score = Math.log10(Math.max(w,1))
                + (discussPost.getCreateTime().getTime() - epoch.getTime())/(1000*3600*24);

        // 更新帖子分数
        discussPostService.updateScore(id,score);

        // 同步更新es服务器中
        discussPost.setScore(score);
        elasticsearchService.savePost(discussPost);
    }
}
