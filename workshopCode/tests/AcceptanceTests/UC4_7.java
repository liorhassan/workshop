package AcceptanceTests;

import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import ServiceLayer.UsersHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC4_7 {
    private StoreManagerHandler handler;

    @Before
    public void setUp() throws Exception{
        handler = new StoreManagerHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        (new UsersHandler()).register("tester","tester");
        (new UsersHandler()).login("tester","tester", false);
        (new StoreHandler()).openNewStore("Castro", "clothing");
        (new UsersHandler()).logout();
        (new UsersHandler()).register("shauli","shauli");
        (new UsersHandler()).register("toya","toya");
        (new UsersHandler()).login("shauli","shauli", false);
        (new StoreHandler()).openNewStore("FoxHome", "stuff for home");
        (new StoreManagerHandler()).addStoreManager("toya","FoxHome");
    }

    @Test
    public void valid(){
        String result = handler.removeStoreManager("toya","FoxHome");
        assertEquals("Manager removed successfully!", result);
        (new StoreManagerHandler()).addStoreManager("toya","FoxHome");
    }

    @Test
    public void storeDoesNotExists(){
        String result = handler.removeStoreManager("toya","Fox");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void userDoesNotExists(){
        String result = handler.removeStoreManager("cooper","FoxHome");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void doesNotHavePrivileges(){
        String result = handler.removeStoreManager("toya", "Castro");
        assertEquals("You must be this store owner for this command", result);
    }

    @Test
    public void notAppointedByUser(){
        String result = handler.removeStoreManager("shauli","FoxHome");
        assertEquals("This username is not one of this store's managers appointed by you", result);
    }

    @Test
    public void emptyInput(){
        String result = handler.removeStoreManager("","FoxHome");
        assertEquals("Must enter username and store name", result);
        result = handler.removeStoreManager(null,"FoxHome");
        assertEquals("Must enter username and store name", result);
        result = handler.removeStoreManager("toya","");
        assertEquals("Must enter username and store name", result);
        result = handler.removeStoreManager("toya",null);
        assertEquals("Must enter username and store name", result);
    }

}
