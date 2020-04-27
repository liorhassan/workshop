package UnitTests;

import DomainLayer.Models.Product;
import DomainLayer.Models.Purchase;
import DomainLayer.Models.ShoppingCart;
import DomainLayer.Models.Store;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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
        Product p1 = new Product("Shirt",null,null,40.0);
        Product p2 = new Product("Dress",null,null,45.5);
        Store store = new Store("Fox",null,null,null);
        store.getInventory().put(p1,10);
        store.getInventory().put(p2,10);
        purchase.getPurchasedProducts().addProduct("Shirt",store,1);;
        assertEquals(40,purchase.getTotalCheck(),0.0000000001);
        purchase.getPurchasedProducts().addProduct("Dress",store,3);;
        assertEquals(40+3*45.5,purchase.getTotalCheck(),0.0000000001);
    }
}