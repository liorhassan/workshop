package ServiceLayer;

import DomainLayer.Permission;
import DomainLayer.SystemHandler;

import java.util.List;

public class EditPermissions {

    public String execute(String userName, List<Permission> permissions, String storeName ){
        try{
            SystemHandler.getInstance().editPermissions(userName, permissions, storeName);
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}
