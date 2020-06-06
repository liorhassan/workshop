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
        (new UsersHandler()).login("noy", "1234", false);
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        String result = storeManagerHandler.editManagerPermissions("maor", p, "Mcdonalds");
        assertEquals("{\"SUCCESS\":\"Privileges have been edited successfully\"}", result);
    }

    @Test
    public void userDoesntExist() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        try{
            storeManagerHandler.editManagerPermissions("zuzu", p, "Mcdonalds");
        }
        catch(Exception e){
            assertEquals("This username doesn't exist", e.getMessage());
        }
    }

    @Test
    public void storeDoesntExist() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        try{
            storeManagerHandler.editManagerPermissions("maor", p, "Lalin");
        }
        catch(Exception e){
            assertEquals("This store doesn't exists", e.getMessage());
        }
    }

    @Test
    public void userIsNotManager() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        (new UsersHandler()).register("zuzu", "1234");
        try{
            storeManagerHandler.editManagerPermissions("zuzu", p, "Mcdonalds");
        }
        catch(Exception e){
            assertEquals("You can't edit this user's privileges", e.getMessage());
        }
    }

    @Test
    public void emptyArg() {
        List<String> p = new LinkedList<>();
        try{
            storeManagerHandler.editManagerPermissions("maor", p, "Mcdonalds");
        }
        catch(Exception e){
            assertEquals("Must enter username, permissions list and store name", e.getMessage());
        }

        p.add("View Store Purchase History");
        try{
            storeManagerHandler.editManagerPermissions("maor", p, "");
        }
        catch(Exception e){
            assertEquals("Must enter username, permissions list and store name", e.getMessage());
        }

        try{
            storeManagerHandler.editManagerPermissions("", p, "Mcdonalds");
        }
        catch(Exception e){
            assertEquals("Must enter username, permissions list and store name", e.getMessage());
        }
    }

    @Test
    public void userIsNotOwner() {
        (new UsersHandler()).logout();
        (new UsersHandler()).login("maor", "1234", false);
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        try{
            storeManagerHandler.editManagerPermissions("noy", p, "Mcdonalds");
        }
        catch(Exception e){
            assertEquals("You must be this store owner for this command", e.getMessage());
        }
    }

}
