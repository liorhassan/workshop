package AcceptanceTests;

import DomainLayer.*;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC4_1 {

    private StoreHandler handler;

    @Before
    public void setUp() throws Exception{
        handler = new StoreHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        (new UsersHandler()).register("toya","shenhav");
        (new UsersHandler()).login("toya","shenhav", false);
        (new StoreHandler()).openNewStore("Castro","clothing");
        (new UsersHandler()).logout();
        (new UsersHandler()).register("shenhav","toya");
        (new UsersHandler()).login("shenhav","toya", false);
        (new StoreHandler()).openNewStore("FoxHome","stuff for home");
        (new StoreHandler()).UpdateInventory("FoxHome","pillow", 25, "BeautyProducts", "beauty pillow", 1);

    }

    @AfterClass
    public static void clean(){
        (new StoreHandler()).resetStores();
        (new UsersHandler()).resetUsers();
    }

    @Test
    public void valid(){
        String result = handler.UpdateInventory("FoxHome", "pillow", 20, "Food", "beauty pillow", 1);
        assertEquals("The product has been updated", result);
    }

    @Test
    public void productDoesNotExist(){
        String result = handler.UpdateInventory("FoxHome", "banana", 20, "Food", "yellow food", 1);
        assertEquals("The product has been added", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = handler.UpdateInventory("Fox", "banana", 20, "Food", "yellow food", 1);
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void doesNotHavePrivileges(){
        String result = handler.UpdateInventory("Castro", "dress", 200, "Clothing", "evening dress", 1);
        assertEquals("Must have editing privileges", result);
    }

    @Test
    public void emptyInput(){
        String result = handler.UpdateInventory("", "banana", 20, "Food", "yellow food", 1);
        assertEquals("Must enter store name, product info, and amount that is bigger than 0", result);
        result = handler.UpdateInventory(null, "banana", 20, "Food", "yellow food", 1);
        assertEquals("Must enter store name, product info, and amount that is bigger than 0", result);
        result = handler.UpdateInventory("FoxHome", "", 20, "Clothing", "yellow food", 1);
        assertEquals("Must enter store name, product info, and amount that is bigger than 0", result);
    }
}
