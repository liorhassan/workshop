package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewPurchaseHistoryHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC3_7 {

    private ViewPurchaseHistoryHandler historyHandlerhandler;
    private UUID session_id;

    @BeforeClass
    public void init() throws Exception {
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("nufi", "1234");
        (new UsersHandler()).login(session_id, "nufi", "1234", false);
    }

    @Before
    public void setUp() throws Exception {
        historyHandlerhandler = new ViewPurchaseHistoryHandler();
    }


    @After
    public void tearDown() throws Exception {
        (new UsersHandler()).resetUsers();
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void valid() {
        String result = historyHandlerhandler.viewLoggedInUserPurchaseHistory(session_id);
        assertEquals("Shopping history:"+"\n", result);
    }
}
