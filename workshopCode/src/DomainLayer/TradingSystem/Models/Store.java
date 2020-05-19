package DomainLayer.TradingSystem.Models;

import DomainLayer.TradingSystem.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Store {

    private Inventory products;
    private String name;
    private HashMap<User, StoreManaging> managements;
    private HashMap<User, StoreOwning> ownerships;
    private String description;
    private User storeFirstOwner;
    private StorePurchaseHistory purchaseHistory;
    private DiscountPolicy discountPolicy;
    private PurchasePolicy purchasePolicy;
    private HashMap<Basket, List<ProductItem>> reservedProducts;

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
        this.purchasePolicy = new PurchasePolicy();
        this.reservedProducts= new HashMap<>();
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
        NotificationSystem.getInstance().notify(user.getUsername(), "You have been no longer " + name + "'s manager");

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
        NotificationSystem.getInstance().notify(newOwner.getUsername(), "You have been appointed as " + name + "'s store owner");
    }

    //reserve a specific product
    //returns false if the product is unavailable in the inventory
    public boolean reserveProduct(Product p, int amount){
        if(checkIfProductAvailable(p.getName(), amount)){
            int prevAmount = this.products.getProducts().get(p);
            if(prevAmount == amount){
                this.products.getProducts().remove(p);
            }
            else{
                this.products.getProducts().replace(p, prevAmount - amount);
            }
            return true;
        }
        return false;
    }

    //for each products in the basket - checks if the product meets the purchase policy requirements
    //if it does - reserve the product and adds it to the reserved products list
    public void reserveBasket(Basket b){
        //this field save all the products that have been reserved
        this.reservedProducts.put(b, new LinkedList<>());
        if(!this.purchasePolicy.purchaseAccordingToPolicy(b)){
            throw new RuntimeException("Your purchase doesn’t match the store’s policy");
        }
        Collection<ProductItem> products = b.getProductItems();
        for (ProductItem pi : products) {
            Product p = pi.getProduct();
            int amount = pi.getAmount();
            if(!reserveProduct(p, amount)){
                throw new RuntimeException("There is currently no stock of " + amount + " " + p.getName() + " products");
            }
            this.reservedProducts.get(b).add(pi);
        }
    }

    public List<ProductItem> getReservedProducts(Basket b){
        return this.reservedProducts.get(b);
    }

    public void unreserveBasket(Basket b){
        for(ProductItem pi: this.reservedProducts.get(b)){
            if(products.getProducts().get(pi.getProduct()) != null){
                products.getProducts().put(pi.getProduct(), products.getProducts().get(pi.getProduct()) + pi.getAmount());
            }
            else{
                products.getProducts().put(pi.getProduct(), pi.getAmount());
            }
        }
    }

    public double calculateTotalCheck(Basket b){
        double total = 0;
        for (ProductItem pi: b.getProductItems()) {
            total += (pi.getAmount() * pi.getProduct().getPrice());
        }
        double discount = this.discountPolicy.calcProductDiscount(b);
        return total - discount;
    }


    public void addStorePurchaseHistory(Basket b, User u){
        Purchase p = new Purchase(b, u);
        p.getPurchasedProducts().computeCartPrice();
        this.purchaseHistory.addPurchase(p);
    }



    public StorePurchaseHistory getPurchaseHistory(){
        return this.purchaseHistory;
    }

    public String getDescription(){
        return this.description;
    }


    public void addDiscount(String productName, double percentage){
        this.discountPolicy.addDiscount(getProductByName(productName), percentage);
    }

    public void notifyOwners(Basket b, String userName) {
        String msg = userName + "bought some groceries from the store " + name + " you own: ";
        for(ProductItem pi: b.getProductItems()) {
            msg += pi.getProduct().getName() + ", ";
        }
        msg.substring(0, msg.length() - 2);
        msg += ".";
        for(User u: ownerships.keySet()) {
            NotificationSystem.getInstance().notify(u.getUsername(), msg);
        }
    }

    public JSONArray getAllProducts(){
        JSONArray products = new JSONArray();
        for(Product p: getInventory().keySet()) {
            JSONObject curr = new JSONObject();
            curr.put("name", p.getName());
            curr.put("price", p.getPrice());
            curr.put("store", this.name);
            curr.put("description", p.getDescription());
            products.add(curr);
        }

        return products;
    }

    public String getProductsJS(){
        JSONArray products = new JSONArray();
        for(Product p: getInventory().keySet()) {
            JSONObject curr = new JSONObject();
            curr.put("name", p.getName());
            curr.put("description", p.getDescription());
            curr.put("price", p.getPrice());
            curr.put("amount", getInventory().get(p));
            products.add(curr);
        }
        return products.toJSONString();
    }
}