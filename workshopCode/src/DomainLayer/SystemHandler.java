package DomainLayer;


import DomainLayer.ExternalSystems.PaymentCollection;
import DomainLayer.ExternalSystems.ProductSupply;
import DomainLayer.Models.*;
import DomainLayer.Security.SecurityHandler;

import java.util.*;
import java.util.stream.Collectors;

public class SystemHandler {
    private static SystemHandler ourInstance = new SystemHandler();
    public static SystemHandler getInstance() {
        return ourInstance;
    }

    private User activeUser;
    private boolean adminMode;
    private HashMap<String, User> users;
    private HashMap<String, Store> stores;
    private List<User> adminsList;
    private List<Product> lastSearchResult;
    private PaymentCollection PC;
    private ProductSupply PS;

    private SystemHandler() {
        users = new HashMap<>();
        stores = new HashMap<>();
        adminsList = new ArrayList<>();
        activeUser = new User();  //guest
        lastSearchResult = new ArrayList<>();
        PC = new PaymentCollection();
        PS = new ProductSupply();
        User firstAdmin = new User();
        firstAdmin.setUsername("Admin159");
        SecurityHandler.getInstance().addUser("Admin159", "951");
        this.adminsList.add(firstAdmin);
        this.users.put("Admin159", firstAdmin);
    }

    public User getUserByName(String username) {
        return users.get(username);
    }

    public Store getStoreByName(String storeName) {
        return stores.get(storeName);
    }

    public User getActiveUser() {
        return activeUser;
    }

    public HashMap<String, Store> getStores() {
        return stores;
    }

    public void setStores(HashMap<String, Store> stores) {
        this.stores = stores;
    }

    public void setUsers(HashMap<String, User> newUsers) {
        users = newUsers;
    }

    //reset functions
    public void resetUsers(){
        users.clear();
        adminsList.clear();
        User firstAdmin = new User();
        firstAdmin.setUsername("Admin159");
        this.adminsList.add(firstAdmin);
        this.users.put("Admin159", firstAdmin);
    }

    public void resetStores(){
        stores.clear();
    }

    //function for handling UseCase 2.2
    public void register(String username) {

        User newUser = new User();
        newUser.setUsername(username);
        users.put(username, newUser);
    }

    //help function for register use case
    public boolean userExists(String username){
        return users.containsKey(username);
    }

    //function for handling UseCase 2.5
    public String searchProducts(String name, String category, String[] keywords){
        boolean searchName = name == null ? false : !name.equals("");
        boolean searchCategory = category == null ? false : !category.equals("");
        boolean searchKKeywords = keywords == null ? false : !String.join("",keywords).equals("");
        List<Product> matching=  new ArrayList<>();
        for(Store s : stores.values()){
            for(Product p : s.getProducts()){
                if(searchName&&!p.getName().contains(name))
                    continue;
                if(searchCategory&&!category.equals(p.getCategory().name()))
                    continue;
                if(searchKKeywords&&!p.getKeyWords().containsAll(Arrays.asList(keywords)))
                    continue;
                matching.add(p);
            }
        }
        if(matching.size()==0)
           return "There are no products that match these parameters";
        lastSearchResult = matching;
        return productListToString(lastSearchResult);
    }

    private String productListToString(List<Product> products){
        StringBuilder output = new StringBuilder();
        for(Product p : products){
            output.append("Name: ").append(p.getName()).append(", Category: ").append(p.getCategory().name()).append(", Description: ").append(p.getDescription()).append(", Price: ").append(p.getPrice()).append("\n");
        }
        return output.toString().strip();
    }

    //function for handling UseCase 2.5
    public String filterResults(Integer minPrice, Integer maxPrice, String category){
        boolean searchCategory = category == null ? false : !category.equals("");
        List<Product> matching = new ArrayList<>();
        for(Product p : lastSearchResult){
            if(searchCategory&&!category.equals(p.getCategory().name()))
                continue;
            if(minPrice!=null&&p.getPrice()<minPrice)
                continue;
            if(maxPrice!=null&&p.getPrice()>maxPrice)
                continue;
            matching.add(p);
        }
        if(matching.size()==0)
            return "There are no products that match this search filter";
        return productListToString(matching);
    }

