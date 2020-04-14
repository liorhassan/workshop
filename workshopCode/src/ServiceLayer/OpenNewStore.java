package ServiceLayer;

import DomainLayer.SystemHandler;


public class OpenNewStore {
    public String execute(String storeName, String storeDescription){
        try {
            return getMessage(SystemHandler.getInstance().openNewStore(storeName,storeDescription));
        }
        catch (RuntimeException e){
            return e.getMessage();
        }
    }

    private String getMessage(String openNewStoreResult){
        return openNewStoreResult;
    }
}
