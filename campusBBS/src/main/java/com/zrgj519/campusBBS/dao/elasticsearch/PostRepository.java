package com.zrgj519.campusBBS.dao.elasticsearch;

import com.zrgj519.campusBBS.entity.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
// Post --- 存储的对象类型         Integer ---- 主键的类型（post.id）
public interface PostRepository extends ElasticsearchRepository<Post,Integer> {
}