    //function for handling UseCase 2.6
    public void addToShoppingBasket(String store, String product, int amount){
        activeUser.getShoppingCart().addProduct(product, stores.get(store), amount);
    }

    //helper function for UseCase 2.6
    public boolean storeExists(String storeName){
        return stores.containsKey(storeName);
    }

    //helper function for UseCase 2.6
    public boolean isProductAvailable(String store, String product, int amount){
        return stores.get(store).checkIfProductAvailable(product, amount);
    }

    // function for handling UseCase 2.3
    public void login(String username, boolean adminMode) {
        User user = users.get(username);
        if (!adminMode) {
            this.adminMode = false;
            activeUser = user;
        }
        else {
                this.adminMode = true;
                activeUser = user;
        }
    }

    //function for handling UseCase 3.1
    public String logout(){
        activeUser = new User();
        return "You have been successfully logged out!";
    }

    //function for handling Use Case 2.7
    public String viewSoppingCart(){

        return activeUser.getShoppingCart().view();
    }

    // function for use case 2.7
    public String editShoppingCart(String storeName, String productName, int amount){
        Store store = stores.get(storeName);
        return activeUser.getShoppingCart().edit(store, productName, amount);

    }

    //function for handling Use Case 4.1
    public String updateInventory(String storeName, String productName, double productPrice, String productCategory, String productDescription, int amount){
        Store s = stores.get(storeName);
        if (!s.hasProduct(productName)) {
            s.addToInventory(productName, productPrice, Category.valueOf(productCategory), productDescription, amount);
            return "The product has been added";
        }
        else {
            s.updateInventory(productName, productPrice, Category.valueOf(productCategory), productDescription, amount);
            return "The product has been updated";
        }
    }

    //helper function for Use Case 4.1
    public boolean userHasEditPrivileges(String storeName){
        return activeUser.hasEditPrivileges(storeName);
    }

    // function for handling Use Case 4.7
    public String removeManager(String username,String storename){
        Store store = stores.get(storename);
        User user = users.get(username);
        if(store != null && user != null) {
            store.removeManager(user);
            return "Manager removed successfully!";
        }
        return "Manager wasn't removed";
    }

    //helper function for Use Case 4.7
    public boolean isUserStoreOwner(String storename){
        Store store = stores.get(storename);
        if(store == null)
            return false;
        return store.isOwner(activeUser);
    }

    //helper function for Use Case 4.7
    public boolean isUserAppointer(String username, String storename){
        Store store = stores.get(storename);
        User user = users.get(username);
        if(store == null || user == null)
            return false;
        User appointer = store.getAppointer(user);
        if(appointer == null)
            return false;
        return appointer.equals(activeUser);
    }

    public String appointManager(String username, String storeName){
        Store store = stores.get(storeName);
        User appointed_user = users.get(username);

        // update store and user
        StoreManaging managing = new StoreManaging(activeUser);
        store.addManager(appointed_user, managing);
        appointed_user.addManagedStore(store, managing);

        return "Username has been added as one of the store managers successfully";
    }

    public boolean emptyString(String[] args){
        for (String s: args) {
            if (s == null || s.equals(""))
                return true;
        }
        return false;
    }

    public boolean allEmptyString(String[] args){
        for (String s: args) {
            if (s != null && !s.equals(""))
                return false;
        }
        return true;
    }

    // function for handling Use Case 3.2 written by Nufar
    public String openNewStore(String storeName, String storeDescription) {

        // update stores of the system and the user's data
        StoreOwning storeOwning = new StoreOwning();
        Store newStore = new Store(storeName, storeDescription, this.activeUser, storeOwning);

        this.activeUser.addOwnedStore(newStore, storeOwning);
        this.stores.put(storeName, newStore);

        return "The new store is now open!";
    }

