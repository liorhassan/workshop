package AcceptanceTests;

import DomainLayer.Category;
import DomainLayer.Store;
import DomainLayer.StoreOwning;
import DomainLayer.SystemHandler;
import ServiceLayer.AddToShoppingBasket;
import ServiceLayer.Register;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC2_6 {

    private AddToShoppingBasket command;

    @Before
    public void setUp() throws Exception{
        command = new AddToShoppingBasket();
    }

    @BeforeClass
    public static void init() throws Exception{
        SystemHandler.getInstance().register("shenhav");
        SystemHandler.getInstance().login("shenhav");
        Store s = new Store("FoxHome", "stuff for home", SystemHandler.getInstance().getActiveUser(), new StoreOwning());
        SystemHandler.getInstance().getStores().put("FoxHome", s);
        s.addToInventory("pillow", 25, null, "beauty pillow", 1);
    }

    @AfterClass
    public static void clean(){
        SystemHandler.getInstance().setUsers(new HashMap<>());
    }

    @Test
    public void valid(){
        String result = command.AddToShoppingBasket("FoxHome", "pillow", 1);
        assertEquals("Items have been added to basket", result);
    }

    @Test
    public void productIsNotAvailable(){
        String result = command.AddToShoppingBasket("FoxHome", "banana", 1);
        assertEquals("The product isn't available in the store with the requested amount", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = command.AddToShoppingBasket("Fox", "banana", 1);
        assertEquals("The store doesn't exist in the trading system", result);
    }

    @Test
    public void emptyInput(){
        String result = command.AddToShoppingBasket("", "pillow", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = command.AddToShoppingBasket(null, "pillow", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = command.AddToShoppingBasket("FoxHome", "", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
        result = command.AddToShoppingBasket("FoxHome", null, 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", result);
    }

}
