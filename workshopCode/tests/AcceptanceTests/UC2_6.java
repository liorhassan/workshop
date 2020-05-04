package AcceptanceTests;

import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC2_6 {

    private ShoppingCartHandler handler;

    @Before
    public void setUp() throws Exception{
        handler = new ShoppingCartHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        (new StoreHandler()).resetStores();
        (new UsersHandler()).register("shenhav","toya");
        (new UsersHandler()).login("shenhav","toya", false);
        (new StoreHandler()).openNewStore("FoxHome","stuff for home");
        (new StoreHandler()).UpdateInventory("FoxHome","pillow", 25, "BeautyProducts", "beauty pillow", 1);

    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid(){
        String result = handler.AddToShoppingBasket("FoxHome", "pillow", 1);
        assertEquals("Items have been added to basket", result);
    }

    @Test
    public void productIsNotAvailable(){
        String result = handler.AddToShoppingBasket("FoxHome", "banana", 1);
        assertEquals("The product isn't available in the store with the requested amount", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = handler.AddToShoppingBasket("Fox", "banana", 1);
        assertEquals("The store doesn't exist in the trading system", result);
    }

    @Test
    public void emptyInput(){
        String result = handler.AddToShoppingBasket("", "pillow", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = handler.AddToShoppingBasket(null, "pillow", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = handler.AddToShoppingBasket("FoxHome", "", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = handler.AddToShoppingBasket("FoxHome", null, 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
    }

}
