package ServiceLayer;

import DomainLayer.Product;
import DomainLayer.Store;
import DomainLayer.SystemHandler;

import java.util.List;

public class ViewInfo {

    private SystemHandler sHandler;

    public void execute(String storeName) {

        this.sHandler = SystemHandler.getInstance();
        System.out.println(sHandler.viewStoreInfo(storeName));
    }

    public void viewProductInfo(String storeName, String productName){

        System.out.println(this.sHandler.viewProductInfo(storeName, productName));
    }
}
