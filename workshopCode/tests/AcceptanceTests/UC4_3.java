package AcceptanceTests;

import DomainLayer.SystemHandler;
import ServiceLayer.AddStoreOwner;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC4_3 {

    private AddStoreOwner command;

    @BeforeClass
    public static void init() throws Exception{
        // store owner (appointer)
        SystemHandler.getInstance().register("nufi");

        // subscribed user (appointee)
        SystemHandler.getInstance().register("tooti");

        SystemHandler.getInstance().login("nufi");
        SystemHandler.getInstance().openNewStore("KKW", "best Kim Kardashian beauty products");
    }

    @AfterClass
    public static void clean() {
        SystemHandler.getInstance().setUsers(new HashMap<>());
        SystemHandler.getInstance().setStores(new HashMap<>());
    }

    @Before
    public void setUp() throws Exception {
        command = new AddStoreOwner();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void valid() {
        String result = command.execute("tooti", "KKW");
        assertEquals("Username has been added as one of the store owners successfully", result);
    }

    @Test
    public void emptyInput(){
        String result = command.execute("", "KKW");
        assertEquals("Must enter username and store name", result);
        result = command.execute(null, "KKW");
        assertEquals("Must enter username and store name", result);
        result = command.execute("tooti", "");
        assertEquals("Must enter username and store name", result);
        result = command.execute("tooti", null);
        assertEquals("Must enter username and store name", result);
    }

    @Test
    public void storeDoesNotExist(){
        String result = command.execute("tooti", "poosh");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void userDoesNotExist(){
        String result = command.execute("tooton", "KKW");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void appointerIsNotOwner() {
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().register("toya");
        SystemHandler.getInstance().login("toya");
        String result = command.execute("tooti", "KKW");
        assertEquals("You must be this store owner for this action", result);
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().login("nufi");
    }

    @Test
    public void userAlreadyOwner() {
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().register("cooper");
        SystemHandler.getInstance().login("nufi");
        command.execute("cooper", "KKW");
        String result = command.execute("cooper", "KKW");
        assertEquals("This username is already one of the store's owners", result);
    }
}
