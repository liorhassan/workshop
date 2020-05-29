package DomainLayer.TradingSystem.Models;

import DomainLayer.TradingSystem.DiscountBInterface;
import DomainLayer.TradingSystem.DiscountPolicyComp;
import DomainLayer.TradingSystem.DiscountPolicyIf;
import DomainLayer.TradingSystem.ProductItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Basket {

    private Store store;
    private List<ProductItem> productItems;
    private double price;
    List<DiscountBInterface> discountsOnProducts;

    public Basket(Store store) {
        this.store = store;
        productItems = new ArrayList<>();
        discountsOnProducts = new ArrayList<>();
        price = 0;
    }


    public List<DiscountBInterface> getDiscountsOnProducts() {
        return discountsOnProducts;
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

    public void deserveDiscounts(){
        for(DiscountBInterface dis : store.getDiscountsOnProducts()){
            if(dis.canGet(this)){
                discountsOnProducts.add(dis);
            }
        }
        for (DiscountBInterface policy : store.getDiscountPolicies()){
            if(policy instanceof DiscountPolicyIf){
                if(policy.canGet(this)){
                    if(!discountsOnProducts.contains(((DiscountPolicyIf) policy).getResult())){
                        this.discountsOnProducts.add(((DiscountPolicyIf) policy).getResult());
                    }
                }
            }
            else(policy instanceof DiscountPolicyComp){

            }
        }
    }





    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }

    public JSONArray viewBasket() {
//        if (productItems.isEmpty())                          // if no products return nothig (במיוחד בשביל הפונקציה של ההצגה של השופינגכארט)
//            return "";
//        String output = "Store name: " + this.store.getName() + "\n";
//        for (ProductItem pi : productItems) {
//            output = output + "Product name: " + pi.getProduct().getName() + " price: " + pi.getProduct().getPrice() + " amount: " + pi.getAmount() + "\n";
//        }
//        return output;
        JSONArray basketArray = new JSONArray();
        if(productItems.isEmpty())
            return basketArray;

        for (ProductItem pi : productItems) {
            JSONObject currProduct = new JSONObject();
            currProduct.put("name", pi.getProduct().getName());
            currProduct.put("price", pi.getProduct().getPrice());
            currProduct.put("store", this.store.getName());
            currProduct.put("amount", pi.getAmount());
            basketArray.add(currProduct);
        }
        return basketArray;
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
