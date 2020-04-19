package DomainLayer;

public class SystemLogger {

    private static SystemLogger ourInstance = new SystemLogger();

    public static SystemLogger getInstance() {
        return ourInstance;
    }

    private SystemLogger() {
    }

    public void writeAction(String action){}

    public void writeError(String error){}
}
