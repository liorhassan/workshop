package DomainLayer;

import java.util.LinkedList;
import java.util.List;

public class StoreOwning {

    User appointer;
    private List<Permission> permission;

    // first owner constructor
    public StoreOwning() {}

    public StoreOwning(User appointer) {
        this.appointer = appointer;
        this.permission = new LinkedList<>();
    }

    public User getAppointer() {
        return appointer;
    }

    public List<Permission> getPermission(){
        return  permission;
    }


}
