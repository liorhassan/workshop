package DomainLayer.TradingSystem;


import ExternalSystems.PaymentCollectionStub;
import ExternalSystems.ProductSupplyStub;
import DomainLayer.TradingSystem.Models.*;
import DomainLayer.Security.SecurityFacade;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class SystemFacade {
    private static SystemFacade ourInstance = new SystemFacade();
    public static SystemFacade getInstance() {
        return ourInstance;
    }

    private User activeUser;
    private boolean adminMode;
    private HashMap<String, User> users;
    private HashMap<String, Store> stores;
    private List<User> adminsList;
    private List<Product> lastSearchResult;
    private PaymentCollectionStub PC;
    private ProductSupplyStub PS;

    private SystemFacade() {
        users = new HashMap<>();
        stores = new HashMap<>();
        adminsList = new ArrayList<>();
        activeUser = new User();  //guest
        lastSearchResult = new ArrayList<>();
        PC = new PaymentCollectionStub();
        PS = new ProductSupplyStub();
        User firstAdmin = new User();
        firstAdmin.setUsername("Admin159");
        SecurityFacade.getInstance().addUser("Admin159", "951");
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
        lastSearchResult.clear();
        boolean searchName = name == null ? false : !name.equals("");
        boolean searchCategory = category == null ? false : !category.equals("");
        boolean searchKKeywords = keywords == null ? false : !String.join("",keywords).equals("");
        //List<Product> matching=  new ArrayList<>();
        JSONArray matching = new JSONArray();
        for(Store s : stores.values()){
            for(Product p : s.getProducts()){
                if(searchName&&!p.getName().contains(name))
                    continue;
                if(searchCategory&&!category.equals(p.getCategory().name()))
                    continue;
                if(searchKKeywords&&!p.getKeyWords().containsAll(Arrays.asList(keywords)))
                    continue;
                JSONObject curr = new JSONObject();
                curr.put("name", p.getName());
                curr.put("price", p.getPrice());
                curr.put("store", s.getName());
                curr.put("description", p.getDescription());
                lastSearchResult.add(p);
                matching.add(curr);
            }
        }
        if(matching.size()==0)
           throw new RuntimeException("There are no products that match these parameters");
//        lastSearchResult = matching;
        return matching.toJSONString();
        //return productListToString(lastSearchResult);
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
        //List<Product> matching = new ArrayList<>();
        JSONArray matching = new JSONArray();
        for(Product p : lastSearchResult){
            if(searchCategory&&!category.equals(p.getCategory().name()))
                continue;
            if(minPrice!=null&&p.getPrice()<minPrice)
                continue;
            if(maxPrice!=null&&p.getPrice()>maxPrice)
                continue;
            JSONObject curr = new JSONObject();
            curr.put("name", p.getName());
            curr.put("price", p.getPrice());
            //TODO: ADD STORE TO PRODUCT !!
            curr.put("store", "");
            curr.put("description", p.getDescription());
            matching.add(curr);
        }
        if(matching.size()==0)
            throw new RuntimeException("There are no products that match this search filter");
        return matching.toJSONString();
        //return productListToString(matching);
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
        NotificationSystem.getInstance().attach(username);
    }

    //function for handling UseCase 3.1
    public String logout(){
        activeUser = new User();
        NotificationSystem.getInstance().dettach(this.activeUser.getUsername());
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
        JSONObject response = new JSONObject();
        if(store != null && user != null) {
            store.removeManager(user);
            response.put("SUCCESS", "Manager removed successfully!");
            return response.toJSONString();
            //return "Manager removed successfully!";
        }
        response.put("ERROR", "Manager wasn't removed");
        return response.toJSONString();
        //return "Manager wasn't removed";
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
        JSONArray historyArray = new JSONArray();
        for(Purchase p: purchaseHistory.getUserPurchases()){
            historyArray.add(p.getPurchasedProducts().viewOnlyProducts());
        }
        return historyArray.toJSONString();
//        String historyOutput = "Shopping history:" + "\n";
//
//        int counter = 1;
//        for (Purchase p : purchaseHistory.getUserPurchases()) {
//            historyOutput = historyOutput + "\n" + "Purchase #" + counter + ":" + "\n";
//            historyOutput = historyOutput + p.getPurchasedProducts().viewOnlyProducts();
//            historyOutput = historyOutput + "\n" + "total money paid: " + p.getTotalCheck();
//            counter++;
//        }
//        return historyOutput;
    }

    // function for handling Use Case 4.10 and 6.4 - written by Nufar
    public String getStorePurchaseHistory(String storeName) {
        StorePurchaseHistory purchaseHistory = this.stores.get(storeName).getPurchaseHistory();
        JSONArray historyArray = new JSONArray();
        for (Purchase p : purchaseHistory.getStorePurchases()) {
            historyArray.add(p.getPurchasedProducts().viewOnlyProducts());
        }
        return historyArray.toJSONString();
//        String historyOutput = "Shopping history of the store:" + "\n";
//        int counter = 1;
//        for (Purchase p : purchaseHistory.getStorePurchases()) {
//            historyOutput = historyOutput + "\n" + "Purchase #" + counter + ":" + "\n";
//            historyOutput = historyOutput + p.getPurchasedProducts().viewOnlyProducts();
//            historyOutput = historyOutput + "\n" + "total money paid: " + p.getTotalCheck();
//            counter++;
//        }
//        return historyOutput;

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
        JSONObject response = new JSONObject();
        response.put("name", s.getName());
        response.put("description", s.getDescription());
        JSONArray productsArray = new JSONArray();
        for (Product currProduct : products) {
           JSONObject curr = new JSONObject();
           curr.put("productName", currProduct.getName());
           curr.put("price", currProduct.getPrice());
           productsArray.add(curr);
        }
        response.put("products", productsArray);
        return response.toJSONString();
//        String storeInfo = "Store name: " + s.getName() +
//                " description: "  + s.getDescription() +
//                "\n products:\n";
//
//        for (Product currProduct : products) {
//            storeInfo = storeInfo.concat("  " + currProduct.getName() + "- " + currProduct.getPrice() + "$\n");
//        }
//        return storeInfo;
    }

    // function for handling Use Case 2.4 - written by Noy
    public String viewProductInfo(String storeName, String productName){
        Store s = getStoreByName(storeName);
        Product p = s.getProductByName(productName);
        JSONObject response = new JSONObject();
        response.put("name", p.getName());
        response.put("description", p.getDescription());
        response.put("price", p.getPrice());
        return response.toJSONString();
//        return (p.getName() + ": " + p.getDescription() + "\nprice: " + p.getPrice() + "$");
    }

    // function for handling Use Case 2.8 - written by Noy
    public void reserveProducts() {
        this.activeUser.getShoppingCart().reserveBaskets();
    }

    // function for handling Use Case 2.8 - written by Noy
    public void computePrice() {
        this.activeUser.getShoppingCart().computeCartPrice();
    }

    // function for handling Use Case 2.8 - written by Noy
    public boolean payment() {
        if(!PC.pay(this.activeUser.getShoppingCart(), this.activeUser)){
            this.activeUser.getShoppingCart().unreserveProducts();
            return false;
        }
        return true;

    }

    // function for handling Use Case 2.8 - written by Noy
    public boolean supply(){
        if(!PS.supply(this.activeUser.getShoppingCart(), this.activeUser)) {
            this.activeUser.getShoppingCart().unreserveProducts();
            return false;
        }
        return true;
    }

    // function for handling Use Case 2.8 - written by Noy
    public void addPurchaseToHistory() {
        ShoppingCart sc = this.activeUser.getShoppingCart();
        //handle User-Purchase-History
        Purchase newPurchase = new Purchase(sc);
        this.activeUser.getPurchaseHistory().addPurchaseToHistory(newPurchase);

        //handle Store-Purchase-History
        sc.addStoresPurchaseHistory();

        //notify all stores owners that products have been purchased in their store
        sc.notifyOwners();

        //finally - empty the shopping cart
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

    public void addDiscount(String storeName, String productName, double percentage){
        Store s = getStoreByName(storeName);
        s.addDiscount(productName, percentage);
    }

    public String myStores(){
        JSONArray response = new JSONArray();
        HashMap<Store, StoreOwning> ownings = this.activeUser.getStoreOwnings();
        HashMap<Store, StoreManaging> managements = this.activeUser.getStoreManagements();
        if(!ownings.isEmpty()){
            for(Store s: ownings.keySet()){
                JSONObject currStore = new JSONObject();
                currStore.put("name", s.getName());
                currStore.put("type", "Owner");
                JSONArray options = new JSONArray();
                for(Permission p: ownings.get(s).getPermission())
                    options.add(p);
                currStore.put("options", options);
                response.add(currStore);
            }
        }

        if(!managements.isEmpty()){
            for(Store s: managements.keySet()){
                JSONObject currStore = new JSONObject();
                currStore.put("name", s.getName());
                currStore.put("type", "Manager");
                JSONArray options = new JSONArray();
                for(Permission p: managements.get(s).getPermission())
                    options.add(p);
                currStore.put("options", options);
                response.add(currStore);
            }
        }

        return response.toJSONString();
    }

    public String getAllProducts(){
        JSONArray response = new JSONArray();
        for(Store s: this.stores.values()) {
            JSONArray curr = s.getAllProducts();
            for(int i = 0; i < curr.size(); i++)
                response.add(curr.get(i));
        }

        return response.toJSONString();
    }

    public String getAllCategories(){
        Category[] categories = Category.values();
        JSONArray response = new JSONArray();
        for(int i = 0; i < categories.length; i++) {
            response.add(categories[i].name());
        }

        return response.toJSONString();
    }
}