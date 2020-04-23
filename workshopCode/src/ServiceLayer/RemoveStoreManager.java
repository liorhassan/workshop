package ServiceLayer;

import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.concurrent.ExecutionException;

public class RemoveStoreManager {
    public String execute(String username,String storename){
        SystemLogger.getInstance().writeError(String.format("Remove manager command: username - %s, store name - %s",username,storename));
        try{
            return SystemHandler.getInstance().removeManager(username,storename);
        } catch(Exception e) {
            SystemLogger.getInstance().writeError("Remove manager error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
