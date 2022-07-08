//package com.zrgj519.campusBBS.quartz;
//
//import com.mark.community.entity.DiscussPost;
//import com.mark.community.service.DiscussPostService;
//import com.mark.community.service.ElasticsearchService;
//import com.mark.community.service.LikeService;
//import com.mark.community.util.CommunityConstant;
//import com.mark.community.util.HostHolder;
//import com.mark.community.util.RedisKeyUtil;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.BoundSetOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class PostScoreRefreshJob implements Job {
//    private static final Logger logger  = LoggerFactory.getLogger(PostScoreRefreshJob.class);
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//    @Autowired
//    private DiscussPostService discussPostService;
//    @Autowired
//    private LikeService likeService;
//    @Autowired
//    private ElasticsearchService elasticsearchService;
//    @Autowired
//    private HostHolder hostHolder;
//
//    // 项目纪元（开始计算的元时间）
//    private static final Date epoch;
//    static {
//            try {
//                epoch  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
//            } catch (ParseException e) {
//                throw new RuntimeException("初始化项目纪元失败",e);
//            }
//    }
//    // 到达指定时间后，计算缓存中所有需要计算分数的帖子。
//    @Override
//    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        String redisKey = RedisKeyUtil.getPostScoreKey();
//        // 获取需要计算帖子分数的帖子id结合
//        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
//        if(operations.size()==0){
//            logger.info("[任务取消] 没有需要刷新的帖子!");
//            return;
//        }
//
//        logger.info("[任务开始] 正在刷新帖子分数: " + operations.size());
//        while(operations.size()>0){
//            this.refreshScore((Integer)operations.pop());
//            updateCompositiveScore((Integer)operations.pop());
//        }
//        logger.info("[任务结束] 帖子分数刷新完毕!");
//    }
//
//    public void refreshScore(int id){
//        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
//        if(discussPost == null){
//            logger.error("该帖子不存在: id = " + id);
//            return;
//        }
//        // 是否精华
//        boolean wonderful = discussPost.getStatus() == 1;
//        // 评论数量
//        int commentCount = discussPost.getCommentCount();
//        // 点赞数量
//        long likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST,id);
//
//        // 公式：log（精华分+评论数*10+点赞数*2）+（发布时间-项目纪元）
//
//        // 计算权重
//        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
//        // 分数 = 帖子权重 （w可能为0，所以和1取大） + 距离天数
//        double score = Math.log10(Math.max(w,1))
//                + (discussPost.getCreateTime().getTime() - epoch.getTime())/(1000*3600*24);
//
//        // 更新帖子分数
//        discussPostService.updateScore(id,score);
//
//        // 同步更新es服务器中
//        discussPost.setScore(score);
//        elasticsearchService.saveDiscussPost(discussPost);
//    }
//
//    // 计算帖子分数
//    public void updateCompositiveScore(int id){
//        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
//        if(discussPost == null){
//            logger.error("该帖子不存在: id = " + id);
//            return;
//        }
//        // 归一化的发布时间（即越新的帖子，值越接近1）
//        double newScore = (discussPost.getCreateTime().getTime() - epoch.getTime())/(epoch.getTime());
//        // 计算综合分数
//        double score = getRecommendScore(id)/3.8 + newScore;
//
//        // 更新帖子分数
//        discussPostService.updateScore(id,score);
//
//        // 同步更新es服务器中
//        discussPost.setScore(score);
//        elasticsearchService.saveDiscussPost(discussPost);
//    }
//
//    public double getRecommendScore(int id){
//        double score = 0;
//        String tags = discussPostService.getDiscussPostTags(id);
//        if(tags == null) return 0;
//        String[] discussPostTags = tags.split(",");
//        String interest = hostHolder.getUser().getInterest();
//        String[] userTags = interest.split(",");
//        for(String postTag : discussPostTags){
//            double tagScore = 0;
//            for(String userTag:userTags){
//                tagScore += (likeService.findTagLikeCount(CommunityConstant.ENTITY_TYPE_POST,postTag)) / Math.log10(1+likeService.findTagLikeByExcept(userTag));
//            }
//            score *= tagScore;
//        }
//        return score;
//    }
//}
