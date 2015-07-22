package log.agent.type;

/**
 * Created by major.baek on 2015-04-27.
 */
public class LogLevel {
    public static final int FATAL     = 0x1; // Critical Error
    public static final int ERROR     = 0x2; // General Error
    public static final int DEBUG     = 0x4; // Debug Message
    public static final int WARNING   = 0x8; // Warning Message
    public static final int INFO      = 0x10; // Information Message
    public static final int ALARM     = 0x20; // Alarm Message - At analysis stage, this will be run notification subsystem
    public static final int MONITOR   = 0x40; // Monitoring Message - For checking system health or reporting status
    public static final int SYSTEM    = 0x80; // Non application message
    public static final int TRACE     = 0x100;
    public static final int ALL       = 0xFFF;

    private static final int ALARM_LEVEL = 97;
    private static final int MONITOR_LEVEL = 98;
    private static final int SYSTEM_LEVEL = 99;

    private static final int FATAL_LEVEL = 100;
    private static final int ERROR_LEVEL = 200;
    private static final int WARNING_LEVEL = 300;
    private static final int INFO_LEVEL = 400;
    private static final int DEBUG_LEVEL = 500;
    private static final int TRACE_LEVEL = 600;

    public static boolean isSet(int source, int target) {
        if ( (source & target) != 0) return true;

        return false;
    }

    public static int maskLevel(int level) {
        int mask = ALL;

        switch (level) {
            case FATAL_LEVEL:
                mask = FATAL;
            break;
            case ALARM_LEVEL:
                mask = ALARM;
            break;
            case ERROR_LEVEL:
                mask = ERROR;
            break;
            case MONITOR_LEVEL:
                mask = MONITOR;
            break;
            case WARNING_LEVEL:
                mask = WARNING;
            break;
            case SYSTEM_LEVEL:
                mask = SYSTEM;
            break;
            case INFO_LEVEL:
                mask = INFO;
            break;
            case DEBUG_LEVEL:
                mask = DEBUG;
            break;
            case TRACE_LEVEL:
                mask = TRACE;
            break;
            default:
                mask = ALL;
            break;
        }

        return mask;
    }

    public static int intLevel(String levelString) {
        int level = TRACE_LEVEL;

        if (levelString.equalsIgnoreCase("FATAL")) {
            level = FATAL_LEVEL;
        } else if (levelString.equalsIgnoreCase("ALARM")) {
            level = ALARM_LEVEL;
        } else if (levelString.equalsIgnoreCase("ERROR")) {
            level = ERROR_LEVEL;
        } else if (levelString.equalsIgnoreCase("MONITOR")) {
            level = MONITOR_LEVEL;
        } else if (levelString.equalsIgnoreCase("WARNING")) {
            level = WARNING_LEVEL;
        } else if (levelString.equalsIgnoreCase("SYSTEM")) {
            level = SYSTEM_LEVEL;
        } else if (levelString.equalsIgnoreCase("INFO")) {
            level = INFO_LEVEL;
        } else if (levelString.equalsIgnoreCase("DEBUG")) {
            level = DEBUG_LEVEL;
        }

        return level;
    }

    public static int intLevel(int level) {
        int iLevel = TRACE_LEVEL;

        switch (level) {
            case FATAL :
                iLevel = FATAL_LEVEL;
            break;
            case ALARM :
                iLevel = ALARM_LEVEL;
            break;
            case ERROR :
                iLevel = ERROR_LEVEL;
            break;
            case MONITOR :
                iLevel = MONITOR_LEVEL;
            break;
            case WARNING :
                iLevel = WARNING_LEVEL;
            break;
            case SYSTEM :
                iLevel = SYSTEM_LEVEL;
            break;
            case INFO :
                iLevel = INFO_LEVEL;
            break;
            case DEBUG :
                iLevel = DEBUG_LEVEL;
            break;
        }

        return iLevel;
    }
}
