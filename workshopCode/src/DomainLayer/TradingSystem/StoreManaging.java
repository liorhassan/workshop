package DomainLayer.TradingSystem;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "storeManagings")
public class StoreManaging implements Serializable {


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

    public StoreManaging(){};
    public StoreManaging(User appointer, String storeName, String appointeeName) {
        this.appointer = appointer;
        this.appointerName = appointer.getUsername();
        this.appointeeName = appointeeName;
        this.storeName = storeName;

        // TODO: init from db
        this.permissions = new LinkedList<>();
        this.permissions.add(new Permission(("View Purchasing History")));
    }

    public void initPermissions(){
        this.permissions = new LinkedList<>();
        List<Permission> perms = PersistenceController.readAllPermissions(storeName, appointeeName);
        for(Permission p: perms){
            this.permissions.add(p);
        }
    }

    public User getAppointer() {
        return appointer;
    }

    public List<Permission> getPermission(){
        return  permissions;
    }

    public void setPermission(List<Permission> perm){
        for(Permission p: this.permissions){
            PersistenceController.delete(p);
        }
        for(Permission p: perm){
            p.setStoreName(storeName);
            p.setAppointee(appointeeName);
            PersistenceController.create(p);
        }

        permissions = perm;
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

    public String getAppointeeName(){
        return this.appointeeName;
    }


    public String getAppoinerName() {
        return this.appointerName;
    }

    public void setAppointer(User app) {
        this.appointer = app;
    }
}
