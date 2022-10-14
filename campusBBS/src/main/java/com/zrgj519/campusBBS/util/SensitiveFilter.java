package com.zrgj519.campusBBS.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT="***";
    // 根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 过滤敏感词
     * @param text  需要过滤的文本
     * @return  过滤后的文本(使用***代替敏感词)
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        // 指向前缀树的指针
        TrieNode cur = rootNode;
        // 指向待过滤字符子串开头的指针
        int begin = 0;
        // 指向可能是敏感字符的指针
        int next = 0;

        StringBuilder res = new StringBuilder();

        while(begin < text.length()){
            char c = text.charAt(next);
            // 跳过符号 如*艹*你*妈中间的*
            if(isSymbol(c)){
                // 前面字符没有包含敏感词字符，否则begin应该不动。
                if(cur == rootNode){
                    res.append(c);
                    begin++;
                }
                // 无论前面字符是否包含敏感字符，next都跳过当前字符。
                next++;
                // 因为是特殊字符，不同搜索比较前缀树
                continue;
            }
            // 检查下级节点
            cur = cur.getSubNode(c);
            // 当前字符开头不包含敏感词字符
            if(cur == null ){
                res.append(text.charAt(begin));
                next = ++ begin;
                cur = rootNode;
            }
            // begin~next是敏感词
            else if(cur.isKeywordEnd()){
                res.append(REPLACEMENT);
                begin=++next;
                cur = rootNode;
            }
            // 当前字符开头包含敏感词，但是还不确定begin~next这个子串是否是敏感词，next继续移动比较
            else{
                next++;
            }
        }
        return res.toString();
    }
    public boolean isSymbol(char c){
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    //根据敏感词文件构造前缀树
    @PostConstruct      //  使用这个注解，让这个方法在容器创建时就调用这个方法
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String sensitiveWord;
            while ((sensitiveWord= reader.readLine())!=null)
            {
                // 添加到前缀树
                this.addSensitiveWord(sensitiveWord);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }
    // 将一个敏感词添加到前缀树中
    public void addSensitiveWord(String sensitiveWord){
        // 指向前缀树的当前指针
        TrieNode cur = rootNode;
        for (int i = 0; i < sensitiveWord.length(); i++) {
            char c = sensitiveWord.charAt(i);
            TrieNode subNode = cur.getSubNode(c);
            // 当前字符在前缀树中没有被构建
            if(subNode == null){
                // 初始化子节点
                subNode = new TrieNode();
                cur.addSubNode(c,subNode);
            }
            // 判断当前字符是否是字符串末尾，如果是，加上标志
            if(i == sensitiveWord.length()-1){
                TrieNode node = cur.getSubNode(c);
                node.setKeywordEnd(true);
            }

            // 移动cur
            cur = subNode;
        }
    }
    // 定义前缀树的节点
    private class TrieNode {
        // 敏感词结束标识
        private boolean isKeywordEnd = false;
        // 子节点，key是字符，value是字符对应的TrieNode
        private Map<Character,TrieNode> subNode = new HashMap<>();

        public boolean isKeywordEnd(){
            return this.isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNode.put(c,node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNode.get(c);
        }
    }
}
