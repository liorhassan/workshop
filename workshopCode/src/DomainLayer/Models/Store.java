package DomainLayer.Models;

import DomainLayer.*;

import java.util.Collection;
import java.util.HashMap;

public class Store {

    private Inventory products;
    private String name;
    private HashMap<User, StoreManaging> managements;
    private HashMap<User, StoreOwning> ownerships;
    private String description;
    private User storeFirstOwner;
    private StorePurchaseHistory purchaseHistory;
    private DiscountPolicy discountPolicy;

    public Store(String name, String description, User firstOwner, StoreOwning owning) {
        this.name = name;
        this.description = description;
        this.storeFirstOwner = firstOwner;
        this.products = new Inventory();
        this.managements = new HashMap<>();
        this.ownerships = new HashMap<>();
        this.purchaseHistory = new StorePurchaseHistory(this);
        this.ownerships.put(firstOwner, owning);
        this.discountPolicy = new DiscountPolicy();
    }


    public User getStoreFirstOwner() {
        return storeFirstOwner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Collection<Product> getProducts(){
        return  products.getProducts().keySet();
    }

    public boolean checkIfProductAvailable(String product, int amount){
        Product p = getProductByName(product);
        if(p == null)
            return false;
        return products.getProducts().get(p) >= amount;
    }

    public HashMap<Product, Integer> getInventory() {
        return products.getProducts();
    }



    public Product getProductByName(String productName){
        for (Product p : products.getProducts().keySet()) {
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
        for (Product p : products.getProducts().keySet()){
            if (p.getName().equals(productName))
                return true;
        }
        return false;
    }

    public Inventory getProductsInventory(){
        return this.products;
    }

    public void addToInventory(String productName, double productPrice, Category productCategory, String productDescription, int amount) {
        products.getProducts().put(new Product(productName, productCategory, productDescription, productPrice), amount);
    }

    public void updateInventory(String productName, double productPrice, Category productCategory, String productDescription, int amount) {
        for (Product p : products.getProducts().keySet()){
            if (p.getName().equals(productName)) {
                p.setPrice(productPrice);
                p.setCategory(productCategory);
                p.setDescription(productDescription);
                products.getProducts().put(p, amount);
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
        if (!managements.containsKey(user))
            managements.put(user, storeManaging);
    }

    // before activating this function make sure the new Owner is registered!!!
    // the function will return true if added successfully and false if the user is already an owner
    public void addStoreOwner(User newOwner, StoreOwning storeOwning) {
        if (!ownerships.containsKey(newOwner))
            this.ownerships.put(newOwner, storeOwning);
    }

    public void purchaseProduct(Product p, int amount){
        if(checkIfProductAvailable(p.getName(), amount)){
            int prevAmount = this.products.getProducts().get(p);
            if(prevAmount == amount){
                this.products.getProducts().remove(p);
            }
            else{
                this.products.getProducts().replace(p, prevAmount - amount);
            }
        }
    }

    public void purchaseBasket(Basket b){
        Collection<ProductItem> products = b.getProductItems();
        for (ProductItem pi : products) {
            Product p = pi.getProduct();
            int amount = pi.getAmount();
            purchaseProduct(p, amount);
        }
    }

    public double calculateTotalCheck(Basket b){
        double total = 0;
        for (ProductItem pi: b.getProductItems()) {
            //TODO: HANDEL PURCHASE POLICY
            total += (pi.getAmount() * (pi.getProduct().getPrice() - this.discountPolicy.calcProductDiscount(pi.getProduct(), pi.getAmount()))) ;
        }
        return total;
    }



    public StorePurchaseHistory getPurchaseHistory(){
        return this.purchaseHistory;
    }

    public String getDescription(){
        return this.description;
    }

    public void returnProdyuctsToStock(Basket b){
        for(ProductItem pi: b.getProductItems()){
            if(products.getProducts().get(pi.getProduct()) != null){
                products.getProducts().put(pi.getProduct(), products.getProducts().get(pi.getProduct()) + pi.getAmount());
            }
            else{
                products.getProducts().put(pi.getProduct(), pi.getAmount());
            }
        }
    }

    public void addDiscount(String productName, double percentage){
        this.discountPolicy.addDiscount(getProductByName(productName), percentage);
    }
}