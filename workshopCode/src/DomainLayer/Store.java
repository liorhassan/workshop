package DomainLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Store {

    private List<Product> products;
    private String name;
    private HashMap<User,StoreManaging> managments;
    private HashMap<User,StoreOwning> ownerships;

    public Store(String name) {
        this.name = name;
        this.products = new ArrayList<>();
        this.managments = new HashMap<>();
        this.ownerships = new HashMap<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Product> getProducts(){
        //TODO: implement
        return  null;
    }

    public boolean checkIfProductAvailable(String product){
        //TODO: implement
        return true;
    }

    public Product getProductByName(String productName){
        for (Product p : products) {
            if (p.getName() == productName)
                return p;
        }
        return null;
    }

    public boolean isOwner(User user) {
        return ownerships.containsKey(user);
    }

    public User getAppointer(User user) {
        User appointer = null;
        StoreManaging manage = managments.get(user);
        if(manage != null)
            appointer = manage.getAppointer();
        return appointer;

    }

    public void removeManager(User user) {
        managments.remove(user);
        user.removeStoreManagment(this);
    }
}