package ServiceLayer;

import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class Logout {

    public String execute(){
        SystemLogger.getInstance().writeEvent("Logout command");
        return SystemHandler.getInstance().logout();
    }
}
