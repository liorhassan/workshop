package ServiceLayer;

import DomainLayer.SystemHandler;

public class AddStoreOwner {

    public String execute(String username, String storeName){
        try {
            return SystemHandler.getInstance().appointOwner(username, storeName);
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}
