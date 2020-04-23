package ServiceLayer;

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
