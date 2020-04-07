package DomainLayer;

import java.util.List;

public class Store {

    private List<Product> products;

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