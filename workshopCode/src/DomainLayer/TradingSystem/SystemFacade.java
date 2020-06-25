package DomainLayer.TradingSystem;


import DataAccessLayer.PersistenceController;
import DomainLayer.Security.SecurityFacade;
import DomainLayer.TradingSystem.Models.*;
import ExternalSystems.PaymentCollectionProxy;
import ExternalSystems.ProductSupplyProxy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.sql.SQLException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SystemFacade {
    private static SystemFacade ourInstance;

    static {
        try {
            ourInstance = new SystemFacade();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SystemFacade getInstance() {
        return ourInstance;
    }

    private ConcurrentHashMap<UUID,Session> active_sessions;
    private ConcurrentHashMap<String, User> users;
    private ConcurrentHashMap<String, Store> stores;
    private List<User> adminsList;
    private PaymentCollectionProxy PC;
    private ProductSupplyProxy PS;
    private AdministrativeStatistics as;

    private SystemFacade() throws SQLException {

        users = new ConcurrentHashMap<>();
        active_sessions = new ConcurrentHashMap<>();
        users = new ConcurrentHashMap<>();
        stores = new ConcurrentHashMap<>();
        adminsList = new ArrayList<>();
        PC = new PaymentCollectionProxy();
        PS = new ProductSupplyProxy();
        as = new AdministrativeStatistics();
        PersistenceController.create(as);
    }

    public void initSystem() throws SQLException {
        User firstAdmin = new User();
        firstAdmin.setUsername("Admin159");
        firstAdmin.setIsAdmin();
        SecurityFacade.getInstance().addUser("Admin159", "951");

        this.adminsList.add(firstAdmin);
        this.users.put("Admin159", firstAdmin);
        NotificationSystem.getInstance().addUser("Admin159");
    }

    private void handleSession(Session se) throws SQLException {
        Date now = new Date(new java.util.Date().getTime());
        if(!as.getDate().toString().equals(now.toString())){
            as = new AdministrativeStatistics();
            PersistenceController.create(as);
        }
        String msg = as.handleConnection(se);
        PersistenceController.update(as);
        for(Session s : active_sessions.values()){
            if(s == se) continue; // to not get notified of own connection
            if(s.isAdminMode())
                NotificationSystem.getInstance().notify(s.getLoggedin_user().getUsername(),msg);
        }
    }

    public UUID createNewSession() throws SQLException {
        Session newSession = new Session();
        active_sessions.put(newSession.getSession_id(), newSession);
        handleSession(newSession);
        return newSession.getSession_id();
    }

    public void closeSession(UUID session_id){
        if(!active_sessions.containsKey(session_id))
            throw new IllegalArgumentException("Invalid Session ID");
        String username = active_sessions.get(session_id).getLoggedin_user().getUsername();
        if(username != null)
            NotificationSystem.getInstance().logOutUser(username);
        active_sessions.remove(session_id);
    }

    public void init() throws SQLException {
        initSubscribedUsers();
        initAdmins();
        initStores();
        initCarts();
        initPurchaseHistory();
    }

    public String getLoggedInUsername(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return (se.getLoggedin_user().getUsername() == null) ? "Guest" : se.getLoggedin_user().getUsername();
    }



    private void initSubscribedUsers() throws SQLException {
        List<User> allSubscribedUsers = PersistenceController.readAllUsers(false);

        for (User user: allSubscribedUsers) {
            users.put(user.getUsername(), user);
            NotificationSystem.getInstance().addUser(user.getUsername());
            NotificationSystem.getInstance().logOutUser(user.getUsername());
        }
    }

    private void initAdmins() throws SQLException {
        List<User> allAdmins = PersistenceController.readAllUsers(true);

        for (User user: allAdmins) {
            adminsList.add(user);
        }
    }

    private void initCarts() throws SQLException {
        for(User u: this.users.values()){
            u.initCart();
        }
    }

    private void initStores() throws SQLException {
        List<Store> allStores = PersistenceController.readAllStores();

        for (Store s: allStores) {
            s.init();
            initManagments(s);
            initOwnerships(s);
            stores.put(s.getName(), s);
        }
    }

    private void initManagments(Store store) throws SQLException {
        List<StoreManaging> sm = PersistenceController.readAllManagers(store.getName());
        User currUser;
        for (StoreManaging s: sm){
            s.initPermissions();
            if(s.getAppoinerName() != null)
                s.setAppointer(getUserByName(s.getAppoinerName()));
            currUser = getUserByName(s.getAppointeeName());
            currUser.addManagedStore(store, s);
            users.replace(currUser.getUsername(), currUser);
            store.addManager(currUser, s, false);
        }
    }

    private void initOwnerships(Store store) throws SQLException {
        List<StoreOwning> so = PersistenceController.readAllOwners(store.getName());
        User currUser;
        for (StoreOwning s:so){
            s.initPermissions();
            if(s.getAppoinerName() != null)
                s.setAppointer(getUserByName(s.getAppoinerName()));
            currUser = getUserByName(s.getAppointeeName());
            currUser.addOwnedStore(store, s);
            users.replace(currUser.getUsername(), currUser);
            store.addStoreOwner(currUser, s);

        }
    }

    private void initPurchaseHistory() throws SQLException {

        for(User u : this.users.values()){
            u.initPurchaseHistory();
        }

        for(Store s : this.stores.values()){
            s.initPurchaseHistory();
        }
    }

    public String getAdminStats(Date from, Date to){
        JSONArray output = new JSONArray();
        List<AdministrativeStatistics> stats = PersistenceController.readAllAdminStats();
        for(AdministrativeStatistics stat : stats){
            if(stat.getDate().equals(from) || stat.getDate().equals(to) || (stat.getDate().after(from) && stat.getDate().before(to)))
                output.add(stat.getStatistics());
        }
        return output.toJSONString();
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
    public void resetUsers() throws SQLException {
//        for(User u : users.values()){
//            if(u.getUsername() == null)
//                continue;
//            PersistenceController.delete(PersistenceController.readUserDetails(u.getUsername()));
//        }
        users.clear();
        adminsList.clear();
        active_sessions = new ConcurrentHashMap<>();
        initSystem();
    }

    public void resetStores() throws SQLException {
        for(Store s : stores.values()){
            for(Product p : s.getInventory().keySet()){
                PersistenceController.delete(p);
            }
        }
        stores.clear();
    }

    //function for handling UseCase 2.2
    public void register(String username) throws SQLException {

        User newUser = new User();
        newUser.setUsername(username);
        users.put(username, newUser);
        NotificationSystem.getInstance().addUser(username);

        //save to DB
        PersistenceController.create(newUser);
        PersistenceController.create(newUser.getShoppingCart());
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
            curr.put("store", p.getStoreName());
            curr.put("description", p.getDescription());
            matching.add(curr);
        }
        if(matching.size()==0)
            throw new RuntimeException("There are no products that match this search filter");
        return matching.toJSONString();
        //return productListToString(matching);
    }

    //function for handling UseCase 2.6
    public void addToShoppingBasket(UUID session_id, String store, String product, int amount) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        se.getLoggedin_user().getShoppingCart().addProduct(product, stores.get(store), amount, se.getLoggedin_user().isRegistred());
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
    public void login(UUID session_id, String username, boolean adminMode) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        User user = users.get(username);
        se.setAdminMode(adminMode);
        se.setLoggedin_user(user);
        handleSession(se);
        NotificationSystem.getInstance().logInUser(username);
    }

    //function for handling UseCase 3.1
    public String logout(UUID session_id) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        if(se.getLoggedin_user().getUsername() != null) {
            NotificationSystem.getInstance().logOutUser(se.getLoggedin_user().getUsername());

            // save data to db
            PersistenceController.update(se.getLoggedin_user().getShoppingCart());
            PersistenceController.update(se.getLoggedin_user());
        }
        NotificationSystem.getInstance().logOutUser(se.getLoggedin_user().getUsername());
        se.setLoggedin_user(new User());
        se.setAdminMode(false);
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
    public String editShoppingCart(UUID session_id, String storeName, String productName, int amount) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = stores.get(storeName);
        return se.getLoggedin_user().getShoppingCart().edit(store, productName, amount, se.getLoggedin_user().isRegistred());

    }

    //function for handling Use Case 4.1
    public String updateInventory(String storeName, String productName, double productPrice, String productCategory, String productDescription, int amount) throws SQLException {
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
    public String removeManager(String username,String storename) throws SQLException {
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

    public String appointManager(UUID session_id, String username, String storeName) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = stores.get(storeName);
        User appointed_user = users.get(username);

        // update store and user
        StoreManaging managing = new StoreManaging(se.getLoggedin_user(), storeName, username);
        store.addManager(appointed_user, managing, true);
        appointed_user.addManagedStore(store, managing);

        // save to db
        PersistenceController.create(managing);

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
    public String openNewStore(UUID session_id, String storeName, String storeDescription) throws SQLException {

        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");

        // update stores of the system and the user's data
        StoreOwning storeOwning = new StoreOwning(storeName, se.getLoggedin_user().getUsername());
        Store newStore = new Store(storeName, storeDescription, se.getLoggedin_user(), storeOwning);

        se.getLoggedin_user().addOwnedStore(newStore, storeOwning);
        this.stores.put(storeName, newStore);

        //save store in DB
        PersistenceController.create(newStore);

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
    public String editPermissions(String userName, List<String> permissions, String storeName) throws SQLException {
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
    public void reserveProducts(UUID session_id) throws Exception {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        se.getLoggedin_user().getShoppingCart().reserveBaskets();
    }

    // function for handling Use Case 2.8 - written by Noy
    public double computePrice(UUID session_id) {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        se.getLoggedin_user().getShoppingCart().computeCartPrice();
        return se.getLoggedin_user().getShoppingCart().getTotalCartPrice();
    }

    // function for handling Use Case 2.8 - written by Noy
    public boolean payment(UUID session_id) throws SQLException {
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
    public boolean payment(Hashtable<String,String> paymentData, UUID session_id) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        ShoppingCart sc = se.getLoggedin_user().getShoppingCart();
        try {
            int transactionId = PC.pay(paymentData);
            if (transactionId == -1) {
                sc.unreserveProducts();
                return false;
            }
            sc.setPaymentTransactionId(transactionId);
            return true;
        }
        catch(Exception e){//connection to payment external system failed
            sc.unreserveProducts();
            throw e;
        }
    }



    // function for handling Use Case 2.8 - written by Noy
    public boolean supply(UUID session_id) throws SQLException {
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
    public boolean supply(Hashtable<String,String> supplyData, UUID session_id) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        ShoppingCart sc = se.getLoggedin_user().getShoppingCart();

        try{
            int transactionId = PS.supply(supplyData);
            if(transactionId == -1  ) {
                sc.unreserveProducts();
                if(PC.cancelPayment(sc.getPaymentTransactionId()) == -1){
                    throw new RuntimeException("supplement and payment cancellation failed, please check your credit card");
                }
                return false;
            }
            sc.setSupplementTransactionId(transactionId);
            return true;
        }
        catch(Exception e){//connection to supply external system failed
            sc.unreserveProducts();
            if(PC.cancelPayment(sc.getPaymentTransactionId()) == -1){
                throw new RuntimeException("supplement and payment cancellation failed, please check your credit card");
            }
            throw e;
        }


    }



    // function for handling Use Case 2.8 - written by Noy
    public void addPurchaseToHistory(UUID session_id) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        ShoppingCart sc = se.getLoggedin_user().getShoppingCart();

        sc.setIsHistory();

        //handle User-Purchase-History
        Purchase newPurchase = new Purchase(sc);
        se.getLoggedin_user().addPurchaseToHistory(newPurchase, se.getLoggedin_user().isRegistred());

        //handle Store-Purchase-History
        if (se.getLoggedin_user().isRegistred()) {
            sc.addStoresPurchaseHistory();
        }

        //notify all stores owners that products have been purchased in their store
        sc.notifyOwners();

        //finally - empty the shopping cart
        se.getLoggedin_user().emptyCart(se.getLoggedin_user().isRegistred());

        // update cart state
        if (se.getLoggedin_user().isRegistred()) {
            PersistenceController.update(sc);
        }
    }


    // function for handling Use Case 4.3 - written by Nufar
    public String appointOwner(UUID session_id, String username, String storeName) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = stores.get(storeName);
        User appointed_user = users.get(username);

        // update store and user
        //StoreOwning owning = new StoreOwning(se.getLoggedin_user(), storeName, username);
        return store.addStoreOwner(appointed_user, se.getLoggedin_user());
        //appointed_user.addOwnedStore(store, owning);

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

    public void addAdmin(String userName) throws SQLException {
        User user = users.get(userName);
        user.setIsAdmin();
        adminsList.add(user);
        PersistenceController.update(user);
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

    public void emptyCart(UUID session_id) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        se.getLoggedin_user().emptyCart(se.getLoggedin_user().isRegistred());
    }


    public void addDiscountCondProductAmount(String storeName, String productName, int percentage, int amount){
        Store s = getStoreByName(storeName);
        s.addDiscountCondProductAmount(productName, percentage, amount);
    }

    public void addDiscountCondBasketProducts(String storeName, String productDiscount, String productCond, int percentage, int amount){
        Store s = getStoreByName(storeName);
        s.addDiscountCondBasketProducts(productDiscount, productCond, percentage, amount);
    }
    public void addDiscountRevealedProduct(String storeName, String productName, int percentage){
        Store s = getStoreByName(storeName);
        s.addDiscountRevealedForProduct(productName, percentage);
    }

    public void addDiscountOnBasket(String storeName, int percentage, int amount, boolean onAll){
        Store s = getStoreByName(storeName);
        s.addDiscountForBasket(percentage, amount, onAll);
    }

    public void addPurchasePolicyProduct(String storeName, String productName, int amount, boolean minOrMax, boolean standAlone){
        Store s = getStoreByName(storeName);
        s.addSimplePurchasePolicyProduct(productName, amount, minOrMax, standAlone);
    }

    public void addPurchasePolicyStore(String storeName, int amount, boolean minOrMax, boolean standAlone){
        Store s = getStoreByName(storeName);
        s.addSimplePurchasePolicyStore( amount, minOrMax, standAlone);
    }

    public boolean productHasDiscount(String storeName, String productName){
        Store s = getStoreByName(storeName);
        return s.hasRevDiscountOnProduct(productName);

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

    public String viewDiscountsForChoose(String storeName){
        Store store = getStoreByName(storeName);
        return store.viewDiscountForChoose();
    }

    public String viewPurchasePolicies(String storeName){
        Store store = getStoreByName(storeName);
        return store.viewPurchasePolicies();
    }

    public String viewPurchasePoliciesForChoose(String storeName){
        Store store = getStoreByName(storeName);
        return store.viewPurchasePoliciesForChoose();
    }

    public DiscountBInterface searchDiscountById(String storeName, int discountId){
        DiscountBInterface discount = getStoreByName(storeName).getDiscountById(discountId);
        return discount;
    }


    public DiscountBInterface buildDiscountPolicy(JSONObject policy, String storeName) {
        DiscountBInterface newPolicy = null;
        String type = (policy.containsKey("type")) ? ((String) policy.get("type")) : null;
        if(type.equals("compose")){
            String operator = (policy.containsKey("operator")) ? ((String) policy.get("operator")) : null;
            if(operator.equals("IF_THEN")){
                JSONObject operand1 = (policy.containsKey("operand1")) ? ((JSONObject) policy.get("operand1")) : null;
                JSONObject operand2 = (policy.containsKey("operand2")) ? ((JSONObject) policy.get("operand2")) : null;

                newPolicy = new DiscountPolicyIf(buildDiscountPolicy(operand1,storeName), buildDiscountPolicy(operand2, storeName));
            }
            if(operator.equals("XOR")){
                JSONObject operand1 = (policy.containsKey("operand1")) ? ((JSONObject) policy.get("operand1")) : null;
                JSONObject operand2 = (policy.containsKey("operand2")) ? ((JSONObject) policy.get("operand2")) : null;

                newPolicy = new DiscountPolicyXor(buildDiscountPolicy(operand1,storeName), buildDiscountPolicy(operand2, storeName));
            }
            if(operator.equals("AND")){
                JSONObject operand1 = (policy.containsKey("operand1")) ? ((JSONObject) policy.get("operand1")) : null;
                JSONObject operand2 = (policy.containsKey("operand2")) ? ((JSONObject) policy.get("operand2")) : null;

                newPolicy = new DiscountPolicyAnd(buildDiscountPolicy(operand1,storeName), buildDiscountPolicy(operand2, storeName));
            }
        }

        else if(type.equals("simple")){
            int discountId = (policy.containsKey("discountId")) ? Integer.parseInt( policy.get("discountId").toString()) : -1;
            if(discountId == -1)
                throw new IllegalArgumentException("invalid discountId");
            Store store = getStoreByName(storeName);
            DiscountBInterface discount= store.getDiscountById(discountId);
            newPolicy = discount;
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
            DiscountBInterface discount = buildDiscountPolicy(requestJson, storeName);
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


    public PurchasePolicy buildPurchasePolicy(JSONObject policy, Store store) {
        PurchasePolicy newPolicy = null;
        String type = (policy.containsKey("type")) ? ((String) policy.get("type")) : null;
        if(type.equals("compose")) {
            String operatorStr = (policy.containsKey("operator")) ? ((String) policy.get("operator")) : null;
            PolicyOperator operator = parseOperator(operatorStr);
            if (operator != null) {
                JSONObject operand1 = (policy.containsKey("operand1")) ? ((JSONObject) policy.get("operand1")) : null;
                JSONObject operand2 = (policy.containsKey("operand2")) ? ((JSONObject) policy.get("operand2")) : null;
                newPolicy = new PurchasePolicyComp(buildPurchasePolicy(operand1, store), buildPurchasePolicy(operand2, store), operator);
            } else
                throw new IllegalArgumentException("store doesnt exist");
        }


        else if(type.equals("simple")){

                int purchaseId = (policy.containsKey("policyId")) ? Integer.parseInt( policy.get("policyId").toString()) : -1;
                if(purchaseId == -1)
                    throw new IllegalArgumentException("invalid purchaseId");
                PurchasePolicy pp= store.getPurchasePolicyById(purchaseId);
                newPolicy = pp;
            }

        return newPolicy;

    }

    public String addPurchasePolicy(String jsonString) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject requestJson = (JSONObject) parser.parse(jsonString);
            String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
            String[] args = {storeName};
            Store store = getStoreByName(storeName);
            if(emptyString(args) || getStoreByName(storeName) == null){
                throw new IllegalArgumentException("store doesnt exist");
            }
            PurchasePolicy  purchasePolicy = buildPurchasePolicy(requestJson, store);
            if(purchasePolicy == null){
                throw new IllegalArgumentException("cant add the purchase policy");
            }
            else{
                store.addPurchasePolicy(purchasePolicy);
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

    public String responseToAppointment(UUID session_id, String storeName, String userToResponse, boolean isApproved) throws SQLException {
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        User waiting = users.get(userToResponse);
        Store store = getStoreByName(storeName);
        if(isApproved){
            return store.approveAppointment(waiting, se.getLoggedin_user());
        }
        else{
            return store.declinedAppointment(waiting, se.getLoggedin_user());
        }

    }

    public String getCartTotalPrice(UUID session_id){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        return String.valueOf(se.getLoggedin_user().getShoppingCart().getTotalCartPrice());
    }

    public void removePolicies(String storeName){
        Store store = getStoreByName(storeName);
        store.removeDiscountPolicies();
    }


    public String removeStoreOwner(String userName, String storeName){
        Store store = getStoreByName(storeName);
        User userToRemove = users.get(userName);
        String result = store.removeOwner(userToRemove);
        return "owner been removed successfully, " +result;
    }

    public boolean isOwnerAppointer(UUID session_id, String storeName, String userName){
        Store store = getStoreByName(storeName);
        User user = users.get(userName);
        User appointer = store.getOwnerAppointer(user);
        if((appointer != null) && active_sessions.get(session_id).getLoggedin_user().equals(appointer)){
            return true;
        }
        return false;
    }

    public String waitingAppointments(UUID session_id, String storeName){
        Session se = active_sessions.get(session_id);
        if(se == null)
            throw new IllegalArgumentException("Invalid Session ID");
        Store store = getStoreByName(storeName);
        String userNames = store.appointmentWaitingForUser(se.getLoggedin_user());
        return userNames;
    }

    public void removePurchasePolicies(String storeName){
        Store store = getStoreByName(storeName);
        store.removePurchasePolicies();
    }

    public boolean haveAdmin() {
        return (this.adminsList.size() > 0);
    }

    public void removeSession(UUID session_id) {
        this.active_sessions.remove(session_id);
    }
}