package AcceptanceTests;

import ServiceLayer.UsersHandler;
import ServiceLayer.ViewPurchaseHistoryHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC3_7 {

    private ViewPurchaseHistoryHandler historyHandlerhandler;
    private UsersHandler usersHandler;

    @BeforeClass
    public void init() throws Exception {
        usersHandler = new UsersHandler();
        historyHandlerhandler = new ViewPurchaseHistoryHandler();
        usersHandler.register("nufi", "123456");
        usersHandler.login("nufi", "123456");
    }

    @AfterClass
    public void tearDown() throws Exception {
        usersHandler.resetUsers();
    }

    @Test
    public void valid() {
        String result = historyHandlerhandler.viewLoggedInUserPurchaseHistory();
        assertEquals("Shopping history:"+"\n", result);
    }
}
