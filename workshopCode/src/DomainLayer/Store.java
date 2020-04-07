package DomainLayer;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private List<Product> products;
    private String name;

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
        //TODO: implement
        return  null;
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

}