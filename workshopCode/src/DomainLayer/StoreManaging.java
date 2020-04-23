package DomainLayer;

import java.util.LinkedList;
import java.util.List;

public class StoreManaging {

    User appointer;
    private List<Permission> permissions;

    public StoreManaging(User appointer) {
        this.appointer = appointer;
        this.permissions = new LinkedList<>();

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

}
