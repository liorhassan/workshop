package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC3_1 {

    private static UsersHandler handler;
    private static UUID session_id;


    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }

    @BeforeClass
    public static void init() throws Exception{
        PersistenceController.initiate();
        handler = new UsersHandler();
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("shauli","shauli");
        (new UsersHandler()).login(session_id, "shauli","shauli", false);
    }

    @Test
    public void valid() {
        String result = handler.logout(session_id);
        assertEquals("{\"SUCCESS\":\"You have been successfully logged out!\"}", result);
    }


}
