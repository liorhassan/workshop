package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.User;

import java.util.LinkedList;
import java.util.List;


public class StoreManaging {

    User appointer;
    private List<Permission> permissions;

    public StoreManaging(User appointer) {
        this.appointer = appointer;
        this.permissions = new LinkedList<>();
        this.permissions.add(new Permission(("View Purchasing History")));
    }

    public User getAppointer() {
        return appointer;
    }

    public List<Permission> getPermission(){
        return  permissions;
    }

    public void setPermission(List<Permission> p){
        permissions = p;
    }

    public boolean havePermission(String permission){
        if(permission.isEmpty())
            return false;
        for(Permission p: permissions){
            if(p.getAllowedAction().equals(permission))
                return true;
        }
        return false;
    }
}
