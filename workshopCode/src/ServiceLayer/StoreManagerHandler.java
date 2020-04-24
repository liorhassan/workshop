package ServiceLayer;

import DomainLayer.Permission;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.List;

public class StoreManagerHandler {

    public String addStoreManager(String username, String storeName){

        SystemLogger.getInstance().writeEvent(String.format("Add manager command: username - %s, store name - %s",username,storeName));
        try {
            String [] args = {username, storeName};
            if(SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username and store name");
            if(! SystemHandler.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if(SystemHandler.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(SystemHandler.getInstance().checkIfActiveUserIsOwner(storeName))
                throw new RuntimeException("You must be this store owner for this command");
            if(SystemHandler.getInstance().checkIfUserIsManager(storeName, username))
                throw new RuntimeException("This username is already one of the store's managers");
            return SystemHandler.getInstance().appointManager(username, storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add manager error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String removeStoreManager(String username,String storename){
        SystemLogger.getInstance().writeError(String.format("Remove manager command: username - %s, store name - %s",username,storename));
        try{
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
}
