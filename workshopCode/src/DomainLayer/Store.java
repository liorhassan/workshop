package DomainLayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Store {

    private HashMap<Product,Integer> products;
    private String name;
    private HashMap<User,StoreManaging> managments;
    private HashMap<User,StoreOwning> ownerships;
    private String description;
    private User storeFirstOwner;

    public Store(String name, String description, User firstOwner) {
        this.name = name;
        this.description = description;
        this.storeFirstOwner = firstOwner;
        this.products = new HashMap<>();
        this.managments = new HashMap<>();
        this.ownerships = new HashMap<>();
        this.ownerships.put(firstOwner, new StoreOwning());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Collection<Product> getProducts(){
        return  products.keySet();
    }

    public boolean checkIfProductAvailable(String product){
        Product p = getProductByName(product);
        if(p==null)
            return false;
        return products.get(p)>0;
    }

    public HashMap<Product, Integer> getInventory() {
        return inventory;
    }

    // help for use case 2.7
    public Boolean checkProductInventory(Product p, int amount){
        if(! inventory.containsKey(p))
            return false;
        int available_amount =  inventory.get(p);
        if(amount> available_amount)
            return false;
         return true;

    }

    public void setInventory(HashMap<Product, Integer> inventory) {
        this.inventory = inventory;
    }

    public Product getProductByName(String productName){
        for (Product p : products.KeySet()) {
            if (p.getName().equals(productName))
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

    public boolean hasProduct(String productName) {
        for (Product p : products.keySet()){
            if (p.getName().equals(productName))
                return true;
        }
        return false;
    }

    public void addToInventory(String productName, double productPrice, Category productCategory, String productDescription) {
        products.put(new Product(productName, productCategory, productDescription, productPrice),1);
    }

    public void updateInventory(String productName, double productPrice, Category productCategory, String productDescription) {
        for (Product p : products.keySet()){
            if (p.getName().equals(productName)) {
                p.setPrice(productPrice);
                p.setCategory(productCategory);
                p.setDescription(productDescription);
                break;
            }
        }
    }

    // before activating this function make sure the new Owner is registered!!!
    // the function will return true if added successfully and false if the user is already an owner
    public boolean addStoreOwner(User newOwner) {
        if (this.ownerships.get(newOwner) != null)
            return false;
        this.ownerships.put(newOwner, new StoreOwning());
        return true;
    }
}