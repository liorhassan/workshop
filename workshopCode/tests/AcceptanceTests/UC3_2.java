package AcceptanceTests;

import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import static org.junit.Assert.assertEquals;

public class UC3_2 {

    @BeforeClass
    public static void init() {
        UC2_3.setUp();
        (new UsersHandler()).login("toya", "1234", false);
    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).resetUsers();
    }

    @After
    public void tearDown() throws Exception {
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid() {
        String result = (new StoreHandler()).openNewStore("KKW store", "best Kim Kardashian beauty products");
        assertEquals("The new store is now open!", result);
    }

    @Test
    public void emptyInput(){
        String result = (new StoreHandler()).openNewStore("", "best Kim Kardashian beauty products");
        assertEquals("Must enter store name and description", result);
        result = (new StoreHandler()).openNewStore(null, "best Kim Kardashian beauty products");
        assertEquals("Must enter store name and description", result);
        result = (new StoreHandler()).openNewStore("KKW store", "");
        assertEquals("Must enter store name and description", result);
        result = (new StoreHandler()).openNewStore("KKW store", null);
        assertEquals("Must enter store name and description", result);
    }

    @Test
    public void storeAlreadyExist(){
        (new StoreHandler()).openNewStore("KKW store", "best Kim Kardashian beauty products");
        String result = (new StoreHandler()).openNewStore("KKW store", "best storeeeee");
        assertEquals("Store name already exists, please choose a different one", result);
    }
}
