package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.Models.Product;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

import java.util.List;

public class SearchHandler {

    public String searchProduct(String name, String category, String[] keywords){
        SystemLogger.getInstance().writeEvent(String.format("Search product command: name - %s, category - %s, keywords - %s",argToString(name),argToString(category),argArrayToString(keywords)));
        try {
            String[] args = {name,category,argArrayToString(keywords)};
            if(SystemHandler.getInstance().allEmptyString(args))
                throw new IllegalArgumentException("Must enter search parameter");
            return SystemHandler.getInstance().searchProducts(name,category,keywords);
        } catch (RuntimeException e){
            SystemLogger.getInstance().writeError("Search product error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public String filterResults(Integer minPrice, Integer maxPrice, String category){
        SystemLogger.getInstance().writeError(String.format("Filter results command: minPrice - %d, maxPrice - %d, category - %s",minPrice,maxPrice,argToString(category)));
        try{
            return SystemHandler.getInstance().filterResults(minPrice,maxPrice,category);
        } catch(Exception e){
            SystemLogger.getInstance().writeError("Filter results error: " + e.getMessage());
            return e.getMessage();
        }
    }



    private String argArrayToString(String[] arg){
        return arg != null ? String.join(",",arg) : "";
    }
    private String argToString(String arg){
        return arg != null ? arg : "";
    }
}
