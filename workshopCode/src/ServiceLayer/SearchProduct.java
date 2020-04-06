package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.Product;
import DomainLayer.SystemHandler;

import java.util.List;

public class SearchProduct {

    public String execute(String name, Category category, String description){
        try {
            return getMessage(SystemHandler.getInstance().searchProducts(name,category,description));
        } catch (RuntimeException e){
            return e.getMessage();
        }
    }

    private String getMessage(List<Product> products){
        return "";
    }
}
