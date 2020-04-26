package ServiceLayer;

import DomainLayer.Models.Store;
import DomainLayer.Models.User;
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
            if(!SystemHandler.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(!SystemHandler.getInstance().checkIfActiveUserIsOwner(storeName))
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


    public String editManagerPermissions(String userName, List<String> permissions, String storeName ){
        SystemLogger.getInstance().writeEvent("Edit Permissions command");
        try{
            String args[] = new String[3];
            if(permissions.isEmpty()) {
                args[0] = userName;
                args[1] = storeName;
                args[2] = "";
            }
            else{
                args = new String[2 + permissions.size()];
                args[0] = userName;
                args[1] = storeName;
                for(int i = 0; i < permissions.size(); i++){
                    args[i+2] = permissions.get(i);
                }
            }


            if(SystemHandler.getInstance().emptyString(args)){
                throw new RuntimeException("Must enter username, permissions list and store name");
            }
            if(!SystemHandler.getInstance().storeExists(storeName)){
                throw new RuntimeException("This store doesn't exists");
            }
            if(!SystemHandler.getInstance().userExists(userName)){
                throw new RuntimeException("This username doesn't exist");
            }
            if(!SystemHandler.getInstance().checkIfActiveUserIsOwner(storeName)){
                throw new RuntimeException("You must be this store owner for this command");
            }
            if(!(SystemHandler.getInstance().checkIfUserIsManager(storeName, userName) && SystemHandler.getInstance().isUserAppointer(userName, storeName))){
                throw new RuntimeException("You can't edit this user's privileges");
            }
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
