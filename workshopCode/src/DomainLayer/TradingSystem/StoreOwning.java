package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.User;
import DataAccessLayer.PersistenceController;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "storeOwnings")
public class StoreOwning implements Serializable {

//    @ManyToOne(cascade = {CascadeType.ALL})
//    @JoinColumn(name = "appointer")
    @Column(name = "appointer")
    private String appointerName;
    @Transient
    private User appointer;

    @Id
    @Column(name = "storeName")
    private String storeName;

    @Id
    @Column(name = "appointeeName")
    private String appointeeName;

    @Transient
    private List<Permission> permissions;

    public StoreOwning(){}
    //first owner constructor
    public StoreOwning(String storeName, String appointeeName) {
        this.storeName = storeName;
        this.appointeeName = appointeeName;
        permissions = new ArrayList<>();
        String[] perms = {"Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History", "Add New Discount", "Add new Discount Policy"};
        for(String p : perms)
            permissions.add(new Permission(p));

        // save to db
        PersistenceController.create(this);
    }

    public StoreOwning(User appointer, String storeName, String appointeeName) {
        this.appointer = appointer;
        this.appointeeName = appointerName;
        this.storeName = storeName;
        this.appointeeName = appointeeName;
        this.permissions = new ArrayList<>();
        String[] perms = {"Add Manager", "Add Owner", "Approve New Owner", "Remove Manager", "Remove Owner", "Edit Permissions", "Manage Supply", "View Purchasing History", "Add New Discount", "Add New Purchase Policy"};
        for(String p : perms)
            permissions.add(new Permission(p));
        // save to db
        PersistenceController.create(this);
    }

    public User getAppointer() {
        return appointer;
    }

    public List<Permission> getPermission(){
        return  permissions;
    }


    public String getAppointeeName() {
        return this.appointeeName;
    }


    public void initPermissions() {
        this.permissions = new LinkedList<>();
        String[] perms = {"Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History", "Add New Discount", "Add new Discount Policy"};
        for(String p : perms)
            permissions.add(new Permission(p));

    }
    public String getAppoinerName() {
        return this.appointerName;
    }

    public void setAppointer(User app) {
        this.appointer = app;
    }
}
