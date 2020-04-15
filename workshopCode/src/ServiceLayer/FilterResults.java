package ServiceLayer;

import DomainLayer.Category;
import DomainLayer.Product;
import DomainLayer.SystemHandler;

import java.util.List;

public class FilterResults {
    public String execute(Integer minPrice, Integer maxPrice, Category category){
        try{
            return getMessage(SystemHandler.getInstance().filterResults(minPrice,maxPrice,category));
        } catch(Exception e){
            return e.getMessage();
        }
    }

    private String getMessage(List<Product> products){
        String output = "";
        for(Product p : products){
            output+="Name: "+p.getName()+", Category: "+p.getCategory().name()+", Description: "+p.getDescription()+", Price: "+p.getPrice()+"\n";
        }
        return output.strip();
    }
}