    // function for handling Use Case 3.7 - written by Nufar
    public String getActiveUserPurchaseHistory() {
        return getUserPurchaseHistory(this.activeUser.getUsername());
    }

    // function for handling Use Case 3.7 + 6.4 - written by Nufar
    public String getUserPurchaseHistory(String userName) {
        UserPurchaseHistory purchaseHistory = this.users.get(userName).getPurchaseHistory();
        String historyOutput = "Shopping history:" + "\n";
        int counter = 1;
        for (Purchase p : purchaseHistory.getUserPurchases()) {
            historyOutput = historyOutput + "\n" + "Purchase #" + counter + ":" + "\n";
            historyOutput = historyOutput + p.getPurchasedProducts().viewOnlyProducts();
            historyOutput = historyOutput + "\n" + "total money paid: " + p.getTotalCheck();
            counter++;
        }
        return historyOutput;
    }

    // function for handling Use Case 4.10 and 6.4 - written by Nufar
    public String getStorePurchaseHistory(String storeName) {
        StorePurchaseHistory purchaseHistory = this.stores.get(storeName).getPurchaseHistory();
        String historyOutput = "Shopping history of the store:" + "\n";
        int counter = 1;
        for (Purchase p : purchaseHistory.getStorePurchases()) {
            historyOutput = historyOutput + "\n" + "Purchase #" + counter + ":" + "\n";
            historyOutput = historyOutput + p.getPurchasedProducts().viewOnlyProducts();
            historyOutput = historyOutput + "\n" + "total money paid: " + p.getTotalCheck();
            counter++;
        }
        return historyOutput;
    }

    // function for handling Use Case 4.6 - written by Noy
    public String editPermissions(String userName, List<String> permissions, String storeName){
        Store store = getStoreByName(storeName);
        User user = getUserByName(userName);

        List<Permission> p = new LinkedList<>();
        for(String per: permissions){
            p.add(new Permission(per));
        }

        store.getManagements().get(user).setPermission(p);
        return "Privileges have been edited successfully";
    }

    // function for handling Use Case 2.4 - written by Noy
    public String viewStoreInfo(String storeName){

        Store s = getStoreByName(storeName);
        Collection<Product> products = s.getProducts();
        String storeInfo = "Store name: " + s.getName() +
                " description: "  + s.getDescription() +
                "\n products:\n";

        for (Product currProduct : products) {
            storeInfo = storeInfo.concat("  " + currProduct.getName() + "- " + currProduct.getPrice() + "$\n");
        }
        return storeInfo;
    }

    // function for handling Use Case 2.4 - written by Noy
    public String viewProductInfo(String storeName, String productName){
        Store s = getStoreByName(storeName);
        Product p = s.getProductByName(productName);
        return (p.getName() + ": " + p.getDescription() + "\nprice: " + p.getPrice() + "$");
    }

    // function for handling Use Case 2.8 - written by Noy
    public void purchaseBaskets() {
        ShoppingCart sc = this.activeUser.getShoppingCart();
        Collection<Basket> baskets = sc.getBaskets();
        for (Basket currBasket : baskets) {
            Store currStore = currBasket.getStore();
            Collection<ProductItem> currProducts = currBasket.getProductItems();
            for (ProductItem pi : currProducts) {
                Product p = pi.getProduct();
                int amount = pi.getAmount();
                if (!currStore.checkIfProductAvailable(p.getName(), amount)) {
                        throw new RuntimeException("There is currently no stock of " + amount + " " + p.getName() + " products");
                }
                currStore.purchaseProduct(p, amount);
            }

            //add the store's basket to her purchase history
            ShoppingCart storeShoppingCart = new ShoppingCart(this.activeUser);
            storeShoppingCart.addBasket(currBasket);
            Purchase storePurchase = new Purchase(storeShoppingCart);
            currStore.getPurchaseHistory().addPurchase(storePurchase);
        }
    }

