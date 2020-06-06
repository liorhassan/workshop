package UnitTests;

import DomainLayer.TradingSystem.Models.Basket;
import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.ShoppingCart;
import DomainLayer.TradingSystem.Models.Store;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ShoppingCartTest {

    ShoppingCart sc;

    @Before
    public void setUp() throws Exception {
        sc = new ShoppingCart(null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addProduct() {
        assertTrue(sc.getBaskets().isEmpty());
        Product p = new Product("Shirt",null,null,40.0, "Fox");
        Store store = new Store("Fox",null,null,null);
        store.getInventory().put(p,10);
        sc.addProduct("Shirt",store,1);
        ArrayList<Basket> baskets = new ArrayList<>(sc.getBaskets());
        assertTrue(baskets.size()==1&&baskets.get(0).getProductItems().size()==1&&baskets.get(0).getProductItems().get(0).getProduct()==p&&baskets.get(0).getProductItems().get(0).getAmount()==1);
        sc.addProduct("Shirt",store,2);
        baskets = new ArrayList<>(sc.getBaskets());
        assertTrue(baskets.size()==1&&baskets.get(0).getProductItems().size()==1&&baskets.get(0).getProductItems().get(0).getProduct()==p&&baskets.get(0).getProductItems().get(0).getAmount()==3);
    }

    @Test
    public void view() {
        assertEquals("Your ShoppingCart details: \nempty!",sc.view());
        Product p = new Product("Shirt",null,null,40.0,"Fox");
        Store store = new Store("Fox",null,null,null);
        store.getInventory().put(p,10);
        sc.addProduct("Shirt",store,1);
        assertEquals("Your ShoppingCart details: \n"+(new ArrayList<>(sc.getBaskets())).get(0).viewBasket(),sc.view());
    }

    @Test
    public void edit() {
        Product p = new Product("Shirt",null,null,40.0,"Fox");
        Store store = new Store("Fox",null,null,null);
        store.getInventory().put(p,10);
        sc.addProduct("Shirt",store,1);
        sc.edit(store,"Shirt",2);
        assertEquals(2,(new ArrayList<>(sc.getBaskets())).get(0).getProductItems().get(0).getAmount());
        sc.edit(store,"Dress",2);
        assertEquals(1,(new ArrayList<>(sc.getBaskets())).get(0).getProductItems().size());
        sc.edit(store,"Shirt",0);
        assertEquals(0,sc.getBaskets().size());
    }


}