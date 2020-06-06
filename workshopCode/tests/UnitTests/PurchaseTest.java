package UnitTests;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Purchase;
import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.Store;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PurchaseTest {

    Purchase purchase;

    @Before
    public void setUp() throws Exception {
        purchase = new Purchase(new ShoppingCart(null));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getTotalCheck() {
        assertEquals(0,purchase.getTotalCheck(),0.0000000001);
        Product p1 = new Product("Shirt",null,null,40.0,"Fox");
        Product p2 = new Product("Dress",null,null,45.5,"Fox");
        Store store = new Store("Fox",null,null,null);
        store.getInventory().put(p1,10);
        store.getInventory().put(p2,10);
        purchase.getPurchasedProducts().addProduct("Shirt",store,1);;
        assertEquals(40,purchase.getTotalCheck(),0.0000000001);
        purchase.getPurchasedProducts().addProduct("Dress",store,3);;
        assertEquals(40+3*45.5,purchase.getTotalCheck(),0.0000000001);
    }
}