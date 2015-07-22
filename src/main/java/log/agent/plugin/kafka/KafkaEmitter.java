package log.agent.plugin.kafka;

import log.agent.core.ILogEvent;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import log.agent.plugin.IEmitter;

import java.util.*;

/**
 * Created by major.baek on 2015-04-07.
 */
public class KafkaEmitter implements IEmitter {

    private Producer<String, String> producer;
    private Properties kafkaProp = new Properties();
    private String topicName;

    public void initialize(Properties prop) {
        String brokerList = prop.getProperty("kafka.broker.list","localhost:9092");
        kafkaProp.put("metadata.broker.list", brokerList);

        String serializerClass = prop.getProperty("kafka.serializer.class","kafka.serializer.DefaultEncoder");
        kafkaProp.put("serializer.class", serializerClass);

        String ack = prop.getProperty("kafka.request.ack","0");
        kafkaProp.put("request.required.acks", ack);

        String partitionerClass = prop.getProperty("kafka.partitioner.class");

        if (partitionerClass != null) {
            kafkaProp.put("partitioner.class", "log.plugin." + partitionerClass);
        }

        String sync = prop.getProperty("kafka.producer.type", "sync");
        kafkaProp.put("producer.type", sync);

        String bufferingTime = prop.getProperty("kafka.buffering.max", "1000");
        kafkaProp.put("queue.buffering.max.ms", bufferingTime);

        // null: Vanish
        this.topicName = prop.getProperty("kafka.topic","null");

        this.producer = new Producer<String, String>(new ProducerConfig(kafkaProp));
    }

    public void emit(List<ILogEvent> batch) {
        String batchEvent;
        StringBuilder sb = new StringBuilder("");

        for (ILogEvent event:batch) {
            sb.append(event.toString());
        }

        //String msg = batchEvent.trim();
        batchEvent = sb.toString();
        if (batchEvent.length() <= 0) return;

        KeyedMessage<String, String> data = new KeyedMessage<String, String>(this.topicName, "" + ((int)Thread.currentThread().getId()), batchEvent);

        //long latency = System.currentTimeMillis();

        producer.send(data);

        //latency = System.currentTimeMillis() - latency;
    }

    public void emit(ILogEvent event) {
        KeyedMessage<String, String> data = new KeyedMessage<String, String>("test", "" + ((int)Thread.currentThread().getId()), event.toString());

        //long latency = System.currentTimeMillis();

        producer.send(data);

        //latency = System.currentTimeMillis() - latency;
    }
}
