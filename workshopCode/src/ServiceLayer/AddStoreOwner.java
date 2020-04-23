package ServiceLayer;

import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class AddStoreOwner {

    public String execute(String username, String storeName) {
        SystemLogger.getInstance().writeEvent(String.format("Add store owner command: new owner username - %s, store name - %s",username,storeName));
        try {
            return SystemHandler.getInstance().appointOwner(username, storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add store owner error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
