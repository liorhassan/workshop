package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.*;

public class Store implements Serializable {

    private Inventory inventory;
    private String name;
    private HashMap<User, StoreManaging> managements;
    private HashMap<User, StoreOwning> ownerships;
    private String description;
    private User storeFirstOwner;
    private StorePurchaseHistory purchaseHistory;
    private List<DiscountPolicy> discountPolicies;

    private HashMap<Product, DiscountBaseProduct> discountsOnProducts;
    private List<DiscountBInterface> discountsOnBaskets;
    private List<PurchasePolicy> purchasePolicies;

    @Transient
    private HashMap<Basket, List<ProductItem>> reservedProducts;
    private int discountID_counter;

    public Store(String name, String description, User firstOwner, StoreOwning owning) {
        this.name = name;
        this.description = description;
        this.storeFirstOwner = firstOwner;
        this.inventory = new Inventory();
        inventory.init();
        this.managements = new HashMap<>();
        this.ownerships = new HashMap<>();
        this.purchaseHistory = new StorePurchaseHistory(this);
        this.ownerships.put(firstOwner, owning);
        this.discountPolicies = new ArrayList<>();
        this.discountsOnBaskets = new ArrayList<>();
        this.discountsOnProducts = new HashMap<>();
        this.purchasePolicies = new ArrayList<>();
        this.reservedProducts= new HashMap<>();
        this.discountID_counter = 0;
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
    public HashMap<Product, DiscountBaseProduct> getDiscountsOnProducts() {
        return discountsOnProducts;
    }

    public Collection<Product> getProducts(){
        return  inventory.getProducts().keySet();
    }



    public HashMap<Product, Integer> getInventory() {
        return inventory.getProducts();
    }

    public DiscountBInterface getDiscountById(int discountId){
        for (DiscountBInterface dis : discountsOnProducts.values()) {
            if (dis.getDiscountID() == discountId)
                return dis;
        }
        for (DiscountBInterface dis : discountsOnBaskets) {
            if (dis.getDiscountID() == discountId)
                return dis;
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
        for (Product p : inventory.getProducts().keySet()){
            if (p.getName().equals(productName))
                return true;
        }
        return false;
    }

    public Inventory getProductsInventory(){
        return this.inventory;
    }

    public void addToInventory(String productName, double productPrice, Category productCategory, String productDescription, int amount) {
        Product p = new Product(productName, productCategory, productDescription, productPrice, this.name, amount);
        inventory.getProducts().put(p, amount);

        // save to DB
        PersistenceController.create(p);
    }

    public void updateInventory(String productName, double productPrice, Category productCategory, String productDescription, int amount) {
        for (Product p : inventory.getProducts().keySet()){
            if (p.getName().equals(productName)) {
                p.setPrice(productPrice);
                p.setCategory(productCategory);
                p.setDescription(productDescription);
                inventory.getProducts().put(p, amount);
                p.setQuantity(amount);
                PersistenceController.update(p);
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
        if (!managements.containsKey(user)) {
            managements.put(user, storeManaging);
            NotificationSystem.getInstance().notify(user.getUsername(), "You have been appointed as " + name + "'s store manager");
        }
    }

    // before activating this function make sure the new Owner is registered!!!
    // the function will return true if added successfully and false if the user is already an owner
    public void addStoreOwner(User newOwner, StoreOwning storeOwning) {
        if (!ownerships.containsKey(newOwner))
            this.ownerships.put(newOwner, storeOwning);
        NotificationSystem.getInstance().notify(newOwner.getUsername(), "You have been appointed as " + name + "'s store owner");
    }



    //for each inventory in the basket - checks if the product meets the purchase policy requirements
    //if it does - reserve the product and adds it to the reserved inventory list
    public void reserveBasket(Basket b){
        //this field save all the inventory that have been reserved
        this.reservedProducts.put(b, new LinkedList<>());
        for(PurchasePolicy p : purchasePolicies){
            if(!p.purchaseAccordingToPolicy(b))
                throw new RuntimeException("Your purchase doesn’t match the store’s policy");
        }
        Collection<ProductItem> products = b.getProductItems();
        for (ProductItem pi : products) {
            Product p = pi.getProduct();
            int amount = pi.getAmount();
            if(!this.inventory.reserveProduct(p, amount)){
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
            this.inventory.unreserveProduct(pi.getProduct(), pi.getAmount());
        }
    }

    public double calculateTotalCheck(Basket b, List<DiscountBInterface> discounts){
        double total = 0;
        if(discounts.size()>0) {
            for (ProductItem pi : b.getProductItems()) {
                boolean found = false;
                for (DiscountBInterface disc : discounts) {
                    if (disc instanceof DiscountBaseProduct){
                        if(((DiscountBaseProduct) disc).getProductName().equals(pi.getProduct().getName())){
                            total = total + disc.calc(b, pi.getProduct().getPrice());
                            discounts.remove(disc);
                            found = true;
                        }
                    }
                }
                if(!found){
                    total = total + pi.getAmount() * pi.getProduct().getPrice();
                }
            }
            if(discounts.size()> 0){
                for(DiscountBInterface disc : discounts){
                    if(disc.canGet(total)) {
                        total = disc.calc(b, total);
                    }
                }
            }
        }
        else{
            total = b.calcPrice();
        }

        for(DiscountBInterface dis :discountsOnBaskets){
            if(!discounts.contains(dis)){
                    total = dis.calc(b, total);
            }
        }

        return total;
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


    public void addDiscountForProduct(String productName, int percentage, int limit, boolean onAll){
        Product p = this.inventory.getProductByName(productName);
        if(p != null)
            discountsOnProducts.put(p, new DiscountBaseProduct(discountID_counter, productName, limit, percentage, onAll));
        discountID_counter ++;
    }

    public void addDiscountForBasket(int percentage, int limit, boolean onprice){

        discountsOnBaskets.add(new DiscountBaseBasket(discountID_counter, limit, percentage, onprice));
        discountID_counter ++;

    }

    public void addDiscountPolicy(DiscountPolicy discountPolicy){
        discountPolicies.add(discountPolicy);
    }

    public String viewDiscount(){
        JSONArray discountsdes = new JSONArray();
        for(DiscountBInterface dis :discountsOnBaskets){
            JSONObject curr = new JSONObject();
            curr.put("discountId", dis.getDiscountID());
            curr.put("discountString", dis.discountDescription());
            discountsdes.add(curr);
        }
        for(DiscountBInterface dis :discountsOnProducts.values()){
            JSONObject curr = new JSONObject();
            curr.put("discountId", dis.getDiscountID());
            curr.put("discountString", dis.discountDescription());
            discountsdes.add(curr);
        }
        return discountsdes.toJSONString();
    }


    public List<DiscountBInterface> getDiscountsOnBasket(Basket basket){
        List<DiscountBInterface> output = new ArrayList<>();
        for(DiscountBaseProduct dis : discountsOnProducts.values()){
            if(dis.canGet(basket.getProductAmount(dis.getProductName())))
                output.add(dis);
        }
        for(DiscountPolicy disP : discountPolicies){
            output = disP.checkDiscounts(output, basket);
        }
        return output;
    }

    public void notifyOwners(Basket b, String userName) {
        String msg = userName + " bought some products from the store " + name + " you own: ";
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
            curr.put("category", p.getCategory().name());
            products.add(curr);
        }
        return products.toJSONString();
    }

    public Product getProductByName(String name) {
        return this.inventory.getProductByName(name);
    }

    public boolean checkIfProductAvailable(String productName, int amount) {
        return this.inventory.checkIfProductAvailable(productName, amount);
    }

    //for store unit test
    public void reserveProduct(Product p, int amount) {
        this.inventory.reserveProduct(p, amount);
    }
}