package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.Product;
import DomainLayer.SystemHandler;

import java.util.List;

public class FilterResults {
    public String execute(List<Product> toFilter, Integer minPrice, Integer maxPrice, Category category){
        try{
            return getMessage(SystemHandler.getInstance().filterResults(toFilter,minPrice,maxPrice,category));
        } catch(Exception e){
            return e.getMessage();
        }
    }

    private String getMessage(List<Product> products){
        //TODO: implement
        return "";
    }
}
