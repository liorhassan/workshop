package ServiceLayer;

import DomainLayer.Models.Store;
import DomainLayer.Models.User;
import DomainLayer.Permission;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.List;

public class StoreManagerHandler {

    public String addStoreManager(String username, String storeName){

        try {
            return SystemHandler.getInstance().appointManager(username, storeName);
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    public String removeStoreManager(String username,String storename){
        SystemLogger.getInstance().writeError(String.format("Remove manager command: username - %s, store name - %s",argToString(username),argToString(storename)));
        try{
            String[] args = {username,storename};
            if(SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username and store name");
            if(!SystemHandler.getInstance().storeExists(storename))
                throw new IllegalArgumentException("This store doesn't exist");
            if(!SystemHandler.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(!SystemHandler.getInstance().isUserStoreOwner(storename))
                throw new RuntimeException("You must be this store owner for this command");
            if(!SystemHandler.getInstance().isUserAppointer(username,storename))
                throw new RuntimeException("This username is not one of this store's managers appointed by you");
            return SystemHandler.getInstance().removeManager(username,storename);
        } catch(Exception e) {
            SystemLogger.getInstance().writeError("Remove manager error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String editManagerPermissions(String userName, List<Permission> permissions, String storeName ){
        try{
            SystemLogger.getInstance().writeEvent("Edit Permissions command");
            return SystemHandler.getInstance().editPermissions(userName, permissions, storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Edit Permissions error: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String argToString(String arg){
        return arg != null ? arg : "";
    }
}
