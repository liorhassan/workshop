package ServiceLayer;

import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;


public class OpenNewStore {

    public String execute(String storeName, String storeDescription){
        SystemLogger.getInstance().writeEvent(String.format("Open new store command: store name - %s, store description - %s", storeName, storeDescription));
        try {
            return getMessage(SystemHandler.getInstance().openNewStore(storeName,storeDescription));
        }
        catch (RuntimeException e){
            SystemLogger.getInstance().writeError("Open new store error: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String getMessage(String openNewStoreResult){
        return openNewStoreResult;
    }
}
