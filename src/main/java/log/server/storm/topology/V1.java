package log.server.storm.topology;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import log.agent.type.LogLevel;
import log.agent.type.LogSchema;
import log.server.storm.bolt.*;
import log.server.storm.bolt.base.*;
import log.server.storm.spout.Beacon;
import log.server.storm.spout.KafkaTopicSpout;
import log.util.Time;

/**
 * Created by major.baek on 2015-04-22.
 */
public class V1 {

    private final String    SPOUT_ID = "v1_spout";
    private final String    BEACON_SPOUT_ID = "beacon_spout";

    private final String    JSON_CONVERTER_ID = "jsonconverter";
    private final String    S3_EXPORT_BOLT_ID = "s3export";
    private final String    INFO_DROP_BOLT_ID = "infoDrop";

    private final String    TR_LOG_FILTER_BOLT_ID = "trlogfilter";
    private final String    LINE_LOG_FILTER_BOLT_ID = "linelogfilter";

    private final String    INFO_FILTER_BOLT_ID = "infofilter";
    private final String    ERROR_FILTER_BOLT_ID = "errorfilter";
    private final String    FATAL_FILTER_BOLT_ID = "fatalfilter";
    private final String    WARNING_FILTER_BOLT_ID = "warningfilter";
    private final String    DEBUG_FILTER_BOLT_ID = "debugfilter";
    private final String    SYS_FILTER_BOLT_ID = "sysfilter";


    private final String    SYS_ALALYSER_BOLT_ID = "sysanalyser";
    private final String    INFO_ANALYSER_BOLT_ID = "infoanalyser";
    private final String    ERROR_ANALYSER_BOLT_ID = "erroranalyser";
    private final String    WARNING_ANALYSER_BOLT_ID = "warninganalyser";
    private final String    DEBUG_ANALYSER_BOLT_ID = "debuganalyser";
    private final String    FATALL_ANALYSER_BOLT_ID = "fatalanalyser";
    private final String    MONGO_EXPORT_BOLT_ID = "mongoexport";

    private final String    TRANSACTION_TIME_ALERT_BOLT_ID = "trtimechecker";

    private KafkaTopicSpout spout;
    private String      topologyName;
    private TopologyBuilder builder;
    private Config      config;
    private int         numWorkers = 2;

    public V1(String topologyName, String topicName, String zkList) {
        this.spout = KafkaTopicSpout.createSpout(SPOUT_ID, topicName, zkList);
        this.topologyName = topologyName;

        this.build();
        this.run(numWorkers);
    }

