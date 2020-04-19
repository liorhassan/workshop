package DomainLayer;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SystemLogger {

    private static SystemLogger ourInstance = new SystemLogger();

    public static SystemLogger getInstance() {
        return ourInstance;
    }

    private final String eventLogURL = "/logs/EventLogger.log";
    private final String errorLogURL = "/logs/ErrorLogger.log";
    private Logger eventLogger;
    private Logger errorLogger;
    private FileHandler eventLog;
    private FileHandler errorLog;

    private SystemLogger() {
        try {
            eventLog = new FileHandler(eventLogURL);
            errorLog = new FileHandler(errorLogURL);
            eventLogger = Logger.getLogger("Event Logger");
            errorLogger = Logger.getLogger("Error Logger");
            SimpleFormatter formatter = new SimpleFormatter();
            eventLog.setFormatter(formatter);
            errorLog.setFormatter(formatter);
            eventLogger.addHandler(eventLog);
            errorLogger.addHandler(errorLog);
        } catch (Exception e){}
    }

    public void writeEvent(String event){
        eventLogger.info(event);
    }

    public void writeError(String error){
        errorLogger.info(error);
    }
}
