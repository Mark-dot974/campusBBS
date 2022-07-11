package com.zrgj519.campusBBS.service;

import com.zrgj519.campusBBS.dao.elasticsearch.PostRepository;
import com.zrgj519.campusBBS.entity.Post;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class ElasticsearchService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public void savePost(Post post){
        postRepository.save(post);
    }

    public void deletePost(Post post){
        postRepository.delete(post);
    }

    public Page<Post> searchDiscussPost(String keyword, int current , int limit){
        // 构造查询匹配的关键词以及关键词匹配的是那部分内容、排序规则，高亮设置（在关键词前后加上特定标签，然后自己在前端css进行设置）
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content","tag"))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
//                        new HighlightBuilder.Field("tag").preTags("<em>").postTags("</em>")
                ).build();

        // 根据上面设置的查询条件，查询到相应内容，并对相应的内容放到page中
        return elasticsearchTemplate.queryForPage(searchQuery, Post.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }
                List<Post> list = new ArrayList<>();
                // 遍历根据keyword搜索出来的结果，构造成post对象 （hit  --- 》   post ）
                for (SearchHit hit : hits) {
                    System.out.println("===========");
                    Post post = new Post();

                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String tag = hit.getSourceAsMap().get("tag").toString();
                    post.setTag(tag);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    // 处理高亮显示的结果
                    // 设置title中匹配到关键词部分内容，title中可能有多句话匹配到关键词，通常只取第一个，内容content同。
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }

                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    HighlightField tagField = hit.getHighlightFields().get("tag");
                    if (tagField != null) {
                        post.setTag(tagField.getFragments()[0].toString());
                    }
                    System.out.println("post = " + post);
                    list.add(post);
                }

                System.out.println("list.size() = " + list.size());
                return new AggregatedPageImpl(list, pageable,
                        hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
            }
        });

    }
}
