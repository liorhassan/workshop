package AcceptanceTests;

import DomainLayer.Category;
import DomainLayer.Store;
import DomainLayer.SystemHandler;
import DomainLayer.User;
import ServiceLayer.AddToShoppingBasket;
import ServiceLayer.UpdateInventory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC4_1 {

    private UpdateInventory command;

    @Before
    public void setUp() throws Exception{
        command = new UpdateInventory();
    }

    @BeforeClass
    public static void init() throws Exception{
        Store s = new Store("FoxHome", "stuff for home", SystemHandler.getInstance().getActiveUser());
        SystemHandler.getInstance().getStores().put("FoxHome", s);
        s.addToInventory("pillow", 25, Category.Clothing, "beauty pillow");
    }

    @Test
    public void valid(){
        String result = command.UpdateInventory("FoxHome", "pillow", 20, Category.Food, "beauty pillow", 1);
        assertEquals("The product has been updated", result);
    }

    @Test
    public void productDoesNotExist(){
        String result = command.UpdateInventory("FoxHome", "banana", 20, Category.Food, "yellow food", 1);
        assertEquals("The product has been added", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = command.UpdateInventory("Fox", "banana", 20, Category.Food, "yellow food", 1);
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void doesNotHavePrivileges(){
        Store s2 = new Store("Castro", "clothing", new User());
        SystemHandler.getInstance().getStores().put("Castro", s2);
        String result = command.UpdateInventory("Castro", "dress", 200, Category.Clothing, "evening dress", 1);
        assertEquals("Must have editing privileges", result);
    }

    @Test
    public void emptyInput(){
        String result = command.UpdateInventory("", "banana", 20, Category.Food, "yellow food", 1);
        assertEquals("Must enter store name and product info", result);
        result = command.UpdateInventory(null, "banana", 20, Category.Food, "yellow food", 1);
        assertEquals("Must enter store name and product info", result);
        result = command.UpdateInventory("FoxHome", "", 20, Category.Clothing, "yellow food", 1);
        assertEquals("Must enter store name and product info", result);
    }
}
