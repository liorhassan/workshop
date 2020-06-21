package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.StoreManagerHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

public class UC4_6 {

    private static StoreManagerHandler storeManagerHandler;
    private static UUID session_id;


    @BeforeClass
    public static void init(){
        PersistenceController.initiate();
        session_id = (new SessionHandler()).openNewSession();
        storeManagerHandler = new StoreManagerHandler();
        // store owner
        (new UsersHandler()).register("noy", "1234");
        // store manager
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).login(session_id, "noy", "1234", false);
        (new StoreHandler()).openNewStore(session_id, "Mcdonalds", "The best hamburger in town");
        (new StoreManagerHandler()).addStoreManager(session_id, "maor", "Mcdonalds");
    }

    @AfterClass
    public static void clean() {
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
        (new SessionHandler()).closeSession(session_id);
    }


    @Test
    public void valid() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        String result = storeManagerHandler.editManagerPermissions(session_id, "maor", p, "Mcdonalds");
        assertEquals("{\"SUCCESS\":\"Privileges have been edited successfully\"}", result);
    }

    @Test
    public void userDoesntExist() {
        List<String> p = new LinkedList<>();
        p.add("View Store Purchase History");
        try{
            storeManagerHandler.editManagerPermissions(session_id, "zuzu", p, "Mcdonalds");
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
            storeManagerHandler.editManagerPermissions(session_id, "maor", p, "Lalin");
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
            storeManagerHandler.editManagerPermissions(session_id, "zuzu", p, "Mcdonalds");
        }
        catch(Exception e){
            assertEquals("You can't edit this user's privileges", e.getMessage());
        }
    }

    @Test
    public void emptyArg() {
        List<String> p = new LinkedList<>();
        try{
            storeManagerHandler.editManagerPermissions(session_id, "maor", p, "Mcdonalds");
        }
        catch(Exception e){
            assertEquals("Must enter username, permissions list and store name", e.getMessage());
        }

        p.add("View Store Purchase History");
        try{
            storeManagerHandler.editManagerPermissions(session_id, "maor", p, "");
        }
        catch(Exception e){
            assertEquals("Must enter username, permissions list and store name", e.getMessage());
        }

        try{
            storeManagerHandler.editManagerPermissions(session_id, "", p, "Mcdonalds");
        }
        catch(Exception e){
            assertEquals("Must enter username, permissions list and store name", e.getMessage());
        }
    }

//    @Test
//    public void userIsNotOwner() {
//        (new UsersHandler()).logout(session_id);
//        (new UsersHandler()).login(session_id, "maor", "1234", false);
//        List<String> p = new LinkedList<>();
//        p.add("View Store Purchase History");
//        try{
//            storeManagerHandler.editManagerPermissions(session_id, "noy", p, "Mcdonalds");
//        }
//        catch(Exception e){
//            assertEquals("You must be this store owner for this command", e.getMessage());
//        }
//    }

}
