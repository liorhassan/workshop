package AcceptanceTests;

import DomainLayer.SystemHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewPurchaseHistoryHandler;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC3_7 {

    private static ViewPurchaseHistoryHandler historyHandlerhandler;
    private static UsersHandler usersHandler;

    @BeforeClass
    public static void init() throws Exception {
        usersHandler = new UsersHandler();
        usersHandler.register("nufi", "123456");
        usersHandler.login("nufi", "123456");
        historyHandlerhandler = new ViewPurchaseHistoryHandler();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // TODO: fix!!!!!!!!!!!!!!
        SystemHandler.getInstance().setUsers(new HashMap<>());
    }

    @Test
    public void valid() {
        String result = historyHandlerhandler.ViewPurchaseHistoryOfUser();
        assertEquals("Shopping history:"+"\n", result);
    }
}
