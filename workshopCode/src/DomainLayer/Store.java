package DomainLayer;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private List<Product> products;
    private String name;
    private String description;

    public Store(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Product> getProducts(){
        return this.products;
    }

    public boolean checkIfProductAvailable(String product){
        //TODO: implement
        return true;
    }

    public Product getProductByName(String productName){
        for (Product p : products) {
            if (p.getName() == productName)
                return p;
        }
        return null;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String newDesc){
        this.description = newDesc;
    }
}