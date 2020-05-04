package ExternalSystems;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.User;

import java.util.Collection;
import java.util.List;

public class ProductSupplyProxy implements ProductSupply{

    private ProductSupply productSupply;

    public ProductSupplyProxy(){
        this.productSupply = new ProductSupplyStub();
    }

    public boolean supply(Collection<Basket> products, User user){
        return this.productSupply.supply(products, user);
    }
}
