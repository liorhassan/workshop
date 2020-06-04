package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "storeOwnings")
public class StoreOwning implements Serializable {

    @JoinColumn(name = "appointer", referencedColumnName = "username")
    private User appointer;

    @Id
    @Column(name = "storeName")
    private String storeName;

    @Id
    @Column(name = "appointeeName")
    private String appointeeName;

    private List<Permission> permissions;

    // first owner constructor
    public StoreOwning(String storeName, String appointeeName) {
        this.storeName = storeName;
        this.appointeeName = appointeeName;
        permissions = new ArrayList<>();
        String[] perms = {"Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History", "Add New Discount", "Add new Discount Policy"};
        for(String p : perms)
            permissions.add(new Permission(p));
    }

    public StoreOwning(User appointer, String storeName, String appointeeName) {
        this.appointer = appointer;
        this.storeName = storeName;
        this.appointeeName = appointeeName;
        this.permissions = new LinkedList<>();
    }

    public User getAppointer() {
        return appointer;
    }

    public List<Permission> getPermission(){
        return  permissions;
    }

    public String getAppointeeName(){
        return this.appointeeName;
    }
}
