package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SearchHandler;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.*;

public class UC2_5 {
    private SearchHandler handler;
    private static UUID session_id;
    @Before
    public void setUp() throws Exception {
        handler = new SearchHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        PersistenceController.initiate(false);
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("shauli","shauli");
        (new UsersHandler()).login(session_id, "shauli","shauli", false);
        (new StoreHandler()).openNewStore(session_id, "FoxHome", "stuff for home");
        (new StoreHandler()).UpdateInventory(session_id, "FoxHome","banana", 7.0, "Food", "yellow with banana-like texture", 1);
        (new StoreHandler()).UpdateInventory(session_id, "FoxHome","shirt", 40.0, "Clothing", "hawaiian shirt", 1);
        (new StoreHandler()).UpdateInventory(session_id, "FoxHome","hat", 900.0, "Clothing", "beauty pillow", 1);
    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }


    @Test
    public void valid() {
        String result = handler.searchProduct(session_id, null,"Clothing",null);
        assertTrue(result.equals("[{\"price\":40.0,\"name\":\"shirt\",\"description\":\"hawaiian shirt\",\"store\":\"FoxHome\"},{\"price\":900.0,\"name\":\"hat\",\"description\":\"beauty pillow\",\"store\":\"FoxHome\"}]") || result.equals("[{\"price\":900.0,\"name\":\"hat\",\"description\":\"beauty pillow\",\"store\":\"FoxHome\"},{\"price\":40.0,\"name\":\"shirt\",\"description\":\"hawaiian shirt\",\"store\":\"FoxHome\"}]"));
        result = handler.filterResults(session_id, 0,50, null);
        assertEquals("[{\"price\":40.0,\"name\":\"shirt\",\"description\":\"hawaiian shirt\",\"store\":\"FoxHome\"}]", result);
    }

    @Test
    public void searchNoMatch() {
        try{
            String result = handler.searchProduct(session_id, "hat","Food",null);
            fail();
        }catch(Exception e) {
            assertEquals("There are no products that match these parameters", e.getMessage());
        }
    }

    @Test
    public void filterNoMatch() {
        handler.searchProduct(session_id, null,"Clothing",null);
        try{
            String result = handler.filterResults(session_id, 0,10, null);
            fail();
        }catch(Exception e) {
            assertEquals("There are no products that match this search filter", e.getMessage());
        }
    }

    @Test
    public void invalidInput() {
        try{
            String result = handler.searchProduct(session_id, null,null,null);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter search parameter", e.getMessage());
        }
        try{
            handler.searchProduct(session_id, "",null,null);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter search parameter", e.getMessage());
        }
    }



}
