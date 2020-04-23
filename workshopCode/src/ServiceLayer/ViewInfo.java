package ServiceLayer;

import DomainLayer.*;

import java.util.Collection;


public class ViewInfo {

    private SystemHandler sHandler;

    public String execute(String storeName) {
        SystemLogger.getInstance().writeEvent("View Info command: store name - %s, store description - %" +  storeName);
        this.sHandler = SystemHandler.getInstance();
        try {

            Store store = sHandler.viewStoreInfo(storeName);
            return getMessage(store);
        } catch (Exception e) {
            SystemLogger.getInstance().writeError("View Info error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String viewProductInfo(String storeName, String productName) {

        try {

            Product product = this.sHandler.viewProductInfo(storeName, productName);
            return getMessage(product);
        } catch (Exception e) {

            return e.getMessage();
        }
    }

    private String getMessage(Store store) {

        Collection<Product> products = store.getProducts();
        String storeInfo = "Store name: " + store.getName() +
                " description: "  + store.getDescription() +
                "\n products:\n";

        for (Product currProduct : products) {
            storeInfo = storeInfo.concat("  " + currProduct.getName() + "- " + currProduct.getPrice() + "$\n");
        }
        return storeInfo;
    }


    private String getMessage(Product product) {
        return (product.getName() + ": " + product.getDescription() + "\nprice: " + product.getPrice() + "$");
    }

}
