package DomainLayer;

public class SystemLogger {

    private static SystemLogger ourInstance = new SystemLogger();

    public static SystemLogger getInstance() {
        return ourInstance;
    }

    private SystemLogger() {
    }

    public void writeEvent(String event){}

    public void writeError(String error){}
}