    void build() {
        int numJSONConverterThread = 4;
        int numInfoDropBoltThread = 4;
        int numMongoExportThread = 4;
        int numTRTimeCheckThread = 4;
        int numFilterThread = 2;

        Beacon beaconSpout = new Beacon();

        JSONConverterBolt jsonBolt = new JSONConverterBolt();
        S3ExportBolt s3ExportBolt = new S3ExportBolt(LogLevel.ERROR | LogLevel.FATAL | LogLevel.WARNING);
        DropBolt infoDropBolt = new DropBolt(LogLevel.INFO);

        TypeFilterBolt trLogFilter = new TypeFilterBolt(LogSchema.TRANSACTION_TYPE);
        TypeFilterBolt lineLogFilter = new TypeFilterBolt(LogSchema.LINE_TYPE);

        LevelFilterBolt errorFilter = new LevelFilterBolt(LogLevel.ERROR);
        LevelFilterBolt criticalFilter = new LevelFilterBolt(LogLevel.FATAL);
        LevelFilterBolt warningFilter = new LevelFilterBolt(LogLevel.WARNING);
        LevelFilterBolt debugFilter = new LevelFilterBolt(LogLevel.DEBUG);
        LevelFilterBolt infoFilter = new LevelFilterBolt(LogLevel.INFO);
        LevelFilterBolt sysFilter = new LevelFilterBolt(LogLevel.SYSTEM | LogLevel.ALARM | LogLevel.MONITOR);

        ProcessBolt sysProcessor = new SystemLogProcessor();
        ProcessBolt infoProcessor = new InfoLogProcessor();
        ProcessBolt errorProcessor = new ErrorLogProcessor();
        ProcessBolt debugProcessor = new DebugLogProcessor();
        ProcessBolt warningProcessor = new WarningLogProcessor();
        ProcessBolt criticalProcessor = new FatalLogProcessor(5 * Time.SECOND.getTime());

        MongoDBExportBolt mongoExportBolt = new MongoDBExportBolt();

        TransactionTimeAlerter trTimeChecker = new TransactionTimeAlerter(30);

        this.builder = new TopologyBuilder();
        this.config = new Config();

        this.builder.setSpout(this.SPOUT_ID, this.spout);
        this.builder.setSpout(this.BEACON_SPOUT_ID, beaconSpout);

        this.builder.setBolt(this.JSON_CONVERTER_ID, jsonBolt, numJSONConverterThread)
                .shuffleGrouping(this.SPOUT_ID);

        this.builder.setBolt(this.TR_LOG_FILTER_BOLT_ID, trLogFilter, numFilterThread)
                .shuffleGrouping(this.JSON_CONVERTER_ID);

        this.builder.setBolt(this.TRANSACTION_TIME_ALERT_BOLT_ID, trTimeChecker,numTRTimeCheckThread)
                .shuffleGrouping(this.TR_LOG_FILTER_BOLT_ID);

        this.builder.setBolt(this.LINE_LOG_FILTER_BOLT_ID, lineLogFilter, numFilterThread)
                .shuffleGrouping(this.JSON_CONVERTER_ID);

        this.builder.setBolt(this.S3_EXPORT_BOLT_ID, s3ExportBolt)
                .globalGrouping(this.LINE_LOG_FILTER_BOLT_ID);

        this.builder.setBolt(this.INFO_FILTER_BOLT_ID, infoFilter, numFilterThread)
                .shuffleGrouping(this.S3_EXPORT_BOLT_ID);

        this.builder.setBolt(this.INFO_ANALYSER_BOLT_ID, infoProcessor)
                .globalGrouping(this.INFO_FILTER_BOLT_ID);

        this.builder.setBolt(this.INFO_DROP_BOLT_ID, infoDropBolt, numInfoDropBoltThread)
                .shuffleGrouping(this.LINE_LOG_FILTER_BOLT_ID);

        this.builder.setBolt(this.ERROR_FILTER_BOLT_ID, errorFilter, numFilterThread)
                .shuffleGrouping(this.INFO_DROP_BOLT_ID);

        this.builder.setBolt(this.FATAL_FILTER_BOLT_ID, criticalFilter, numFilterThread)
                .shuffleGrouping(this.INFO_DROP_BOLT_ID);

        this.builder.setBolt(this.WARNING_FILTER_BOLT_ID, warningFilter, numFilterThread)
                .shuffleGrouping(this.INFO_DROP_BOLT_ID);

        this.builder.setBolt(this.DEBUG_FILTER_BOLT_ID, debugFilter, numFilterThread)
                .shuffleGrouping(this.INFO_DROP_BOLT_ID);

        this.builder.setBolt(this.SYS_FILTER_BOLT_ID, sysFilter, numFilterThread)
                .shuffleGrouping(this.INFO_DROP_BOLT_ID);

        this.builder.setBolt(this.SYS_ALALYSER_BOLT_ID, sysProcessor)
                .globalGrouping(this.SYS_FILTER_BOLT_ID)
                .globalGrouping(this.BEACON_SPOUT_ID);

        this.builder.setBolt(this.ERROR_ANALYSER_BOLT_ID, errorProcessor)
                .globalGrouping(this.ERROR_FILTER_BOLT_ID)
                .globalGrouping(this.BEACON_SPOUT_ID);

        this.builder.setBolt(this.FATALL_ANALYSER_BOLT_ID, criticalProcessor)
                .globalGrouping(this.FATAL_FILTER_BOLT_ID)
                .globalGrouping(this.BEACON_SPOUT_ID);

        this.builder.setBolt(this.DEBUG_ANALYSER_BOLT_ID, debugProcessor)
                .globalGrouping(this.DEBUG_FILTER_BOLT_ID)
                .globalGrouping(this.BEACON_SPOUT_ID);

        this.builder.setBolt(this.WARNING_ANALYSER_BOLT_ID, warningProcessor)
                .globalGrouping(this.WARNING_FILTER_BOLT_ID)
                .globalGrouping(this.BEACON_SPOUT_ID);

        this.builder.setBolt(this.MONGO_EXPORT_BOLT_ID, mongoExportBolt, numMongoExportThread)
                .shuffleGrouping(this.ERROR_ANALYSER_BOLT_ID)
                .shuffleGrouping(this.FATALL_ANALYSER_BOLT_ID)
                .shuffleGrouping(this.DEBUG_ANALYSER_BOLT_ID)
                .shuffleGrouping(this.SYS_ALALYSER_BOLT_ID)
                .shuffleGrouping(this.WARNING_ANALYSER_BOLT_ID);
    }

    void run(int numWorkers) {
        int nw = 1;

        if (numWorkers > 1) nw = numWorkers;

        config.setNumWorkers(nw);

        try {
            StormSubmitter.submitTopology(this.topologyName, this.config, this.builder.createTopology());
        } catch (InvalidTopologyException ite) {
            System.out.println(ite.toString());
        } catch (AlreadyAliveException aae) {
            System.out.println(aae.toString());
        }
    }

    public static void main(String [] args) {
        if (args.length < 3) {
            System.out.println("Usage: V1 [topology name] [topic name] [zookeeper list]");
        }

        new V1(args[0], args[1], args[2]);
    }
}
