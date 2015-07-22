package log.agent.core;

import com.lmax.disruptor.RingBuffer;

/**
 * Created by major.baek on 2015-02-16.
 */
public class LogEventProducer {
    private final RingBuffer<ILogEvent> ringBuffer;

    public LogEventProducer(LogStore store) {
        this.ringBuffer = store.getRingBuffer();
    }

    public void produce(ILogEvent log) {
        long sequence = ringBuffer.next();
        //System.out.println("Next : " + sequence);

        try {
            ILogEvent event = ringBuffer.get(sequence);
            event.set(log);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
