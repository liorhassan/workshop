package DomainLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Store {

    private List<Product> products;
    private String name;



    private HashMap<Product, Integer> inventory;

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

    public HashMap<Product, Integer> getInventory() {
        return inventory;
    }

    // help for use case 2.7
    public Boolean checkProductInventory(Product p, int amount){
        if(! inventory.containsKey(p))
            return false;
        int available_amount =  inventory.get(p);
        if(amount> available_amount)
            return false;
         return true;

    }

    public void setInventory(HashMap<Product, Integer> inventory) {
        this.inventory = inventory;
    }

    public Product getProductByName(String productName){
        for (Product p : inventory.keySet()) {
            if (p.getName() == productName)
                return p;
        }
        return null;
    }

}