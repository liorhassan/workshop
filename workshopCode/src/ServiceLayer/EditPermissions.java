package ServiceLayer;

import DomainLayer.Permission;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.List;

public class EditPermissions {

    public String execute(String userName, List<Permission> permissions, String storeName ){
        try{

            String result =  SystemHandler.getInstance().editPermissions(userName, permissions, storeName);

            //logger
            String permissionsStr;
            if(permissions.isEmpty()){
                permissionsStr = "empty permissions list";
            }
            else{
                permissionsStr = "{";
                for(Permission p: permissions){
                    permissionsStr = permissionsStr.concat(p.getAllowedAction() + ", ");
                }
                permissionsStr = permissionsStr + "}";
            }
            SystemLogger.getInstance().writeEvent("Edit Permissions command: user name - " + userName + ", permissions - " + permissionsStr + ", store name - " + storeName);
            return result;
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Edit Permissions error: " + e.getMessage());
            return e.getMessage();
        }
    }
}
