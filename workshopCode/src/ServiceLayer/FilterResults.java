package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.Product;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.List;

public class FilterResults {
    public String execute(Integer minPrice, Integer maxPrice, Category category){
        SystemLogger.getInstance().writeError(String.format("Filter results command: minPrice - %d, maxPrice - %d, category - %s",minPrice,maxPrice,category.name()));
        try{
            return getMessage(SystemHandler.getInstance().filterResults(minPrice,maxPrice,category));
        } catch(Exception e){
            SystemLogger.getInstance().writeError("Filter results error: " + e.getMessage());
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
