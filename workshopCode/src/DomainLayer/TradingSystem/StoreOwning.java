package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StoreOwning {

    User appointer;
    private List<Permission> permissions;

    // first owner constructor
    public StoreOwning() {
        permissions = new ArrayList<>();
        String[] perms = {"Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History", "Add New Discount", "Add new Discount Policy"};
        for(String p : perms)
            permissions.add(new Permission(p));
    }

    public StoreOwning(User appointer) {
        this.appointer = appointer;
        this.permissions = new ArrayList<>();
        String[] perms = {"Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History", "Add New Discount", "Add new Discount Policy"};
        for(String p : perms)
            permissions.add(new Permission(p));
    }

    public User getAppointer() {
        return appointer;
    }

    public List<Permission> getPermission(){
        return  permissions;
    }


}
