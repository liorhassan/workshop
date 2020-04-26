package AcceptanceTests;

import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewPurchaseHistoryHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC3_7 {

    private ViewPurchaseHistoryHandler historyHandlerhandler;

    @BeforeClass
    public static void init() throws Exception {
        (new UsersHandler()).register("nufi", "123456");
        (new UsersHandler()).login("nufi", "123456", false);
    }

    @Before
    public void setUp() throws Exception {
        historyHandlerhandler = new ViewPurchaseHistoryHandler();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        (new UsersHandler()).resetUsers();
    }

    @Test
    public void valid() {
        String result = historyHandlerhandler.viewLoggedInUserPurchaseHistory();
        assertEquals("Shopping history:"+"\n", result);
    }
}
