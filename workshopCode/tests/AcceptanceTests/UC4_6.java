package AcceptanceTests;

import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class UC4_6 {

    private static StoreManagerHandler storeManagerHandler;

    @Before
    public void setUp(){
        storeManagerHandler = new StoreManagerHandler();
    }

    @BeforeClass
    public static void init(){
        // store owner
        (new UsersHandler()).register("noy", "1234");
        // store manager
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).login("noy", "1234", false);
        (new StoreHandler()).openNewStore("Mcdonalds", "The best hamburger in town");
        (new StoreManagerHandler()).addStoreManager("maor", "Mcdonalds");
    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).logout();
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void valid() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        String result = storeManagerHandler.editManagerPermissions("maor", p, "Mcdonalds");
        assertEquals("Privileges have been edited successfully", result);
    }

    @Test
    public void userDoesntExist() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        String result = storeManagerHandler.editManagerPermissions("zuzu", p, "Mcdonalds");
        assertEquals("This username doesn't exist", result);
    }

    @Test
    public void storeDoesntExist() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        String result = storeManagerHandler.editManagerPermissions("maor", p, "Lalin");
        assertEquals("This store doesn't exists", result);
    }

    @Test
    public void userIsNotManager() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        (new UsersHandler()).register("zuzu", "1234");
        String result = storeManagerHandler.editManagerPermissions("zuzu", p, "Mcdonalds");
        assertEquals("You can't edit this user's privileges", result);
    }

    @Test
    public void emptyArg() {
        List<String> p = new LinkedList<>();
        String result =  storeManagerHandler.editManagerPermissions("maor", p, "Mcdonalds");
        assertEquals("Must enter username, permissions list and store name", result);

        p.add("View Store Purchase History");
        result =  storeManagerHandler.editManagerPermissions("maor", p, "");
        assertEquals("Must enter username, permissions list and store name", result);

        result =  storeManagerHandler.editManagerPermissions("", p, "Mcdonalds");
        assertEquals("Must enter username, permissions list and store name", result);
    }

    @Test
    public void userIsNotOwner() {
        (new UsersHandler()).logout();
        (new UsersHandler()).login("maor", "1234", false);
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        String result =  storeManagerHandler.editManagerPermissions("noy", p, "Mcdonalds");
        assertEquals("You must be this store owner for this command", result);
    }

}
