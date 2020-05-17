package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;
import org.json.simple.JSONObject;


public class ViewInfoHandler {

    public String viewStoreinfo(String storeName) {
        SystemLogger.getInstance().writeEvent("View Store Info command");
        try {
            String[] arg = {storeName};
            if(SystemFacade.getInstance().emptyString(arg)) {
                throw new IllegalArgumentException("The store name is invalid");
            }
            if(!SystemFacade.getInstance().storeExists(storeName)){
                throw new RuntimeException("This store doesn't exist in this trading system");
            }
            return SystemFacade.getInstance().viewStoreInfo(storeName);
            //return SystemFacade.getInstance().viewStoreInfo(storeName);

        } catch (Exception e) {
            SystemLogger.getInstance().writeError("View Store Info error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
            //return e.getMessage();
        }
    }

    public String viewProductInfo(String storeName, String productName) {
        SystemLogger.getInstance().writeEvent("View Product Info command");
        try {
            String[] args = {storeName, productName};
            if(SystemFacade.getInstance().emptyString(args)){
                throw new IllegalArgumentException("The product name is invalid");
            }
            if(!SystemFacade.getInstance().checkIfProductExists(storeName, productName)){
                throw new RuntimeException("This product is not available for purchasing in this store");
            }

            return SystemFacade.getInstance().viewProductInfo(storeName, productName);
        } catch (Exception e) {
            SystemLogger.getInstance().writeError("View Product Info error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
            //return createJSONMsg("ERROR", e.getMessage());
        }
    }

}
