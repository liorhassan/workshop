package DomainLayer;

import java.util.LinkedList;
import java.util.List;

public class StoreManaging {

    User appointer;
    private List<Permission> permission;

    public StoreManaging(User appointer) {
        this.appointer = appointer;
        this.permission = new LinkedList<>();

    }

    public User getAppointer() {
        return appointer;
    }

    public List<Permission> getPermission(){
        return  permission;
    }

    public void setPermission(List<Permission> p){
        permission = p;
    }

}
