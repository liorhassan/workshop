package AcceptanceTests;

import DomainLayer.Category;
import DomainLayer.Store;
import DomainLayer.SystemHandler;
import ServiceLayer.AddToShoppingBasket;
import ServiceLayer.Register;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        Store s = new Store("FoxHome", "stuff for home", SystemHandler.getInstance().getActiveUser());
        SystemHandler.getInstance().getStores().put("FoxHome", s);
        s.addToInventory("pillow", 25, null, "beauty pillow");
    }

    @Test
    public void valid(){
        String result = command.AddToShoppingBasket("FoxHome", "pillow");
        assertEquals("Item has been added to basket", result);
    }

    @Test
    public void productIsNotAvailable(){
        String result = command.AddToShoppingBasket("FoxHome", "banana");
        assertEquals("The product isn't available in the store", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = command.AddToShoppingBasket("Fox", "banana");
        assertEquals("The store doesn't exist in the trading system", result);
    }

    @Test
    public void emptyInput(){
        String result = command.AddToShoppingBasket("", "pillow");
        assertEquals("Must enter store name and product name", result);
        result = command.AddToShoppingBasket(null, "pillow");
        assertEquals("Must enter store name and product name", result);
        result = command.AddToShoppingBasket("FoxHome", "");
        assertEquals("Must enter store name and product name", result);
        result = command.AddToShoppingBasket("FoxHome", null);
        assertEquals("Must enter store name and product name", result);
    }

}
