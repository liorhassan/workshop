package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;


public class StoreHandler {

    public String openNewStore(UUID session_id, String storeName, String storeDescription){
        SystemLogger.getInstance().writeEvent(String.format("Open new store command: store name - %s, store description - %s", storeName, storeDescription));
        String[] args = {storeName, storeDescription};

        try {
            if (!SystemFacade.getInstance().checkIfActiveUserSubscribed(session_id))
                throw new RuntimeException("Only subscribed users can open a new store");
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter store name and description");
            if (SystemFacade.getInstance().storeExists(storeName))
                throw new RuntimeException("Store name already exists, please choose a different one");
            return createJSONMsg("SUCCESS", SystemFacade.getInstance().openNewStore(session_id,storeName,storeDescription));
        }
        catch (RuntimeException e){
            SystemLogger.getInstance().writeError("Open new store error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return e.getMessage();
        }
    }

    public String addStoreOwner(UUID session_id, String username, String storeName) {
        SystemLogger.getInstance().writeEvent(String.format("Add store owner command: new owner username - %s, store name - %s",username,storeName));
        try {
            String[] args = {username, storeName};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username and store name");
            if (!SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if(!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(!SystemFacade.getInstance().checkIfActiveUserIsOwner(session_id, storeName))
                throw new RuntimeException("You must be this store owner for this action");
            if(SystemFacade.getInstance().checkIfUserIsOwner(storeName, username))
                throw new RuntimeException("This username is already one of the store's owners");
            return createJSONMsg("SUCCESS", SystemFacade.getInstance().appointOwner(session_id, username, storeName));
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Add store owner error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return e.getMessage();
        }
    }

    public String responseToAppointmentRequest(UUID SessionID,  String storeName, String username, boolean isApproved) {
        SystemLogger.getInstance().writeEvent(String.format("Response to store owner appointment command: new owner username - %s, store name - %s",username,storeName));
        try {
            String[] args = {username, storeName};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username and store name");
            if (!SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if(!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(!SystemFacade.getInstance().checkIfActiveUserIsOwner(SessionID, storeName))
                throw new RuntimeException("You must be this store owner for this action");
            if(SystemFacade.getInstance().checkIfUserIsOwner(storeName, username))
                throw new RuntimeException("This username is already one of the store's owners");
            return createJSONMsg("SUCCESS", SystemFacade.getInstance().responseToAppointment(SessionID, username, storeName, isApproved));
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Response to store owner appointment error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return e.getMessage();
        }
    }

    public String removeStoreOwner(UUID session_id, String username,String storename){
        //SystemLogger.getInstance().writeError(String.format("Remove manager command: username - %s, store name - %s",argToString(username),argToString(storename)));
        try{
            String[] args = {username,storename};
            if(SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Must enter username and store name");
            if(!SystemFacade.getInstance().storeExists(storename))
                throw new IllegalArgumentException("This store doesn't exist");
            if(!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username doesn't exist");
            if(!SystemFacade.getInstance().checkIfActiveUserIsOwner(session_id, storename))
                throw new RuntimeException("You must be this store owner for this command");
            if(!SystemFacade.getInstance().isOwnerAppointer(session_id,storename, username))
                throw new RuntimeException("This username is not one of this store's managers appointed by you");
            return SystemFacade.getInstance().removeStoreOwner(username,storename);
            //return SystemFacade.getInstance().removeManager(username,storename);
        } catch(Exception e) {
            SystemLogger.getInstance().writeError("Remove manager error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    public String UpdateInventory(UUID session_id, String storeName, String productName, Double productPrice, String productCategory, String productDes, Integer amount){
        SystemLogger.getInstance().writeEvent(String.format("Update inventory command: store name - %s, product name - %s, product price - %f, product category - %s, product description - %s, amount - %d", storeName, productName, productPrice, productCategory, productDes, amount));
        try {
            String[] args = {storeName, productName};
            if (SystemFacade.getInstance().emptyString(args) || amount <= 0)
                throw new IllegalArgumentException("Must enter store name, and product info");
            if (!SystemFacade.getInstance().storeExists(storeName))
                throw new IllegalArgumentException("This store doesn't exist");
            if (!SystemFacade.getInstance().userHasEditPrivileges(session_id, storeName))
                throw new IllegalArgumentException("Must have editing privileges");
            String[] args2 = {productDes};
            if(SystemFacade.getInstance().checkIfProductExists(storeName,productName)){
                if((amount == null && SystemFacade.getInstance().emptyString(args2)) || (amount != null && amount <0))
                    throw new IllegalArgumentException("Must enter amount bigger than 0 or product description");
            }
            else {
                if(amount == null || SystemFacade.getInstance().emptyString(args2) || amount < 0)
                    throw new IllegalArgumentException("Must enter amount bigger than 0 and product description");
            }
            return  createJSONMsg("SUCCESS", SystemFacade.getInstance().updateInventory(storeName, productName, productPrice, productCategory, productDes, amount));
            //return SystemFacade.getInstance().updateInventory(storeName, productName, productPrice, productCategory, productDes, amount);
        }
        catch (Exception e) {
            SystemLogger.getInstance().writeError("Update inventory error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    public String addDiscountCondProductAmount(String storeName, String productName, int percentage, int amount) {

        try {
            String[] args = {storeName, productName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if (percentage > 100 || percentage < 0) {
                throw new IllegalArgumentException("Invalid percentage value: must be between 0 and 100");
            }
            if (amount < 0) {
                throw new IllegalArgumentException("Invalid amount value: must be more then 0 ");
            }
            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }
            if (!SystemFacade.getInstance().checkIfProductExists(storeName, productName)) {
                throw new IllegalArgumentException("Cant add the discount on this product");
            }
            SystemFacade.getInstance().addDiscountCondProductAmount(storeName, productName, percentage, amount);
            return createJSONMsg("SUCCESS", "The discount has been added successfully");

        } catch (Exception e) {
            SystemLogger.getInstance().writeError("Add Discount For product: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String addDiscountRevealedProduct(String storeName, String productName, int percentage){

        try{
            String[] args = {storeName, productName};
            if(SystemFacade.getInstance().emptyString(args)){
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if(percentage > 100 || percentage < 0){
                throw new IllegalArgumentException("Invalid percentage value: must be between 0 and 100");
            }
            if(!SystemFacade.getInstance().storeExists(storeName)){
                throw new IllegalArgumentException("The store doesn't exist");
            }
            if(!SystemFacade.getInstance().checkIfProductExists(storeName, productName) && SystemFacade.getInstance().productHasDiscount(storeName, productName)){
                throw new IllegalArgumentException("Cant add the discount on this product");
            }
            SystemFacade.getInstance().addDiscountRevealedProduct(storeName, productName, percentage );
            return createJSONMsg("SUCCESS","The discount has been added successfully");

        }
        catch(Exception e){
            SystemLogger.getInstance().writeError("Add Discount For product: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String addDiscountCondBasketProducts(String storeName, String productDiscount, String condProduct, int percentage, int amount){

        try{
            String[] args = {storeName, productDiscount, condProduct};
            if(SystemFacade.getInstance().emptyString(args)){
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if(percentage > 100 || percentage < 0){
                throw new IllegalArgumentException("Invalid percentage value: must be between 0 and 100");
            }
            if(amount < 0){
                throw new IllegalArgumentException("Invalid amount value: must be more then 0 ");
            }
            if(!SystemFacade.getInstance().storeExists(storeName)){
                throw new IllegalArgumentException("The store doesn't exist");
            }
            if(!SystemFacade.getInstance().checkIfProductExists(storeName, productDiscount) || !SystemFacade.getInstance().checkIfProductExists(storeName, productDiscount)){
                throw new IllegalArgumentException("productCond or productDiscount does not exist in the store");
            }
            SystemFacade.getInstance().addDiscountCondBasketProducts(storeName, productDiscount, condProduct, percentage, amount);
            return createJSONMsg("SUCCESS","The discount has been added successfully");

        }
        catch(Exception e){
            SystemLogger.getInstance().writeError("Add Discount For product: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String addDiscountForBasketPriceOrAmount(String storeName,  int percentage, int amount, boolean onPrice) {
        try {
            String[] args = {storeName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if (percentage > 100 || percentage < 0) {
                throw new IllegalArgumentException("Invalid percentage value: must be between 0 and 100");
            }
            if (amount < 0) {
                throw new IllegalArgumentException("Invalid amount value: must be more then 0 ");
            }
            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }

            SystemFacade.getInstance().addDiscountOnBasket(storeName, percentage, amount, onPrice);
            return createJSONMsg("SUCCESS","The discount has been added successfully");

        } catch (Exception e) {
            SystemLogger.getInstance().writeError("Add Discount For Basket error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    public String addPurchasePolicyProduct(String storeName, String productName, int amount, boolean minOrMax, boolean standAlone) {

        try {
            String[] args = {storeName, productName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if (amount < 0 ) {
                throw new IllegalArgumentException("Invalid percentage value: must be more then 0 ");
            }

            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }
            if (!SystemFacade.getInstance().checkIfProductExists(storeName, productName)) {
                throw new IllegalArgumentException("Cant add the policy on this product");
            }
            SystemFacade.getInstance().addPurchasePolicyProduct(storeName, productName, amount, minOrMax, standAlone);
            return createJSONMsg("SUCCESS", "The purchase policy has been added successfully");

        } catch (Exception e) {
            SystemLogger.getInstance().writeError("Add purchase policy For product: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String addPurchasePolicyStore(String storeName, int amount, boolean minOrMax, boolean standAlone) {

        try {
            String[] args = {storeName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name ");
            }
            if (amount < 0 ) {
                throw new IllegalArgumentException("Invalid limit : must be more then 0");
            }

            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }

            SystemFacade.getInstance().addPurchasePolicyStore(storeName, amount, minOrMax, standAlone);
            return createJSONMsg("SUCCESS", "The purchase policy has been added successfully");

        } catch (Exception e) {
            SystemLogger.getInstance().writeError("Add purchase policy For product: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    public String viewAllDiscounts(String storeName){
        try {
            String[] args = {storeName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }
            return SystemFacade.getInstance().viewDiscounts(storeName);
        }
        catch(Exception e){
            SystemLogger.getInstance().writeError("View Discounts error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String viewDiscounts(String storeName){
        try {
            String[] args = {storeName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }
            return SystemFacade.getInstance().viewDiscountsForChoose(storeName);
        }
        catch(Exception e){
            SystemLogger.getInstance().writeError("View Discounts for choose error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String viewPurchasePolicies(String storeName){
        try {
            String[] args = {storeName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name and product name");
            }
            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }
            return SystemFacade.getInstance().viewPurchasePoliciesForChoose(storeName);
        }
        catch(Exception e){
            SystemLogger.getInstance().writeError("View purchase policy for choose error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String viewAllPurchasePolicies(String storeName){
        try {
            String[] args = {storeName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name ");
            }
            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }
            return SystemFacade.getInstance().viewPurchasePolicies(storeName);
        }
        catch(Exception e){
            SystemLogger.getInstance().writeError("View purchase policy error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String addDiscountPolicy(String discountPolicy){
        try{

            String result = SystemFacade.getInstance().addDiscountPolicy(discountPolicy);
            return createJSONMsg("SUCCESS", result);
        }
        catch (Exception e) {
            SystemLogger.getInstance().writeError("Add Discount Policy error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String addPurchasePolicy(String purchasePolicy){
        try{
            String result = SystemFacade.getInstance().addPurchasePolicy(purchasePolicy);
            return createJSONMsg("SUCCESS", result);
        }
        catch (Exception e) {
            SystemLogger.getInstance().writeError("Add Discount Policy error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getAllWaitingAppointments(UUID session_id, String storeName){
        try {
            String[] args = {storeName};
            if (SystemFacade.getInstance().emptyString(args)) {
                throw new IllegalArgumentException("Must enter store name ");
            }
            if (!SystemFacade.getInstance().storeExists(storeName)) {
                throw new IllegalArgumentException("The store doesn't exist");
            }
            return SystemFacade.getInstance().waitingAppointments(session_id, storeName);
        }
        catch(Exception e){
            SystemLogger.getInstance().writeError("get All Waiting Appointments error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    public String getStoreProducts(String storeName) {
        return SystemFacade.getInstance().getAllProducts(storeName);
    }

    public void resetStores(){
        SystemFacade.getInstance().resetStores();
    }
    public void removeDiscountPolicies(String storeName){
        SystemFacade.getInstance().removePolicies(storeName);
    }
    public void removePurchasePolicies(String storeName){
        SystemFacade.getInstance().removePurchasePolicies(storeName);
    }

    public String getMyStores(UUID session_id){
        return SystemFacade.getInstance().myStores(session_id);
    }

    public String checkAmountInInventory(String productName, String storeName) {
        return SystemFacade.getInstance().checkAmountInInventory(productName, storeName);
    }


    public String createJSONMsg(String type, String content) {
        JSONObject response = new JSONObject();
        response.put(type, content);
        return response.toJSONString();
    }

}
