package com.zrgj519.campusBBS.entity.Event;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 生产事件
    public void fireEvent(Event event){
        // 生产发送后，监听相关topic的消费者会收到value
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
