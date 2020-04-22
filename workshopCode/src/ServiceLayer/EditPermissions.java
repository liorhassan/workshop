package ServiceLayer;

import DomainLayer.Permission;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.List;

public class EditPermissions {

    public String execute(String userName, List<Permission> permissions, String storeName ){
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
