package ServiceLayer;

import DomainLayer.Product;
import DomainLayer.Store;
import DomainLayer.SystemHandler;
import DomainLayer.UserPurchaseHistory;

import java.util.Collection;


public class ViewInfo {

    private SystemHandler sHandler;

    public String execute(String storeName) {

        this.sHandler = SystemHandler.getInstance();

        try {

            Store store = sHandler.viewStoreInfo(storeName);
            return getMessage(store);
        } catch (Exception e) {
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
