package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.Models.Product;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.List;

public class SearchHandler {

    public String searchProduct(String name, Category category, String description){
        SystemLogger.getInstance().writeEvent(String.format("Search product command: name - %s, category - %s, description - %s",name,category.name(),description));
        try {
            return getMessageSearchProduct(SystemHandler.getInstance().searchProducts(name,category,description));
        } catch (RuntimeException e){
            SystemLogger.getInstance().writeError("Search product error: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String getMessageSearchProduct(List<Product> products){
        StringBuilder output = new StringBuilder();
        for(Product p : products){
            output.append("Name: ").append(p.getName()).append(", Category: ").append(p.getCategory().name()).append(", Description: ").append(p.getDescription()).append(", Price: ").append(p.getPrice()).append("\n");
        }
        return output.toString().strip();
    }

    public String filterResults(Integer minPrice, Integer maxPrice, Category category){
        SystemLogger.getInstance().writeError(String.format("Filter results command: minPrice - %d, maxPrice - %d, category - %s",minPrice,maxPrice,category.name()));
        try{
            return getMessageFilter(SystemHandler.getInstance().filterResults(minPrice,maxPrice,category));
        } catch(Exception e){
            SystemLogger.getInstance().writeError("Filter results error: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String getMessageFilter(List<Product> products){
        StringBuilder output = new StringBuilder();
        for(Product p : products){
            output.append("Name: ").append(p.getName()).append(", Category: ").append(p.getCategory().name()).append(", Description: ").append(p.getDescription()).append(", Price: ").append(p.getPrice()).append("\n");
        }
        return output.toString().strip();
    }
}
