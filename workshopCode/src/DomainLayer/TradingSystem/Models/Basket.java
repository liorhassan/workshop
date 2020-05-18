package DomainLayer.TradingSystem.Models;

import DomainLayer.TradingSystem.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class Basket {

    private Store store;
    private List<ProductItem> productItems;

    public Basket(Store store) {
        this.store = store;
        productItems = new ArrayList<>();
    }


    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public int getProductAmount(String productName){
        int amount = 0;
        for (ProductItem pi : getProductItems()) {
            if (pi.getProduct().equals(getStore().getProductByName(productName))) {
                amount = pi.getAmount();
                break;
            }
        }
        return amount;
    }

    public double calcPrice(){
        double total = 0;
        for (ProductItem pi: getProductItems()) {
            total += (pi.getAmount() * pi.getProduct().getPrice());
        }
        return total;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }

    public String viewBasket() {
        if (productItems.isEmpty())                          // if no products return nothig (במיוחד בשביל הפונקציה של ההצגה של השופינגכארט)
            return "";
        String output = "Store name: " + this.store.getName() + "\n";
        for (ProductItem pi : productItems) {
            output = output + "Product name: " + pi.getProduct().getName() + " price: " + pi.getProduct().getPrice() + " amount: " + pi.getAmount() + "\n";
        }
        return output;
    }


    public void addProduct(Product p, int amount) {
        for (ProductItem pi : productItems) {
            if (pi.getProduct().equals(p)) {
                pi.setAmount(pi.getAmount() + amount);
                return;
            }
        }
        productItems.add(new ProductItem(p, amount, this));
    }
}
