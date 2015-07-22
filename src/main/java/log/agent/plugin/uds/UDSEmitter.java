package log.agent.plugin.uds;

import log.agent.core.ILogEvent;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import log.agent.plugin.IEmitter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Created by major.baek on 2015-04-08.
 */
public class UDSEmitter implements IEmitter {
    private AFUNIXSocket socket = null;
    private AFUNIXSocketAddress addr = null;
    private BufferedOutputStream bos = null;

    public void initialize(Properties prop) {
        try {
            System.out.println("Using UDS Emitter");
            this.socket = AFUNIXSocket.newInstance();
            String path = prop.getProperty("uds.path","/dev/null");

            this.addr = new AFUNIXSocketAddress(new File(path));
            this.socket.connect(this.addr);
            this.bos = new BufferedOutputStream(this.socket.getOutputStream());

            if (this.bos == null) {
                System.out.println("Output Stream NULL");
            }

            System.out.println("UDS Emitter: OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void emit(ILogEvent event) {

    }

    public void emit(List<ILogEvent> batch) {
        String batchEvent = "";

        for (ILogEvent event:batch) {
            batchEvent += event.toString();
        }

        try {
            if (batchEvent.trim().length() > 0) {
                this.bos.write(batchEvent.trim().getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
