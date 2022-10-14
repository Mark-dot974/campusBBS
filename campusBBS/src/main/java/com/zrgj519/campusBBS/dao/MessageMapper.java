package com.zrgj519.campusBBS.dao;

import com.zrgj519.campusBBS.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 查询当前用户的会话列表，针对每个会话只返回一条最新的私信。
    List<Message> selectConversations(int userId, int offset , int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询会话下的所有信息内容，进行分页展示
    List<Message> selectLetters(String conversationId,int offset , int limit);

    // 查询某个会话下信息的数量
    int selectLetterCount(String conversationId);

    // 查询用户某个会话的未读消息数量
    int selectLetterUnreadCount(int userId,String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 修改消息的状态
    int updateStatus(List<Integer> ids,int status);

    Message selectLatestNotice(int userId, String conversationId);

    int selectTopicNoticeCount(int userId, String conversationId);

    int selectTopicNoticeUnreadCount(int userId, String conversationId);

    List<Message> selectTopicNotices(int userId, String conversationId, int offset, int limit);
}
