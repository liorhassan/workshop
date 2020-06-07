package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC3_1 {

    private UsersHandler handler;
    private UUID session_id;
    @Before
    public void setUp() throws Exception {
        handler = new UsersHandler();
        session_id = (new SessionHandler()).openNewSession();
    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @BeforeClass
    public void init() throws Exception{
        (new UsersHandler()).register("shauli","shauli");
        (new UsersHandler()).login(session_id, "shauli","shauli", false);
    }
    @After
    public void tearDown() throws Exception {
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid() {
        String result = handler.logout(session_id);
        assertEquals("You have been successfully logged out!", result);
    }


}
