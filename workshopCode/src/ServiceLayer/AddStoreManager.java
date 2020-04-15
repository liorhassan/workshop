package ServiceLayer;

import DomainLayer.SystemHandler;

public class AddStoreManager {

    public String execute(String username, String storeName){

        try {
            return SystemHandler.getInstance().appointManager(username, storeName);
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

}
