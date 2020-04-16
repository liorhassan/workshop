package AcceptanceTests;

import DomainLayer.SystemHandler;
import ServiceLayer.ViewUserPurchaseHistory;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC3_7 {

    private ViewUserPurchaseHistory command;

    @BeforeClass
    public static void init() throws Exception{
        SystemHandler.getInstance().register("nufi");
        SystemHandler.getInstance().login("nufi");
    }

    @Before
    public void setUp() throws Exception {
        command = new ViewUserPurchaseHistory();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        SystemHandler.getInstance().setUsers(new HashMap<>());
    }

    @Test
    public void valid() {
        String result = command.execute();
        assertEquals("Shopping history:"+"\n", result);
    }
}
