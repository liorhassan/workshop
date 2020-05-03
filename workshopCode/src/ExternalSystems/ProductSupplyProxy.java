package ExternalSystems;

import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

public class ProductSupplyProxy implements ProductSupply{

    private ProductSupply productSupply;

    public ProductSupplyProxy(){
        this.productSupply = new ProductSupplyStub();
    }

    public boolean supply(ShoppingCart sc, User user){
        return this.productSupply.supply(sc, user);
    }
}
