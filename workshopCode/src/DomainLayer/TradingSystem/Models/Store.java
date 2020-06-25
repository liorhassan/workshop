package DomainLayer.TradingSystem.Models;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.*;
import net.bytebuddy.description.modifier.Ownership;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "stores")
public class Store implements Serializable {

    @Transient
    private Inventory inventory;

    @Id
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;


    @Column(name = "storeFirstOwner")
    private String firstOwnerName;
    @Transient
    private User storeFirstOwner;

    @Transient
    private ConcurrentHashMap<User, StoreManaging> managements;
    @Transient
    private ConcurrentHashMap<User, StoreOwning> ownerships;
    @Transient
    private ConcurrentHashMap<User, AppointmentAgreement> waitingAgreements;
    @Transient
    private StorePurchaseHistory purchaseHistory;
    @Transient
    private List<DiscountBInterface> discountPolicies;
    @Transient
    private List<DiscountBInterface> discountsOnProducts;
    @Transient
    private List<DiscountBInterface> discountsOnBasket;
    @Transient
    private List<PurchasePolicy> notStandAlonePolicies;
    @Transient
    private List<PurchasePolicy> purchasePolicies;
    @Transient
    private boolean doubleDiscounts;                    //on products and basketPrice
    @Transient
    private int discountID_counter;
    @Transient
    private int purchaseID_counter;

    public Store(){};
    public Store(String name, String description, User firstOwner, StoreOwning owning) throws SQLException {
        this.name = name;
        this.description = description;
        this.storeFirstOwner = firstOwner;
        this.firstOwnerName = firstOwner.getUsername();
        this.inventory = new Inventory();
        this.managements = new ConcurrentHashMap<>();
        this.ownerships = new ConcurrentHashMap<>();
        this.ownerships.put(firstOwner, owning);
        this.purchaseHistory = new StorePurchaseHistory(this);
        this.discountPolicies = new ArrayList<>();
        this.discountsOnBasket = new ArrayList<>();
        this.discountsOnProducts = new ArrayList<>();
        this.purchasePolicies = new ArrayList<>();
        this.notStandAlonePolicies = new ArrayList<>();
        this.waitingAgreements = new ConcurrentHashMap<>();
        this.discountID_counter = 0;
    }

