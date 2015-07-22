package log.agent.core;

import com.lmax.disruptor.EventHandler;
import log.agent.plugin.IEmitter;

import java.util.ArrayList;

/**
 * Created by major.baek on 2015-02-16.
 */
public class LogEventHandler<T extends ILogEvent> implements EventHandler<T> {
    private int id;
    private int batchSize;
    private int proccessed;
    private int parallelismHint;
    private IEmitter iEmitter;
    private ArrayList<ILogEvent> events;

    public LogEventHandler(int id, int batchSize, int parallelismHint, IEmitter iEmitter) throws Exception {
        super();
        this.id = id;
        this.batchSize = batchSize;
        this.proccessed = 0;
        this.parallelismHint = parallelismHint;
        this.iEmitter = iEmitter;
        this.events = new ArrayList<ILogEvent>();
    }

    public void onEvent(ILogEvent event, long sequence, boolean endOfBatch) throws Exception {
       if (sequence % this.parallelismHint == this.id) {
            this.proccessed++;

            if (this.proccessed >= this.batchSize) {
                //System.out.println("Batch " + this.id + ": " + this.proccessed);
                this.proccessed = 0;

                this.iEmitter.emit(this.events);
                this.events.clear();
            } else {
                this.events.add(event);
                //System.out.println(event.toString());
            }
       }
    }
}
