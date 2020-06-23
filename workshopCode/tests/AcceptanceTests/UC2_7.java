package AcceptanceTests;

import DataAccessLayer.PersistenceController;
import ServiceLayer.SessionHandler;
import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.StoreHandler;
import org.junit.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UC2_7 {

    private static ShoppingCartHandler service;
    private static UUID session_id;


    @BeforeClass
    public static void init() throws Exception {
        PersistenceController.initiate(false);

        service = new ShoppingCartHandler();
        session_id = (new SessionHandler()).openNewSession();
        (new UsersHandler()).register("lior", "1234");
        (new UsersHandler()).login(session_id, "lior", "1234", false);
        (new StoreHandler()).openNewStore(session_id, "Rami Levi", "Supermarket");
        (new StoreHandler()).openNewStore(session_id, "Shufersal", "Supermarket");
        (new StoreHandler()).UpdateInventory(session_id, "Rami Levi","apple", 2.0, "Food", "green apple", 3);

        (new StoreHandler()).UpdateInventory(session_id, "Rami Levi","water", 3.0, "Food", "water 1 L", 2);
        (new  ShoppingCartHandler()).AddToShoppingBasket(session_id, "Rami Levi", "apple",  2);
        (new  ShoppingCartHandler()).AddToShoppingBasket(session_id, "Rami Levi", "water", 1);
    }
    @AfterClass
    public static void clean() throws SQLException {
        (new SessionHandler()).closeSession(session_id);
        (new  UsersHandler()).resetUsers();
        (new  StoreHandler()).resetStores();
    }

    @Test
    public void successful(){
        service.AddToShoppingBasket(session_id, "Rami Levi", "apple",  2);
        String output1 = service.editCart(session_id, "Rami Levi", "apple", 1);
        assertEquals("{\"SUCCESS\":\"The product has been updated successfully\"}", output1);
        String output2 = service.editCart(session_id, "Rami Levi", "water", 0);
        assertEquals("{\"SUCCESS\":\"The product has been updated successfully\"}", output2);
        String output3 = service.viewCart(session_id);
        assertEquals("[{\"amount\":1,\"price\":2.0,\"name\":\"apple\",\"store\":\"Rami Levi\"}]", output3);
    }

    @Test
    public void productIsNotExist(){
        try{
            String output = service.editCart(session_id, "Rami Levi", "fish", 2);
            fail();
        }catch(Exception e) {
            assertEquals("The product doesn’t exist in your shopping cart", e.getMessage());
        }
    }

    @Test
    public void storeDoesNotExist(){
        try{
            String output1 = service.editCart(session_id, "Shufersal", "milki", 1);
            fail();
        }catch(Exception e) {
            assertEquals("This store doesn't exist", e.getMessage());
        }
        try{
            String output2 = service.editCart(session_id, "Golda", "ice cream" , 1);
            fail();
        }catch(Exception e) {
            assertEquals("This store doesn't exist", e.getMessage());
        }
    }

    @Test
    public void productDoesNotAvailable(){
        try{
            String output1 = service.editCart(session_id, "Rami Levi", "water", 4);
            fail();
        }catch(Exception e) {
            assertEquals("The product doesn’t exist in your shopping cart", e.getMessage());
        }
        try{
            String output2 = service.editCart(session_id, "Rami Levi", "apple" , 10);
            fail();
        }catch(Exception e) {
            assertEquals("The product isn't available in the store with the requested amount", e.getMessage());
        }
    }

    @Test
    public void emptyInput(){
        try{
            String output1 = service.editCart(session_id, "", "apple", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
        try{
            String output2 = service.editCart(session_id, null, "apple", 1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
        try{
            String output3 = service.editCart(session_id, "Rami levi", "", 2);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
        try{
            String output4 = service.editCart(session_id, "Rami levi", null, 2);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
        try{
            String output5 = service.editCart(session_id, "Rami levi", "apple", -1);
            fail();
        }catch(Exception e) {
            assertEquals("Must enter store name and product name and amount bigger than 0", e.getMessage());
        }
    }

}
