package AcceptanceTests;

import DomainLayer.Category;
import DomainLayer.Store;
import DomainLayer.StoreOwning;
import DomainLayer.SystemHandler;
import ServiceLayer.FilterResults;
import ServiceLayer.SearchProduct;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC2_5 {
    private SearchProduct searchCommand;
    private FilterResults filterCommand;
    @Before
    public void setUp() throws Exception {
        searchCommand = new SearchProduct();
        filterCommand = new FilterResults();
    }

    @BeforeClass
    public static void init() throws Exception{
        Store s = new Store("FoxHome", "stuff for home", SystemHandler.getInstance().getActiveUser(), new StoreOwning());
        SystemHandler.getInstance().getStores().put("FoxHome", s);
        s.addToInventory("banana", 7, Category.Food, "yellow with banana-like texture", 1);
        s.addToInventory("shirt", 40, Category.Clothing, "hawaiian shirt", 1);
        s.addToInventory("hat", 900, Category.Clothing, "beauty pillow", 1);

    }

    @Test
    public void valid() {
        String result = searchCommand.execute(null,Category.Clothing,null);
        assertEquals("Name: shirt, Category: Clothing, Description: hawaiian shirt, Price: 40.0\nName: hat, Category: Clothing, Description: beauty pillow, Price: 900.0", result);
        result = filterCommand.execute(0,50, null);
        assertEquals("Name: shirt, Category: Clothing, Description: hawaiian shirt, Price: 40.0", result);
    }

    @Test
    public void searchNoMatch() {
        String result = searchCommand.execute("hat",Category.Food,null);
        assertEquals("There are no products that match these parameters", result);
    }

    @Test
    public void filterNoMatch() {
        searchCommand.execute(null,Category.Clothing,null);
        String result = filterCommand.execute(0,10, null);
        assertEquals("There are no products that match this search filter", result);
    }

    @Test
    public void invalidInput() {
        String result = searchCommand.execute(null,null,null);
        assertEquals("Must enter search parameter", result);
        result = searchCommand.execute("",null,"");
        assertEquals("Must enter search parameter", result);
    }



}
