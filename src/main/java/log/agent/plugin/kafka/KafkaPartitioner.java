package log.agent.plugin.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Created by major.baek on 2015-03-19.
 */
public class KafkaPartitioner implements Partitioner {
    public KafkaPartitioner(VerifiableProperties props) {}

    public int partition(Object key, int i) {
        int partition = 0;
        int iKey = Integer.parseInt((String)key);
        if (iKey > 0) {
            partition = iKey % i;
        }

        return partition;
    }
}
