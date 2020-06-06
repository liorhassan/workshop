package AcceptanceTests;

import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.StoreHandler;
import org.junit.*;

import static org.junit.Assert.assertEquals;

public class UC2_7 {

    private  ShoppingCartHandler service;

    @Before
    public void setUp() throws Exception{
        service = new ShoppingCartHandler();
    }

    @BeforeClass
    public static void init() throws Exception {

        (new UsersHandler()).register("lior", "1234");
        (new UsersHandler()).login("lior", "1234", false);
        (new StoreHandler()).openNewStore("Rami Levi", "Supermarket");
        (new StoreHandler()).openNewStore("Shufersal", "Supermarket");
        (new StoreHandler()).UpdateInventory("Rami Levi","apple", 2.0, "Food", "green apple", 3);

        (new StoreHandler()).UpdateInventory("Rami Levi","water", 3.0, "Food", "water 1 L", 2);
        (new  ShoppingCartHandler()).AddToShoppingBasket("Rami Levi", "apple",  2);
        (new  ShoppingCartHandler()).AddToShoppingBasket("Rami Levi", "water", 1);
    }
    @AfterClass
    public static void clean() {
        (new  UsersHandler()).resetUsers();
        (new  StoreHandler()).resetStores();
    }

    @Test
    public void successful(){
        service.AddToShoppingBasket("Rami Levi", "apple",  2);
       // System.out.println(SystemFacade.getInstance().storeExists("Rami Levi"));
        String output1 = service.editCart("Rami Levi", "apple", 1);
        assertEquals("The product has been updated successfully", output1);
        String output2 = service.editCart("Rami Levi", "water", 0);
        assertEquals("The product has been updated successfully", output2);
        String output3 = service.viewCart();
        assertEquals("Your ShoppingCart details: \nStore name: Rami Levi\nProduct name: apple price: 2.0 amount: 1\n", output3);
        service.AddToShoppingBasket("Rami Levi", "water", 1);
    }

    @Test
    public void productIsNotExist(){
        String output = service.editCart("Rami Levi", "fish", 2);
        assertEquals("The product doesn’t exist in your shopping cart", output);
    }

    @Test
    public void storeDoesNotExist(){
        String output1 = service.editCart("Shufersal", "milki", 1);
        assertEquals("This store doesn't exist", output1);
        String output2 = service.editCart("Golda", "ice cream" , 1);
        assertEquals("This store doesn't exist", output2);
    }

    @Test
    public void productDoesNotAvailable(){
        String output1 = service.editCart("Rami Levi", "water", 4);
        assertEquals("The product isn't available in the store with the requested amount", output1);
        String output2 = service.editCart("Rami Levi", "apple" , 10);
        assertEquals("The product isn't available in the store with the requested amount", output2);
    }

    @Test
    public void emptyInput(){
        String output1 = service.editCart("", "apple", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", output1);
        String output2 = service.editCart(null, "apple", 1);
        assertEquals("Must enter store name and product name and amount bigger than 0", output2);
        String output3 = service.editCart("Rami levi", "", 2);
        assertEquals("Must enter store name and product name and amount bigger than 0", output3);
        String output4 = service.editCart("Rami levi", null, 2);
        assertEquals("Must enter store name and product name and amount bigger than 0", output4);
        String output5 = service.editCart("Rami levi", "apple", -1);
        assertEquals("Must enter store name and product name and amount bigger than 0", output5);
    }

}
