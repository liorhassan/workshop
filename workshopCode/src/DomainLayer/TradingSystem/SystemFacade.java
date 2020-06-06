package DomainLayer.TradingSystem;


import ExternalSystems.PaymentCollectionStub;
import ExternalSystems.ProductSupplyStub;
import DomainLayer.TradingSystem.Models.*;
import DomainLayer.Security.SecurityFacade;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        NotificationSystem.getInstance().notify(username, "hey");
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
            try {
                JSONParser parser = new JSONParser();
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
    public void reserveProducts() {
        activeUser.getShoppingCart().reserveBaskets();
    }

    // function for handling Use Case 2.8 - written by Noy
    public void computePrice() {
        activeUser.getShoppingCart().computeCartPrice();
    }

    // function for handling Use Case 2.8 - written by Noy
    public boolean payment() {
        ShoppingCart sc = activeUser.getShoppingCart();
        if(!PC.pay(sc.getTotalCartPrice(), activeUser)){
            sc.unreserveProducts();
            return false;
        }
        return true;

    }

    // function for handling Use Case 2.8 - written by Noy
    public boolean supply(){
        ShoppingCart sc = activeUser.getShoppingCart();
        if(!PS.supply(sc.getBaskets(), activeUser)) {
            sc.unreserveProducts();
            return false;
        }
        return true;
    }

    // function for handling Use Case 2.8 - written by Noy
    public void addPurchaseToHistory() {
        ShoppingCart sc = activeUser.getShoppingCart();

        //handle User-Purchase-History
        Purchase newPurchase = new Purchase(sc);
        activeUser.addPurchaseToHistory(newPurchase);

        //handle Store-Purchase-History
        sc.addStoresPurchaseHistory();

        //notify all stores owners that products have been purchased in their store
        sc.notifyOwners();

        //finally - empty the shopping cart
        activeUser.emptyCart();

    }


    // function for handling Use Case 4.3 - written by Nufar
    public String appointOwner(String username, String storeName) {
        Store store = stores.get(storeName);
        User appointed_user = users.get(username);

        // update store and user
        store.addStoreOwner(appointed_user, activeUser);
        //StoreOwning owning = new StoreOwning(activeUser);
        //store.addStoreOwner(appointed_user, owning);
        //appointed_user.addOwnedStore(store, owning);


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
    public boolean productHasDiscount(String storeName, String productName){
        Store s = getStoreByName(storeName);
        return s.hasRevDiscountOnProduct(productName);

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


        else if(type.equals("PurchasePolicyProduct")){
            String productName = (policy.containsKey("productName")) ? ((String) policy.get("productName")) : null;
            if(checkIfProductExists(store.getName(), productName)){
                throw new IllegalArgumentException(" this product " + productName + "does not exist in store");
            }
            Product p = store.getProductByName(productName);
            Integer amount = (policy.containsKey("amount:")) ? ((int) policy.get("amount")) : null;
            if(amount == null)
                throw new IllegalArgumentException("invalid - no amount");
            Boolean minOrMax = (policy.containsKey("minOrMax:")) ? ((boolean) policy.get("minOrMax")) : null;
            if(minOrMax == null)
                throw new IllegalArgumentException("invalid - no minOrMax");

            //newPolicy = new PurchasePolicyProduct(productName, amount, minOrMax);
        }

        else if(type.equals("PurchasePolicyStore")){

            Integer limit = (policy.containsKey("limit:")) ? ((int) policy.get("limit")) : null;
            if(limit == null)
                throw new IllegalArgumentException("invalid - no limit");
            Boolean minOrMax = (policy.containsKey("minOrMax:")) ? ((boolean) policy.get("minOrMax")) : null;
            if(minOrMax == null)
                throw new IllegalArgumentException("invalid - no minOrMax");

            //newPolicy = new PurchasePolicyStore( limit, minOrMax);
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

    public String responseToAppointment(String storeName, String userToResponse, boolean isApproved){
        User waiting = users.get(userToResponse);
        Store store = getStoreByName(storeName);
        if(isApproved){
            store.approveAppointment(waiting, activeUser);
        }
        else{
            store.declinedAppointment(waiting, activeUser);
        }
        return "your response was updated successfully";
    }

    public String getCartTotalPrice(){
        return String.valueOf(this.activeUser.getShoppingCart().getTotalCartPrice());
    }

    public void removePolicies(String storeName){
        Store store = getStoreByName(storeName);
        store.removeDiscountPolicies();
    }
}