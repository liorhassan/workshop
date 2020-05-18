package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

import java.util.ArrayList;
import java.util.List;

public class PolicyCondCategory implements DicountPolicy {
    private Category category;

    public PolicyCondCategory( Category category) {
        this.category = category;
    }

    @Override
    public List<DiscountBInterface> checkDiscounts(List<DiscountBInterface> discounts, Basket basket) {
        List<DiscountBInterface> output = new ArrayList<>();

        for (DiscountBInterface dis : discounts) {
            if (dis instanceof DiscountBaseProduct) {
                int amount = 0;
                for (ProductItem pi : basket.getProductItems()) {
                    if (pi.getProduct().equals(basket.getStore().getProductByName(((DiscountBaseProduct) dis).getProductName()))) {
                        amount = pi.getAmount();
                        break;
                    }
                }
                if(amount == 0){
                    continue;
                }
                if ((basket.getStore().getProductByName(((DiscountBaseProduct) dis).getProductName()).getCategory() == category && dis.canGet(amount))) {
                    output.add(dis);
                }
            }
        }
        return output;
    }

}
