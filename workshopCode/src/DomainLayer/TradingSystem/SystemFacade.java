package DomainLayer.TradingSystem;


import ExternalSystems.PaymentCollectionStub;
import ExternalSystems.ProductSupplyStub;
import DomainLayer.TradingSystem.Models.*;
import DomainLayer.Security.SecurityFacade;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SystemFacade {
    private static SystemFacade ourInstance = new SystemFacade();
    public static SystemFacade getInstance() {
        return ourInstance;
    }

    private ConcurrentHashMap<UUID,Session> active_sessions;
    private ConcurrentHashMap<String, User> users;
    private ConcurrentHashMap<String, Store> stores;
    private List<User> adminsList;
    private PaymentCollectionStub PC;
    private ProductSupplyStub PS;

    private SystemFacade() {
        active_sessions = new ConcurrentHashMap<>();
        users = new ConcurrentHashMap<>();
        stores = new ConcurrentHashMap<>();
        adminsList = new ArrayList<>();
        PC = new PaymentCollectionStub();
        PS = new ProductSupplyStub();
        initSystem();
    }

    private void initSystem(){
        User firstAdmin = new User();
        firstAdmin.setUsername("Admin159");
        SecurityFacade.getInstance().addUser("Admin159", "951");
        this.adminsList.add(firstAdmin);
        this.users.put("Admin159", firstAdmin);
    }

    public UUID createNewSession(){
        Session newSession = new Session();
        active_sessions.put(newSession.getSession_id(), newSession);
        return newSession.getSession_id();
    }

    public void closeSession(UUID session_id){
        if(!active_sessions.containsKey(session_id))
            throw new IllegalArgumentException("Invalid Session ID");
        active_sessions.remove(session_id);
    }

    public User getUserByName(String username) {
        return users.get(username);
    }

    public Store getStoreByName(String storeName) {
        return stores.get(storeName);
    }

    public ConcurrentHashMap<String, Store> getStores() {
        return stores;
    }

    public void setStores(ConcurrentHashMap<String, Store> stores) {
        this.stores = stores;
    }

    public void setUsers(ConcurrentHashMap<String, User> newUsers) {
        users = newUsers;
    }

    //reset functions
    public void resetUsers(){
        users.clear();
        adminsList.clear();
        initSystem();
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
    public String searchProducts(UUID session_id, String name, String category, String[] keywords){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");

        se.clearSearchResults();

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
                se.addToSearchResults(p);
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
    public String filterResults(UUID session_id, Integer minPrice, Integer maxPrice, String category){
        boolean searchCategory = category == null ? false : !category.equals("");
        //List<Product> matching = new ArrayList<>();
        JSONArray matching = new JSONArray();
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        for(Product p : se.getLastSearchResult()){
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
    public void addToShoppingBasket(UUID session_id, String store, String product, int amount){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        se.getLoggedin_user().getShoppingCart().addProduct(product, stores.get(store), amount);
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
    public void login(UUID session_id, String username, boolean adminMode) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        User user = users.get(username);
        se.setAdminMode(adminMode);
        se.setLoggedin_user(user);
        NotificationSystem.getInstance().notify(username, "hey");
    }

    //function for handling UseCase 3.1
    public String logout(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        NotificationSystem.getInstance().dettach(se.getLoggedin_user().getUsername());
        se.setLoggedin_user(new User());
        return "You have been successfully logged out!";
    }

    //function for handling Use Case 2.7
    public String viewSoppingCart(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return se.getLoggedin_user().getShoppingCart().view();
    }

    // function for use case 2.7
    public String editShoppingCart(UUID session_id, String storeName, String productName, int amount){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = stores.get(storeName);
        return se.getLoggedin_user().getShoppingCart().edit(store, productName, amount);

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
    public boolean userHasEditPrivileges(UUID session_id, String storeName){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return se.getLoggedin_user().hasEditPrivileges(storeName);
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
    public boolean isUserAppointer(UUID session_id, String username, String storename){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = stores.get(storename);
        User user = users.get(username);
        if(store == null || user == null)
            return false;
        User appointer = store.getAppointer(user);
        if(appointer == null)
            return false;
        return appointer.equals(se.getLoggedin_user());
    }

    public String appointManager(UUID session_id, String username, String storeName){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = stores.get(storeName);
        User appointed_user = users.get(username);

        // update store and user
        StoreManaging managing = new StoreManaging(se.getLoggedin_user());
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
    public String openNewStore(UUID session_id, String storeName, String storeDescription) {

        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");

        // update stores of the system and the user's data
        StoreOwning storeOwning = new StoreOwning();
        Store newStore = new Store(storeName, storeDescription, se.getLoggedin_user(), storeOwning);

        se.getLoggedin_user().addOwnedStore(newStore, storeOwning);
        this.stores.put(storeName, newStore);

        return "The new store is now open!";
    }

    // function for handling Use Case 3.7 - written by Nufar
    public String getActiveUserPurchaseHistory(UUID session_id) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return getUserPurchaseHistory(se.getLoggedin_user().getUsername());
    }

    // function for handling Use Case 3.7 + 6.4 - written by Nufar
    public String getUserPurchaseHistory(String userName) {
        JSONParser parser = new JSONParser();
        UserPurchaseHistory purchaseHistory = this.users.get(userName).getPurchaseHistory();
        JSONArray historyArray = new JSONArray();
        for(Purchase p: purchaseHistory.getUserPurchases()){
            try {
                JSONArray h = (JSONArray) parser.parse(p.getPurchasedProducts().viewOnlyProducts());
                historyArray.add(h);
            }catch(Exception e){System.out.println(e.getMessage());};
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
        JSONParser parser = new JSONParser();
        StorePurchaseHistory purchaseHistory = this.stores.get(storeName).getPurchaseHistory();
        JSONArray historyArray = new JSONArray();
        for (Purchase p : purchaseHistory.getStorePurchases()) {
            try {
                JSONArray currPurch = (JSONArray) parser.parse(p.getPurchasedProducts().viewOnlyProducts());
                historyArray.add(currPurch);
            }catch(Exception e){System.out.println(e.getMessage());};
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
    public void reserveProducts(UUID session_id) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        se.getLoggedin_user().getShoppingCart().reserveBaskets();
    }

    // function for handling Use Case 2.8 - written by Noy
    public void computePrice(UUID session_id) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        se.getLoggedin_user().getShoppingCart().computeCartPrice();
    }

    // function for handling Use Case 2.8 - written by Noy
    public boolean payment(UUID session_id) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        ShoppingCart sc = se.getLoggedin_user().getShoppingCart();
        if(!PC.pay(sc.getTotalCartPrice(), se.getLoggedin_user())){
            sc.unreserveProducts();
            return false;
        }
        return true;

    }

    // function for handling Use Case 2.8 - written by Noy
    public boolean supply(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        ShoppingCart sc = se.getLoggedin_user().getShoppingCart();
        if(!PS.supply(sc.getBaskets(), se.getLoggedin_user())) {
            sc.unreserveProducts();
            return false;
        }
        return true;
    }

    // function for handling Use Case 2.8 - written by Noy
    public void addPurchaseToHistory(UUID session_id) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        ShoppingCart sc = se.getLoggedin_user().getShoppingCart();

        //handle User-Purchase-History
        Purchase newPurchase = new Purchase(sc);
        se.getLoggedin_user().addPurchaseToHistory(newPurchase);

        //handle Store-Purchase-History
        sc.addStoresPurchaseHistory();

        //notify all stores owners that products have been purchased in their store
        sc.notifyOwners();

        //finally - empty the shopping cart
        se.getLoggedin_user().emptyCart();

    }


    // function for handling Use Case 4.3 - written by Nufar
    public String appointOwner(UUID session_id, String username, String storeName) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = stores.get(storeName);
        User appointed_user = users.get(username);

        // update store and user
        StoreOwning owning = new StoreOwning(se.getLoggedin_user());
        store.addStoreOwner(appointed_user, owning);
        appointed_user.addOwnedStore(store, owning);


        return "Username has been added as one of the store owners successfully";

    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    public boolean checkIfActiveUserIsOwner(UUID session_id, String storeName) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = stores.get(storeName);
        return store.isOwner(se.getLoggedin_user());
    }

    public boolean checkIfUserIsOwner(String storName, String userName) {
        return stores.get(storName).isOwner(this.users.get(userName));
    }

    public boolean checkIfActiveUserIsManager(UUID session_id, String storeName) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return stores.get(storeName).isManager(se.getLoggedin_user());
    }

    public boolean checkIfUserIsManager(String storName, String userName) {
        return stores.get(storName).isManager(this.users.get(userName));
    }

    public boolean checkIfProductInCart(UUID session_id, String storName, String productName) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return se.getLoggedin_user().getShoppingCart().isProductInCart(productName, stores.get(storName));
    }

    public boolean checkIfActiveUserSubscribed(UUID session_id) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return se.getLoggedin_user().getUsername() != null;
    }

    public boolean checkIfUserIsAdmin( String userName) {
        User user = getUserByName(userName);
        return adminsList.contains(user);
    }

    public boolean checkIfBasketExists(UUID session_id, String storeName) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return se.getLoggedin_user().getShoppingCart().isBasketExists(getStoreByName(storeName));
    }

    public void addAdmin(String userName){
        User user = users.get(userName);
        adminsList.add(user);
    }

    public boolean checkIfInAdminMode(UUID session_id) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return se.isAdminMode();
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


    public boolean cartIsEmpty(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return se.getLoggedin_user().getShoppingCart().isEmpty();
    }

    public boolean checkIfUserHavePermission(UUID session_id, String storeName, String permission){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        if(!storeExists(storeName))
            return false;
        Store s = getStoreByName(storeName);
        return s.getManagements().get(se.getLoggedin_user()).havePermission(permission);
    }

    public boolean checkIfProductExists(String storeName, String productName) {
        Store s = stores.get(storeName);
        if(s == null || s.getProductByName(productName) == null)
            return false;
        return true;
    }

    public void emptyCart(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        se.getLoggedin_user().emptyCart();
    }


    public void addDiscountOnProduct(String storeName, String productName, int percentage, int amount, boolean onAll){
        Store s = getStoreByName(storeName);
        s.addDiscountForProduct(productName, percentage, amount, onAll);
    }

    public void addDiscountOnBasket(String storeName, int percentage, int amount, boolean onAll){
        Store s = getStoreByName(storeName);
        s.addDiscountForBasket(percentage, amount, onAll);
    }
    public boolean productHasDiscount(String storeName, String productName){
        Store s = getStoreByName(storeName);
        Product p = s.getProductByName(productName);
        if(s.getDiscountsOnProducts().containsKey(p))
            return true;
        return false;
    }

    public String myStores(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        JSONArray response = new JSONArray();
        ConcurrentHashMap<Store, StoreOwning> ownings = se.getLoggedin_user().getStoreOwnings();
        ConcurrentHashMap<Store, StoreManaging> managements = se.getLoggedin_user().getStoreManagements();
        if(!ownings.isEmpty()){
            for(Store s: ownings.keySet()){
                JSONObject currStore = new JSONObject();
                currStore.put("name", s.getName());
                currStore.put("type", "Owner");
                JSONArray options = new JSONArray();
                for(Permission p: ownings.get(s).getPermission())
                    options.add(p.getAllowedAction());
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
                    options.add(p.getAllowedAction());
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

    public String getAllProducts(String store) {
        Store s = getStoreByName(store);
        if(s != null)
            return s.getProductsJS();
        return new JSONArray().toJSONString();
    }

    public String viewDiscounts(String storeName){
        Store store = getStoreByName(storeName);
        return store.viewDiscount();
    }

    public DiscountBInterface searchDiscountById(String storeName, int discountId){
        DiscountBInterface discount = getStoreByName(storeName).getDiscountById(discountId);
        return discount;
    }


    public DiscountPolicy buildDiscountPolicy(JSONObject policy, String storeName) {
            DiscountPolicy newPolicy = null;
            String type = (policy.containsKey("type")) ? ((String) policy.get("type")) : null;
            if(type.equals("If")){
                JSONObject condition = (policy.containsKey("condition")) ? ((JSONObject) policy.get("condition")) : null;
                int discountId = (policy.containsKey("discountId")) ? Integer.parseInt(policy.get("discountId").toString()) : null;
                DiscountBInterface discount = searchDiscountById(storeName, discountId);
                if(discount == null){
                    throw new IllegalArgumentException("invalid discountId");
                }
                newPolicy = new DiscountPolicyIf(discount, buildDiscountPolicy(condition, storeName));
            }
            else if(type.equals("Comp")){
                String operator = (policy.containsKey("operator")) ? ((String) policy.get("operator")) : null;
                JSONObject operand1 = (policy.containsKey("operand1")) ? ((JSONObject) policy.get("operand1")) : null;
                JSONObject operand2 = (policy.containsKey("operand2")) ? ((JSONObject) policy.get("operand2")) : null;
                String[] args = {operator};
                if(emptyString(args)){
                    throw new IllegalArgumentException("invalid operator");
                }
                PolicyOperator polOperator = parseOperator(operator);
                newPolicy = new DiscountPolicyComp(buildDiscountPolicy(operand1, storeName), buildDiscountPolicy(operand2, storeName), polOperator);
            }
            else if(type.equals("simpleCategory")){
                String category = (policy.containsKey("category")) ? ((String) policy.get("category")) : null;
                String[] args = {category};
                if(emptyString(args)){
                    throw new IllegalArgumentException("invalid category");
                }
                Category cat = Category.valueOf(category);
                newPolicy = new PolicyCondCategory(cat);
            }
            else if(type.equals("simpleProduct")){
                String product = (policy.containsKey("productName")) ? ((String) policy.get("productName")) : null;
                String[] args = {product};
                if(emptyString(args) || !checkIfProductExists(storeName, product)){
                    throw new IllegalArgumentException("invalid product name");
                }

                newPolicy = new PolicyCondProduct(product);
            }
            return newPolicy;

    }

    private PolicyOperator parseOperator(String operator) {
        if (operator.equals("OR")) {
            return PolicyOperator.OR;
        }
        if (operator.equals("AND")) {
            return PolicyOperator.AND;
        }
        if (operator.equals("XOR")) {
            return PolicyOperator.XOR;
        }
        return null;
    }


    public String addDiscountPolicy(String jsonString) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject requestJson = (JSONObject) parser.parse(jsonString);
            String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
            String[] args = {storeName};
            Store store = getStoreByName(storeName);
            if(emptyString(args) || getStoreByName(storeName) == null){
                throw new IllegalArgumentException("store doesnt exist");
            }
            DiscountPolicy discount = buildDiscountPolicy(requestJson, storeName);
            if(discount == null){
                throw new IllegalArgumentException("cant add the discount policy");
            }
            else{
                store.addDiscountPolicy(discount);
            }
            return "the discount policy added successfully";

            }
         catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String checkAmountInInventory(String productName, String storeName) {
        Store s = getStoreByName(storeName);
        if(s != null) {
            Product p = s.getProductByName(productName);
            if(p!= null) {
                Integer amount = s.getInventory().get(p);
                if(amount != null) {
                    return String.valueOf(amount);
                }
             }
        }
        return "";
    }

    public String getCartTotalPrice(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return String.valueOf(se.getLoggedin_user().getShoppingCart().getTotalCartPrice());
    }
}