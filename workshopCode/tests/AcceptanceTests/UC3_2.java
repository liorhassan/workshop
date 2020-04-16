package AcceptanceTests;

import DomainLayer.SystemHandler;
import ServiceLayer.OpenNewStore;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC3_2 {

    private OpenNewStore command;

    @BeforeClass
    public static void init() throws Exception{
        SystemHandler.getInstance().register("nufi");
        SystemHandler.getInstance().login("nufi");
    }

    @Before
    public void setUp() throws Exception {
        command = new OpenNewStore();
    }

    @After
    public void tearDown() throws Exception {
        SystemHandler.getInstance().setStores(new HashMap<>());
    }

    @Test
    public void valid() {
        String result = command.execute("KKW store", "best Kim Kardashian beauty products");
        assertEquals("The new store is now open!", result);
    }

    @Test
    public void emptyInput(){
        String result = command.execute("", "best Kim Kardashian beauty products");
        assertEquals("Must enter store name and description", result);
        result = command.execute(null, "best Kim Kardashian beauty products");
        assertEquals("Must enter store name and description", result);
        result = command.execute("KKW store", "");
        assertEquals("Must enter store name and description", result);
        result = command.execute("KKW store", null);
        assertEquals("Must enter store name and description", result);
    }

    @Test
    public void storeAlreadyExist(){
        command.execute("KKW store", "best Kim Kardashian beauty products");
        String result = command.execute("KKW store", "best storeeeee");
        assertEquals("Store name already exists, please choose a different one", result);
    }
}
