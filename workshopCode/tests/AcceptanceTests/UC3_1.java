package AcceptanceTests;

import DomainLayer.Category;
import DomainLayer.Store;
import DomainLayer.SystemHandler;
import ServiceLayer.FilterResults;
import ServiceLayer.Logout;
import ServiceLayer.SearchProduct;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC3_1 {

    private Logout command;
    @Before
    public void setUp() throws Exception {
        command = new Logout();
    }

    @BeforeClass
    public static void init() throws Exception{
        SystemHandler.getInstance().register("shauli");
        SystemHandler.getInstance().login("shauli");
    }

    @Test
    public void valid() {
        String result = command.execute();
        assertEquals("You have been successfully logged out!", result);
    }


}
