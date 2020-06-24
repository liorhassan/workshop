package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.UUID;

public class StoreManagerHandler {

    public String addStoreManager(UUID session_id, String username, String storeName){

        SystemLogger.getInstance().writeEvent(String.format("Add manager command: username - %s, store name - %s",username,storeName));
        try {
            String [] args = {username, storeName};
            if(SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username and store name");
            if(! SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if(!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(SystemFacade.getInstance().checkIfUserIsManager(storeName, username))
                throw new RuntimeException("This username is already one of the store's managers");
            if(!(SystemFacade.getInstance().checkIfActiveUserIsOwner(session_id, storeName)||(SystemFacade.getInstance().checkIfActiveUserIsManager(session_id,storeName) && SystemFacade.getInstance().checkIfUserHavePermission(session_id, storeName, "Add Manager"))))
                throw new RuntimeException("You must be this store owner for this command or store manager with permissions");
            return createJSONMsg("SUCCESS", SystemFacade.getInstance().appointManager(session_id, username, storeName));
            //return SystemFacade.getInstance().appointManager(username, storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add manager error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    public String removeStoreManager(UUID session_id, String username,String storename){
        SystemLogger.getInstance().writeError(String.format("Remove manager command: username - %s, store name - %s",argToString(username),argToString(storename)));
        try{
            String[] args = {username,storename};
            if(SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username and store name");
            if(!SystemFacade.getInstance().storeExists(storename))
                throw new IllegalArgumentException("This store doesn't exist");
            if(!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(!SystemFacade.getInstance().isUserAppointer(session_id, username,storename))
                throw new RuntimeException("This username is not one of this store's managers appointed by you");
            return SystemFacade.getInstance().removeManager(username,storename);
            //return SystemFacade.getInstance().removeManager(username,storename);
        } catch(Exception e) {
            SystemLogger.getInstance().writeError("Remove manager error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }


    public String editManagerPermissions(UUID session_id, String userName, List<String> permissions, String storeName ){
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


            if(SystemFacade.getInstance().emptyString(args)){
                throw new RuntimeException("Must enter username, permissions list and store name");
            }
            if(!SystemFacade.getInstance().storeExists(storeName)){
                throw new RuntimeException("This store doesn't exists");
            }
            if(!SystemFacade.getInstance().userExists(userName)){
                throw new RuntimeException("This username doesn't exist");
            }
            if(!(SystemFacade.getInstance().checkIfUserIsManager(storeName, userName) && SystemFacade.getInstance().isUserAppointer(session_id, userName, storeName))){
                throw new RuntimeException("You can't edit this user's privileges");
            }
            return createJSONMsg("SUCCESS", SystemFacade.getInstance().editPermissions(userName, permissions, storeName));
            //return SystemFacade.getInstance().editPermissions(userName, permissions, storeName);
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Edit Permissions error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    private String argToString(String arg){
        return arg != null ? arg : "";
    }

    public String createJSONMsg(String type, String content) {
        JSONObject response = new JSONObject();
        response.put(type, content);
        return response.toJSONString();
    }
}
