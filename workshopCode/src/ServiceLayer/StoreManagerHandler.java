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

    public String editManagerPermissions(String userName, List<String> permissions, String storeName ){
        SystemLogger.getInstance().writeEvent("Edit Permissions command");
        try{
            String args[] = new String[2 + permissions.size()];
            args[0] = userName;
            args[1] = storeName;
            for(int i = 0; i < permissions.size(); i++){
                args[i+2] = permissions.get(i);
            }

            if(SystemHandler.getInstance().emptyString(args)){
                throw new RuntimeException("Must enter username, permissions list and store name");
            }
            if(SystemHandler.getInstance().checkIfStoreExists(storeName)){
                throw new RuntimeException("This store doesn't exists");
            }
            if(!store.getOwnerships().containsKey(this.activeUser)){
                throw new RuntimeException("You must be this store owner for this command");
            }


            return SystemHandler.getInstance().editPermissions(userName, permissions, storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Edit Permissions error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
