package ExternalSystems;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

import java.util.Collection;
import java.util.Hashtable;

public class ProductSupplyProxy implements ProductSupply{

    private ProductSupply productSupply;

    public ProductSupplyProxy(){
        this.productSupply = new ProductSupplyStub();
    }

    public boolean supply(Collection<Basket> products, User user){
        return this.productSupply.supply(products, user);
    }

    public int supply(Hashtable<String, String> data){
        return this.productSupply.supply(data);
    }

    public int cancelSupplement(int transactionId) {
        return this.productSupply.cancelSupplement(transactionId);
    }


}