    public void init() throws SQLException {
        this.storeFirstOwner = SystemFacade.getInstance().getUserByName(firstOwnerName);
        this.inventory = new Inventory();
        inventory.init(name);
        this.ownerships = new ConcurrentHashMap<>();
        this.managements = new ConcurrentHashMap<>();
        this.purchaseHistory = new StorePurchaseHistory(this);
        this.discountPolicies = new ArrayList<>();
        this.discountsOnBasket = new ArrayList<>();
        this.notStandAlonePolicies = new ArrayList<>();
        this.discountsOnProducts = new ArrayList<>();
        this.purchasePolicies = new ArrayList<>();
        this.waitingAgreements = new ConcurrentHashMap<>();
        this.discountID_counter = 0;
        this.purchaseID_counter = 0;
        this.purchaseID_counter = 0;
        this.doubleDiscounts = true;
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

    public List<DiscountBInterface> getDiscountsOnProducts() {
        return discountsOnProducts;
    }

    public List<DiscountBInterface> getDiscountPolicies() {
        return discountPolicies;
    }

    public Collection<Product> getProducts() {
        return inventory.getProducts().keySet();
    }

    public void setDoubleDiscounts(boolean doubleDiscounts) {
        this.doubleDiscounts = doubleDiscounts;
    }


    public ConcurrentHashMap<Product, Integer> getInventory() {
        return inventory.getProducts();
    }

    public DiscountBInterface getDiscountById(int discountId) {
        for (DiscountBInterface dis : discountsOnProducts) {
            if (dis instanceof DiscountSimple) {
                if (((DiscountSimple) dis).getDiscountID() == discountId)
                    return dis;
            }
        }
        return null;
    }

    public PurchasePolicy getPurchasePolicyById(int purchaseId) {
        for (PurchasePolicy pp : notStandAlonePolicies) {
            if (pp instanceof PurchasePolicyProduct) {
                if (((PurchasePolicyProduct) pp).getPurchaseId() == purchaseId)
                    return pp;
            }
            if (pp instanceof PurchasePolicyStore) {
                if (((PurchasePolicyStore) pp).getPurchaseId() == purchaseId)
                    return pp;
            }
        }

        for (PurchasePolicy pp : purchasePolicies) {
            if (pp instanceof PurchasePolicyProduct) {
                if (((PurchasePolicyProduct) pp).getPurchaseId() == purchaseId)
                    return pp;
            }
            if (pp instanceof PurchasePolicyStore) {
                if (((PurchasePolicyStore) pp).getPurchaseId() == purchaseId)
                    return pp;
            }
        }

        return null;
    }

    public boolean hasRevDiscountOnProduct(String productName) {
        Product product = getProductByName(productName);
        for (DiscountBInterface dis : discountsOnProducts) {
            if (dis instanceof DiscountRevealedProduct) {
                if (((DiscountRevealedProduct) dis).getProductDiscount().getName().equals(productName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public DiscountBInterface searchIDInCompDiscount(DiscountBInterface discount, int discountId) {

        if (discount instanceof DiscountSimple) {
            if (((DiscountSimple) discount).getDiscountID() == discountId) {
                return discount;
            }
        } else {
            DiscountBInterface res1 = searchIDInCompDiscount(((DiscountPolicy) discount).getOperand1(), discountId);
            DiscountBInterface res2 = searchIDInCompDiscount(((DiscountPolicy) discount).getOperand2(), discountId);
            if (res1 != null) {
                return res1;
            }
            if (res2 != null) {
                return res2;
            }
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
        if (manage != null)
            appointer = manage.getAppointer();
        return appointer;

    }

    public void removeManager(User user) throws SQLException {
        //update DB
        PersistenceController.delete(managements.get(user));

        managements.remove(user);
        user.removeStoreManagement(this);
        NotificationSystem.getInstance().notify(user.getUsername(), "You have been no longer " + name + "'s manager");
    }

    public boolean hasProduct(String productName) {
        for (Product p : inventory.getProducts().keySet()) {
            if (p.getName().equals(productName))
                return true;
        }
        return false;
    }

    public Inventory getProductsInventory() {
        return this.inventory;
    }

    public void addToInventory(String productName, double productPrice, Category productCategory, String productDescription, int amount) throws SQLException {
        Product p = new Product(productName, productCategory, productDescription, productPrice, this.name, amount);
        inventory.getProducts().put(p, amount);

        // save to DB
        PersistenceController.create(p);
    }

    public void updateInventory(String productName, double productPrice, Category productCategory, String productDescription, int amount) throws SQLException {
        for (Product p : inventory.getProducts().keySet()) {
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

    public ConcurrentHashMap<User, StoreManaging> getManagements() {
        return managements;
    }

    public ConcurrentHashMap<User, StoreOwning> getOwnerships() {
        return ownerships;
    }

    public void setManagements(ConcurrentHashMap<User, StoreManaging> managements) {
        this.managements = managements;
    }

    public void addManager(User user, StoreManaging storeManaging, boolean notify) {
        if (!managements.containsKey(user)) {
            managements.put(user, storeManaging);
            if (notify)
                NotificationSystem.getInstance().notify(user.getUsername(), "You have been appointed as " + name + "'s store manager");
        }
    }

    // before activating this function make sure the new Owner is registered!!!
    // the function will return true if added successfully and false if the user is already an owner
    public String addStoreOwner(User newOwner, User appointer) throws SQLException {
        if (ownerships.size() == 1) {
            NotificationSystem.getInstance().notify(newOwner.getUsername(), "Your appointment as owner of" + name + "store, is waiting to be approved");
            StoreOwning storeOwning = new StoreOwning(appointer, name, newOwner.getUsername());
            ownerships.put(newOwner, storeOwning);
            newOwner.addOwnedStore(this, storeOwning);
            PersistenceController.create(storeOwning);
            return "the appointment of the new owner is done successfully";
        } else {
            if (!waitingAgreements.containsKey(newOwner))
                this.waitingAgreements.put(newOwner, new AppointmentAgreement(ownerships.keySet(), appointer));
            //notify all owners
            for (User u : ownerships.keySet()) {
                if (u.getUsername().equals(appointer.getUsername()))
                    continue;
                NotificationSystem.getInstance().notify(u.getUsername(), "the appointment of the user: " + newOwner.getUsername() + " as owner of: " + getName() + " is waiting to your response");
            }
            NotificationSystem.getInstance().notify(newOwner.getUsername(), "Your appointment as owner of" + name + "store, is waiting to be approved");
        }
        return "the appointment of the new owner is waiting for the owners response";

    }

    public void addStoreOwner(User u, StoreOwning so) {
        this.ownerships.put(u, so);
    }

    //UC 4.3
    public String approveAppointment(User waitingForApprove, User approveOwner) throws SQLException {
        AppointmentAgreement apag = waitingAgreements.get(waitingForApprove);
        apag.approve(approveOwner);
        if (apag.getWaitingForResponse().size() == 0) {
            if (apag.getDeclined().size() == 0) {
                StoreOwning storeOwning = new StoreOwning(apag.getTheAppointerUser(), name, "");//TODO: apointeeName?????
                ownerships.put(waitingForApprove, storeOwning);
                waitingAgreements.remove(waitingForApprove);
                waitingForApprove.addOwnedStore(this, storeOwning);
                PersistenceController.create(storeOwning);

                //notify that the appointment approved )
                for(User u: ownerships.keySet()) {
                    NotificationSystem.getInstance().notify(u.getUsername(), "the appointment of the user: " + waitingForApprove.getUsername() + " as owner of: " + getName() + " is approved");
                }
                return "your response was updated successfully - the new appointment approved";
            }
            else {
                waitingAgreements.remove(waitingForApprove);
                //notify that the appointment declined
                for(User u: ownerships.keySet()) {
                    NotificationSystem.getInstance().notify(u.getUsername(), "the appointment of the user: " + waitingForApprove.getUsername() + " as owner of: " + getName() + " is declined");
                }
                return "your response was updated successfully - the new appointment declined";
            }
        }
        return "your response was updated successfully";
    }

    //UC 4.3
    public String declinedAppointment(User waitingForApprove, User declinedOwner) {
        AppointmentAgreement apag = waitingAgreements.get(waitingForApprove);
        apag.decline(declinedOwner);
        if (apag.getWaitingForResponse().size() == 0) {
            waitingAgreements.remove(waitingForApprove);
            //notify that the appointment declined
            for(User u: ownerships.keySet()) {
                NotificationSystem.getInstance().notify(u.getUsername(), "the appointment of the user: " + waitingForApprove.getUsername() + " as owner of: " + getName() + " is declined");
            }
            return "your response was updated successfully - the new appointment declined";
        }
        return "your response was updated successfully";

    }


    //for each inventory in the basket - checks if the product meets the purchase policy requirements
    //if it does - reserve the product and adds it to the reserved inventory list
    public void reserveBasket(Basket b) throws SQLException {
        //this field save all the inventory that have been reserved
        for (PurchasePolicy p : purchasePolicies) {
            if (!p.purchaseAccordingToPolicy(b))
                throw new RuntimeException("Your purchase doesn’t match the store’s policy, details: "+ p.getPurchaseDescription());
        }
        Collection<ProductItem> products = b.getProductItems();
        for (ProductItem pi : products) {
            Product p = pi.getProduct();
            int amount = pi.getAmount();
            if (!this.inventory.reserveProduct(pi, b)) {
                throw new RuntimeException("There is currently no stock of " + amount + " " + p.getName() + " products");
            }
        }
    }

    public List<ProductItem> getReservedProducts(Basket b) {
        return this.inventory.getReservedProducts(b);
    }

    public void unreserveBasket(Basket b) throws SQLException {
        this.inventory.unreserveBasket(b);
    }

    public double calculateTotalCheck(Basket b) {
        double priceAfterDiscount = b.calcBasketPrice();
        double priceBeforDiscount = b.calcBasketPriceBeforeDiscount();
        double tempPrice = priceAfterDiscount;

        if (doubleDiscounts || (priceAfterDiscount == priceBeforDiscount)) {
            for (DiscountBInterface dis : discountsOnBasket) {
                if (dis.canGet(b)) {
                    double newPrice = ((DiscountSimple) dis).calc(b);
                    if (newPrice < tempPrice) {
                        tempPrice = newPrice;
                    }
                }
            }
        }
        b.setPrice(tempPrice);
        return tempPrice;
    }


    public void addStorePurchaseHistory(Basket b, User u) throws SQLException {
        Purchase p = new Purchase(b, u);
        p.getPurchasedProducts().computeCartPrice();
        this.purchaseHistory.addPurchase(p);

        // save to db
        PersistenceController.create(p);
        PersistenceController.update(p.getPurchasedProducts());
    }


    public StorePurchaseHistory getPurchaseHistory() {
        return this.purchaseHistory;
    }

    public String getDescription() {
        return this.description;
    }


    public void addDiscountCondProductAmount(String productName, int percentage, int limit) {
        Product p = this.getProductByName(productName);
        if (p != null)
            discountsOnProducts.add(new DiscountCondProductAmount(discountID_counter, limit, p, percentage));
        discountID_counter++;
    }

    public void addDiscountCondBasketProducts(String productDiscount, String productCond, int percentage, int limit) {
        Product pDiscount = this.getProductByName(productDiscount);
        Product pCond = this.getProductByName(productCond);
        if (pDiscount != null && pCond != null)
            discountsOnProducts.add(new DiscountCondBasketProducts(discountID_counter, pCond, pDiscount, limit, percentage));
        discountID_counter++;
    }

    public void addDiscountRevealedForProduct(String productName, int percentage) {
        Product p = this.getProductByName(productName);
        if (p != null)
            discountsOnProducts.add(new DiscountRevealedProduct(discountID_counter, p, percentage));
        discountID_counter++;
    }

    public void addDiscountForBasket(int percentage, int limit, boolean onprice) {
        discountsOnBasket.add(new DiscountBasketPriceOrAmount(discountID_counter, limit, percentage, onprice));
        discountID_counter++;

    }

    public void addDiscountPolicy(DiscountBInterface discountPolicy) {
        discountPolicies.add(discountPolicy);
    }

    public void addPurchasePolicy(PurchasePolicy purchasePolicy) {
        purchasePolicies.add(purchasePolicy);
    }

    public void addSimplePurchasePolicyStore(int limit, boolean minOrMax, boolean standAlone) {
        if (standAlone) {
            purchasePolicies.add(new PurchasePolicyStore(limit, minOrMax, purchaseID_counter));
            purchaseID_counter++;
        } else {
            notStandAlonePolicies.add(new PurchasePolicyStore(limit, minOrMax, purchaseID_counter));
            purchaseID_counter++;
        }
    }

    public void addSimplePurchasePolicyProduct(String productName, int limit, boolean minOrMax, boolean standAlone) {
        if (standAlone) {
            purchasePolicies.add(new PurchasePolicyProduct(productName, limit, minOrMax, purchaseID_counter));
            purchaseID_counter++;
        } else {
            notStandAlonePolicies.add(new PurchasePolicyProduct(productName, limit, minOrMax, purchaseID_counter));
            purchaseID_counter++;
        }
    }

    public String viewDiscount() {
        JSONArray discountsdes = new JSONArray();
        for (DiscountBInterface dis : discountsOnProducts) {
            JSONObject curr = new JSONObject();
            curr.put("discountId", ((DiscountSimple) dis).getDiscountID());
            curr.put("discountString", dis.discountDescription());
            discountsdes.add(curr);
        }

        for(DiscountBInterface dis :discountsOnBasket){
            JSONObject curr = new JSONObject();
            curr.put("discountId", ((DiscountSimple)dis).getDiscountID());
            curr.put("discountString", dis.discountDescription());
            discountsdes.add(curr);
        }
        for(DiscountBInterface dis :discountPolicies){
            JSONObject curr = new JSONObject();
            curr.put("discountPolicyString", dis.discountDescription());
            discountsdes.add(curr);
        }

        return discountsdes.toJSONString();
    }

    public String viewDiscountForChoose(){
        JSONArray discountsdes = new JSONArray();
        for(DiscountBInterface dis :discountsOnProducts){
            JSONObject curr = new JSONObject();
            curr.put("discountId", ((DiscountSimple)dis).getDiscountID());
            curr.put("discountString", dis.discountDescription());
            discountsdes.add(curr);
        }

        return discountsdes.toJSONString();
    }

    public String viewPurchasePolicies(){
        JSONArray purchaePolicy = new JSONArray();
        for(PurchasePolicy pp :purchasePolicies){
            JSONObject curr = new JSONObject();
            curr.put("purchaseString", pp.getPurchaseDescription());
            purchaePolicy.add(curr);
        }

        return purchaePolicy.toJSONString();
    }

    public String viewPurchasePoliciesForChoose(){
        JSONArray purchaePolicy = new JSONArray();
        for(PurchasePolicy pp :purchasePolicies) {
            if (pp instanceof PurchasePolicyStore) {
                JSONObject curr = new JSONObject();
                curr.put("policyId", ((PurchasePolicyStore) pp).getPurchaseId());
                curr.put("description", (pp.getPurchaseDescription()));
                purchaePolicy.add(curr);
            }
            else if(pp instanceof PurchasePolicyProduct){
                JSONObject curr = new JSONObject();
                curr.put("policyId", ((PurchasePolicyProduct) pp).getPurchaseId());
                curr.put("description", (pp.getPurchaseDescription()));
                purchaePolicy.add(curr);
            }
        }

        for(PurchasePolicy pp : notStandAlonePolicies) {
            if (pp instanceof PurchasePolicyStore) {
                JSONObject curr = new JSONObject();
                curr.put("policyId", ((PurchasePolicyStore) pp).getPurchaseId());
                curr.put("description", (pp.getPurchaseDescription()));
                purchaePolicy.add(curr);
            }
            else if(pp instanceof PurchasePolicyProduct){
                JSONObject curr = new JSONObject();
                curr.put("policyId", ((PurchasePolicyProduct) pp).getPurchaseId());
                curr.put("description", (pp.getPurchaseDescription()));
                purchaePolicy.add(curr);
            }
        }

        return purchaePolicy.toJSONString();
    }


    public void notifyOwners(Basket b, String userName) {
        String msg = userName + " bought some products from the store " + name + " you own: ";
        for (ProductItem pi : b.getProductItems()) {
            msg += pi.getProduct().getName() + ", ";
        }
        msg.substring(0, msg.length() - 2);
        msg += ".";
        for (User u : ownerships.keySet()) {
            NotificationSystem.getInstance().notify(u.getUsername(), msg);
        }
    }

    public JSONArray getAllProducts() {
        JSONArray products = new JSONArray();
        for (Product p : getInventory().keySet()) {
            JSONObject curr = new JSONObject();
            curr.put("name", p.getName());
            curr.put("price", p.getPrice());
            curr.put("store", this.name);
            curr.put("description", p.getDescription());
            products.add(curr);
        }

        return products;
    }

    public String getProductsJS() {
        JSONArray products = new JSONArray();
        for (Product p : getInventory().keySet()) {
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

    public String appointmentWaitingForUser(User owner){
        //List<String> output = new ArrayList<>();
        JSONArray usernames = new JSONArray();
        for(User user : waitingAgreements.keySet()){
            AppointmentAgreement aa = waitingAgreements.get(user);
            if(aa.getWaitingForResponse().contains(owner)){
                JSONObject curr = new JSONObject();
                curr.put("userName", user.getUsername());
                usernames.add(curr);
            }
        }
        return usernames.toJSONString();
    }

    public Product getProductByName(String name) {
        return this.inventory.getProductByName(name);
    }

    public boolean checkIfProductAvailable(String productName, int amount) {
        return this.inventory.checkIfProductAvailable(productName, amount);
    }

    public void removeDiscountPolicies() {
        this.discountPolicies = new ArrayList<>();
    }
    public void removePurchasePolicies(){
        this.purchasePolicies = new ArrayList<>();
    }

    //for store unit test
    public void reserveProduct(ProductItem pi, Basket b) throws SQLException {
        this.inventory.reserveProduct(pi, b);
    }

    public void initPurchaseHistory() throws SQLException {
        List<Purchase> purchases = PersistenceController.readPurchaseHistory(this.name);
        for(Purchase p: purchases){
            p.setCart(PersistenceController.readCartById(p.getCartId()));
            ShoppingCart sc = p.getPurchasedProducts();
            sc.setUser(SystemFacade.getInstance().getUserByName(sc.getUserName()));
            sc.initBaskets(sc);
            this.purchaseHistory.addPurchase(p);
        }

    }

    public User getOwnerAppointer(User user) {
        User appointer = null;
        StoreOwning manage = ownerships.get(user);
        if(manage != null)
            appointer = manage.getAppointer();
        return appointer;

    }

    public String removeOwner(User user){
        String output = "more appointments was deleted: ";
        for(User manUser : managements.keySet()){
            if(managements.get(manUser).getAppointer().equals(user)){
                //sand alert to the user being removed from managers list
                NotificationSystem.getInstance().notify(manUser.getUsername(), "your appointment as manager of: " + getName() + " is canceled");
                manUser.removeStoreManagement(this);
                managements.remove(manUser);
                output = output + manUser.getUsername()+"-manager ";
            }
        }
        for(User ownUser : ownerships.keySet()){
            if(ownerships.get(ownUser).getAppointer()!= null && ownerships.get(ownUser).getAppointer().equals(user)){
                //sand alert to the user being removed from owners list
                NotificationSystem.getInstance().notify(ownUser.getUsername(), "your appointment as owner of: " + getName() + " is canceled");
                ownUser.removeStoreOwning(this);
                ownerships.remove(ownUser);
                output = output + ownUser.getUsername()+"-owner ";

            }
        }
        NotificationSystem.getInstance().notify(user.getUsername(), "your appointment as owner of: " + getName() + " is canceled");
        ownerships.remove(user);
        user.removeStoreOwning(this);
        return output;
    }

}