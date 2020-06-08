package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC4_7 {
    private StoreManagerHandler handler;
    private static UUID session_id;

    @Before
    public void setUp() throws Exception{
        handler = new StoreManagerHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        PersistenceController.initiate();
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("tester","tester");
        (new UsersHandler()).login(session_id, "tester","tester", false);
        (new StoreHandler()).openNewStore(session_id, "Castro", "clothing");
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).register("shauli","shauli");
        (new UsersHandler()).register("toya","toya");
        (new UsersHandler()).login(session_id, "shauli","shauli", false);
        (new StoreHandler()).openNewStore(session_id, "FoxHome", "stuff for home");
        (new StoreManagerHandler()).addStoreManager(session_id, "toya","FoxHome");
    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @After
    public void tearDown() throws Exception {
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid(){
        String result = handler.removeStoreManager(session_id, "toya","FoxHome");
        assertEquals("Manager removed successfully!", result);
        (new StoreManagerHandler()).addStoreManager(session_id, "toya","FoxHome");
    }

    @Test
    public void storeDoesNotExists(){
        String result = handler.removeStoreManager(session_id, "toya","Fox");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void userDoesNotExists(){
        String result = handler.removeStoreManager(session_id, "cooper","FoxHome");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void doesNotHavePrivileges(){
        String result = handler.removeStoreManager(session_id, "toya", "Castro");
        assertEquals("You must be this store owner for this command", result);
    }

    @Test
    public void notAppointedByUser(){
        String result = handler.removeStoreManager(session_id, "shauli","FoxHome");
        assertEquals("This username is not one of this store's managers appointed by you", result);
    }

    @Test
    public void emptyInput(){
        String result = handler.removeStoreManager(session_id, "","FoxHome");
        assertEquals("Must enter username and store name", result);
        result = handler.removeStoreManager(session_id, null,"FoxHome");
        assertEquals("Must enter username and store name", result);
        result = handler.removeStoreManager(session_id, "toya","");
        assertEquals("Must enter username and store name", result);
        result = handler.removeStoreManager(session_id, "toya",null);
        assertEquals("Must enter username and store name", result);
    }

}