    // function for handling Use Case 2.8 - written by Noy
    public void addToPurchaseHistory() {
        ShoppingCart sc = this.activeUser.getShoppingCart();
        Purchase newPurchase = new Purchase(sc);
        this.activeUser.getPurchaseHistory().addPurchaseToHistory(newPurchase);
    }

    // function for handling Use Case 2.8 - written by Noy
    public boolean pay() {
        ShoppingCart sc = this.activeUser.getShoppingCart();
        Purchase newPurchase = new Purchase(sc);
        return PC.pay(newPurchase, this.activeUser);
    }

    // function for handling Use Case 2.8 - written by Noy
    public void supply(){
        ShoppingCart sc = this.activeUser.getShoppingCart();
        Purchase newPurchase = new Purchase(sc);
        PS.supply(newPurchase, this.activeUser);
        this.activeUser.emptyCart();
    }

    // function for handling Use Case 4.3 - written by Nufar
    public String appointOwner(String username, String storeName) {
        Store store = stores.get(storeName);
        User appointed_user = users.get(username);

        // update store and user
        StoreOwning owning = new StoreOwning(activeUser);
        store.addStoreOwner(appointed_user, owning);
        appointed_user.addOwnedStore(store, owning);

        return "Username has been added as one of the store owners successfully";
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public boolean checkIfActiveUserIsOwner(String storeName) {
        Store store = stores.get(storeName);
        return store.isOwner(activeUser);
    }

    public boolean checkIfUserIsOwner(String storName, String userName) {
        return stores.get(storName).isOwner(this.users.get(userName));
    }

    public boolean checkIfActiveUserIsManager(String storeName) {
        return stores.get(storeName).isManager(this.activeUser);
    }

    public boolean checkIfUserIsManager(String storName, String userName) {
        return stores.get(storName).isManager(this.users.get(userName));
    }

    public boolean checkIfProductInCart(String storName, String productName) {
        return activeUser.getShoppingCart().isProductInCart(productName, stores.get(storName));
    }

    public boolean checkIfActiveUserSubscribed() {
        return activeUser.getUsername() != null;
    }

    public boolean checkIfUserIsAdmin( String userName) {
        User user = getUserByName(userName);
        return adminsList.contains(user);
    }

    public boolean checkIfBasketExists(String storeName) {
        return activeUser.getShoppingCart().isBasketExists(getStoreByName(storeName));
    }

    public void addAdmin(String userName){
        User user = users.get(userName);
        adminsList.add(user);
    }

    public boolean checkIfInAdminMode() {
        return this.adminMode;
    }

    public void resetAdmins(){
        adminsList = new ArrayList<>();
    }


    // function fir handling Use Case 3b - written by Nufar
    public List<String> getProductsNamesAndKeywords() {
        List<String> res = new LinkedList<>();
        for (Store store: this.stores.values()) {
            res.addAll(store.getProducts().stream().map(p -> p.getName()).collect(Collectors.toList()));
            res.addAll(store.getProducts().stream().map(p -> p.getKeyWords()).
                    reduce(new LinkedList<String>(), (ans, i)-> {
                        ans.addAll(i);
                        return ans;
                    }));
        }
        return res;
    }


    public boolean cartIsEmpty(){
        return this.activeUser.getShoppingCart().isEmpty();
    }

    public boolean checkIfUserHavePermission(String storeName, String permission){
        if(!storeExists(storeName))
            return false;
        Store s = getStoreByName(storeName);
        return s.getManagements().get(this.activeUser).havePermission(permission);
    }

    public boolean checkIfProductExists(String storeName, String productName) {
        Store s = stores.get(storeName);
        if(s == null || s.getProductByName(productName) == null)
            return false;
        return true;
    }

    public void emptyCart(){
        this.activeUser.emptyCart();
    }

    public boolean isAdminMode() {
        return adminMode;
    }

}