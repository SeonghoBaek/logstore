package log.server.storm.spout;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

/**
 * Created by major.baek on 2015-04-22.
 */
public class KafkaTopicSpout extends KafkaSpout {

    private static String ZK_ROOT = "/logstore";

    private KafkaTopicSpout(SpoutConfig spoutConf) {
        super(spoutConf);
    }

    public static KafkaTopicSpout createSpout(String spoutId, String topic, String zkList) {
        ZkHosts zkHosts = new ZkHosts(zkList);

        SpoutConfig spoutConfig = new SpoutConfig(zkHosts, topic, ZK_ROOT, spoutId);
        //spoutConfig.forceFromStart = true;

        KafkaTopicSpout spout = new KafkaTopicSpout(spoutConfig);

        return spout;
    }
}
