package AcceptanceTests;

import ServiceLayer.UsersHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC3_1 {

    private UsersHandler handler;
    @Before
    public void setUp() throws Exception {
        handler = new UsersHandler();
    }

    @BeforeClass
    public static void init() throws Exception{
        (new UsersHandler()).register("shauli","shauli");
        (new UsersHandler()).login("shauli","shauli", false);
    }

    @Test
    public void valid() {
        String result = handler.logout();
        assertEquals("You have been successfully logged out!", result);
    }


}
