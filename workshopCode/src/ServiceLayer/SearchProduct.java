package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.Product;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.List;

public class SearchProduct {

    public String execute(String name, Category category, String description){
        SystemLogger.getInstance().writeEvent(String.format("Search product command: name - %s, category - %s, description - %s",name,category.name(),description));
        try {
            return getMessage(SystemHandler.getInstance().searchProducts(name,category,description));
        } catch (RuntimeException e){
            SystemLogger.getInstance().writeError("Search product error: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String getMessage(List<Product> products){
        StringBuilder output = new StringBuilder();
        for(Product p : products){
            output.append("Name: ").append(p.getName()).append(", Category: ").append(p.getCategory().name()).append(", Description: ").append(p.getDescription()).append(", Price: ").append(p.getPrice()).append("\n");
        }
        return output.toString().strip();
    }
}
