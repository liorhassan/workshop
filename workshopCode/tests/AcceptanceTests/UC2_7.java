package AcceptanceTests;

import ServiceLayer.SessionHandler;
import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.StoreHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC2_7 {

    private  ShoppingCartHandler service;
    private UUID session_id;

    @Before
    public void setUp() throws Exception{
        service = new ShoppingCartHandler();
        session_id = (new SessionHandler()).openNewSession();
    }

    @BeforeClass
    public void init() throws Exception {

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
    public static void clean() {
        (new  UsersHandler()).resetUsers();
        (new  StoreHandler()).resetStores();
    }

    @After
    public void tearDown() throws Exception {
        (new SessionHandler()).closeSession(session_id);
    }

    @Test
    public void successful(){
        service.AddToShoppingBasket(session_id, "Rami Levi", "apple",  2);
        String output1 = service.editCart(session_id, "Rami Levi", "apple", 1);
        assertEquals("The product has been updated successfully", output1);
        String output2 = service.editCart(session_id, "Rami Levi", "water", 0);
        assertEquals("The product has been updated successfully", output2);
        String output3 = service.viewCart(session_id);
        assertEquals("Your ShoppingCart details: \nStore name: Rami Levi\nProduct name: apple price: 2.0 amount: 1\n", output3);
        service.AddToShoppingBasket(session_id, "Rami Levi", "water", 1);
    }

    @Test
    public void productIsNotExist(){
        String output = service.editCart(session_id, "Rami Levi", "fish", 2);
        assertEquals("The product doesn’t exist in your shopping cart", output);
    }

    @Test
    public void storeDoesNotExist(){
        String output1 = service.editCart(session_id, "Shufersal", "milki", 1);
        assertEquals("This store doesn't exist", output1);
        String output2 = service.editCart(session_id, "Golda", "ice cream" , 1);
        assertEquals("This store doesn't exist", output2);
    }

    @Test
    public void productDoesNotAvailable(){
        String output1 = service.editCart(session_id, "Rami Levi", "water", 4);
        assertEquals("The product isn't available in the store with the requested amount", output1);
        String output2 = service.editCart(session_id, "Rami Levi", "apple" , 10);
        assertEquals("The product isn't available in the store with the requested amount", output2);
    }

    @Test
    public void emptyInput(){
        String output1 = service.editCart(session_id, "", "apple", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", output1);
        String output2 = service.editCart(session_id, null, "apple", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", output2);
        String output3 = service.editCart(session_id, "Rami levi", "", 2);
        assertEquals("Must enter store name and product name and amount bigger than 0", output3);
        String output4 = service.editCart(session_id, "Rami levi", null, 2);
        assertEquals("Must enter store name and product name and amount bigger than 0", output4);
        String output5 = service.editCart(session_id, "Rami levi", "apple", -1);
        assertEquals("Must enter store name and product name and amount bigger than 0", output5);
    }

}
