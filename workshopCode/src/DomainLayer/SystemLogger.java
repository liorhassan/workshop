package DomainLayer;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SystemLogger {

    private static SystemLogger ourInstance = new SystemLogger();

    public static SystemLogger getInstance() {
        return ourInstance;
    }

    File logDir = new File("../logs/");
    private final String eventLogURL = "logs/EventLogger.log";
    private final String errorLogURL = "logs/ErrorLogger.log";
    private Logger eventLogger;
    private Logger errorLogger;
    private FileHandler eventLog;
    private FileHandler errorLog;

    private SystemLogger() {
        try {
            if( !(logDir.exists()) )
                logDir.mkdir();
            eventLog = new FileHandler(eventLogURL,true);
            errorLog = new FileHandler(errorLogURL,true);
            eventLogger = Logger.getLogger("Event Logger");
            errorLogger = Logger.getLogger("Error Logger");
            SimpleFormatter formatter = new SimpleFormatter();
            eventLog.setFormatter(formatter);
            errorLog.setFormatter(formatter);
            eventLogger.addHandler(eventLog);
            errorLogger.addHandler(errorLog);
        } catch (Exception e){
            System.out.println("error:");
            System.out.println(e.getMessage());
        }
    }

    public void writeEvent(String event){
        eventLogger.info(event);
    }

    public void writeError(String error){
        errorLogger.info(error);
    }
}
