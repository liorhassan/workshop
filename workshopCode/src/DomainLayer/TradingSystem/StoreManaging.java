package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "storeManagings")
public class StoreManaging implements Serializable {

    @ManyToOne
    @JoinColumn(name = "appointer", referencedColumnName = "username")
    private User appointer;

    @Id
    @Column(name = "storeName")
    private String storeName;

    @Id
    @Column(name = "appointeeName")
    private String appointeeName;

    // TODO: change transient!!!
    @Transient
    private List<Permission> permissions;

    public StoreManaging(User appointer, String storeName, String appointeeName) {
        this.appointer = appointer;
        this.appointeeName = appointeeName;
        this.storeName = storeName;

        // TODO: init from db
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

    public String getAppointeeName(){
        return this.appointeeName;
    }
}
