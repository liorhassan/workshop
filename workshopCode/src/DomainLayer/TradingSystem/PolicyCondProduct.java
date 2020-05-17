package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.ArrayList;
import java.util.List;

public class PolicyCondProduct implements DicountPolicy {
    private boolean deserve;
    private String productName;

    public PolicyCondProduct(boolean deserve, String product) {
        this.deserve = deserve;
        this.productName = product;
    }


    @Override
    public List<DiscountBInterface> checkDiscounts(List<DiscountBInterface> discounts, Basket basket) {
        List<DiscountBInterface> output = new ArrayList<>();
        int amount = 0;
        for (ProductItem pi : basket.getProductItems()) {
            if (pi.getProduct().equals(basket.getStore().getProductByName(productName))) {
                amount = pi.getAmount();
                break;
            }
        }
        if(amount == 0){
            return output;
        }
        for (DiscountBInterface dis : discounts){
            if(dis instanceof DiscountBaseProduct){
                if(((DiscountBaseProduct) dis).getProductName().equals(productName) && dis.canGet(amount)){
                    output.add(dis);
                }
            }
        }
        return output;
    }
}
