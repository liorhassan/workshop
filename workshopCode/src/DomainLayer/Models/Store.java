package DomainLayer.Models;

import DomainLayer.*;

import java.util.Collection;
import java.util.HashMap;

public class Store {

    private HashMap<Product,Integer> products;
    private String name;
    private HashMap<User, StoreManaging> managements;
    private HashMap<User, StoreOwning> ownerships;
    private String description;
    private User storeFirstOwner;
    private StorePurchaseHistory purchaseHistory;

    public Store(String name, String description, User firstOwner, StoreOwning owning) {
        this.name = name;
        this.description = description;
        this.storeFirstOwner = firstOwner;
        this.products = new HashMap<>();
        this.managements = new HashMap<>();
        this.ownerships = new HashMap<>();
        this.purchaseHistory = new StorePurchaseHistory(this);
        this.ownerships.put(firstOwner, owning);
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

    public boolean checkIfProductAvailable(String product, int amount){
        Product p = getProductByName(product);
        if(p == null)
            return false;
        return products.get(p) >= amount;
    }

    public HashMap<Product, Integer> getInventory() {
        return products;
    }

    public void setInventory(HashMap<Product, Integer> inventory) {
        this.products = inventory;
    }

    public Product getProductByName(String productName){
        for (Product p : products.keySet()) {
            if (p.getName().equals(productName))
                return p;
        }
        return null;
    }

    public boolean isOwner(User user) {
        return ownerships.containsKey(user);
    }

    public boolean isManager(User user) {
        return managements.containsKey(user);
    }

    public User getAppointer(User user) {
        User appointer = null;
        StoreManaging manage = managements.get(user);
        if(manage != null)
            appointer = manage.getAppointer();
        return appointer;

    }

    public void removeManager(User user) {
        managements.remove(user);
        user.removeStoreManagement(this);
    }

    public boolean hasProduct(String productName) {
        for (Product p : products.keySet()){
            if (p.getName().equals(productName))
                return true;
        }
        return false;
    }

    public void addToInventory(String productName, double productPrice, Category productCategory, String productDescription, int amount) {
        products.put(new Product(productName, productCategory, productDescription, productPrice), amount);
    }

    public void updateInventory(String productName, double productPrice, Category productCategory, String productDescription, int amount) {
        for (Product p : products.keySet()){
            if (p.getName().equals(productName)) {
                p.setPrice(productPrice);
                p.setCategory(productCategory);
                p.setDescription(productDescription);
                products.put(p, amount);
                break;
            }
        }
    }

    public HashMap<User, StoreManaging> getManagements() {
        return managements;
    }

    public HashMap<User, StoreOwning> getOwnerships() {
        return ownerships;
    }

    public void setManagements(HashMap<User, StoreManaging> managements) {
        this.managements = managements;
    }

    public void addManager(User user, StoreManaging storeManaging){
        managements.put(user, storeManaging);
    }

    // before activating this function make sure the new Owner is registered!!!
    // the function will return true if added successfully and false if the user is already an owner
    public void addStoreOwner(User newOwner, StoreOwning storeOwning) {
        this.ownerships.put(newOwner, storeOwning);
    }

    public void purchaseProduct(Product p, int amount){
        if(checkIfProductAvailable(p.getName(), amount)){
            int prevAmount = this.products.get(p);
            if(prevAmount == amount){
                this.products.remove(p);
            }
            else{
                this.products.replace(p, prevAmount - amount);
            }
        }
    }

    public StorePurchaseHistory getPurchaseHistory(){
        return this.purchaseHistory;
    }

    public String getDescription(){
        return this.description;
    }
}