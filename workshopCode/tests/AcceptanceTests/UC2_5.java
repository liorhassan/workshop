package AcceptanceTests;

import ServiceLayer.SearchHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UC2_5 {
    private SearchHandler handler;
    @Before
    public void setUp() throws Exception {
        handler = new SearchHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        (new UsersHandler()).register("shauli","shauli");
        (new UsersHandler()).login("shauli","shauli", false);
        (new StoreHandler()).openNewStore("FoxHome", "stuff for home");
        (new StoreHandler()).UpdateInventory("FoxHome","banana", 7, "Food", "yellow with banana-like texture", 1);
        (new StoreHandler()).UpdateInventory("FoxHome","shirt", 40, "Clothing", "hawaiian shirt", 1);
        (new StoreHandler()).UpdateInventory("FoxHome","hat", 900, "Clothing", "beauty pillow", 1);
    }

    @Test
    public void valid() {
        String result = handler.searchProduct(null,"Clothing",null);
        assertTrue(result.equals("Name: shirt, Category: Clothing, Description: hawaiian shirt, Price: 40.0\nName: hat, Category: Clothing, Description: beauty pillow, Price: 900.0")||result.equals("Name: hat, Category: Clothing, Description: beauty pillow, Price: 900.0\nName: shirt, Category: Clothing, Description: hawaiian shirt, Price: 40.0"));
        result = handler.filterResults(0,50, null);
        assertEquals("Name: shirt, Category: Clothing, Description: hawaiian shirt, Price: 40.0", result);
    }

    @Test
    public void searchNoMatch() {
        String result = handler.searchProduct("hat","Food",null);
        assertEquals("There are no products that match these parameters", result);
    }

    @Test
    public void filterNoMatch() {
        handler.searchProduct(null,"Clothing",null);
        String result = handler.filterResults(0,10, null);
        assertEquals("There are no products that match this search filter", result);
    }

    @Test
    public void invalidInput() {
        String result = handler.searchProduct(null,null,null);
        assertEquals("Must enter search parameter", result);
        result = handler.searchProduct("",null,null);
        assertEquals("Must enter search parameter", result);
    }



}
