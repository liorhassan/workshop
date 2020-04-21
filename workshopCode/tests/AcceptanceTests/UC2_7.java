package AcceptanceTests;

import DomainLayer.Category;
import DomainLayer.Store;
import DomainLayer.StoreOwning;
import DomainLayer.SystemHandler;
import ServiceLayer.ViewAndEditShoppingCart;
import ServiceLayer.Register;
import org.junit.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC2_7 {

    private  ViewAndEditShoppingCart editShoppingCart;

    @Before
    public void setUp() throws Exception{
        editShoppingCart = new ViewAndEditShoppingCart();
    }

    @BeforeClass
    public static void init() throws Exception {

        Store store = new Store("Rami Levi", "supermarket", SystemHandler.getInstance().getActiveUser(), new StoreOwning());
        Store store2 = new Store("Shufersal", "supermarket", SystemHandler.getInstance().getActiveUser(), new StoreOwning());

        SystemHandler.getInstance().getStores().put("Rami Levi", store);
        SystemHandler.getInstance().getStores().put("Shufersal", store2);
        store.addToInventory("apple", 2, Category.Food, "green apple", 3);

        store.addToInventory("water", 3, Category.Food, "water 1 L", 2);
        SystemHandler.getInstance().addToShoppingBasket("Rami Levi", "apple",  2);
        SystemHandler.getInstance().addToShoppingBasket("Rami Levi", "water", 1);
    }
    @AfterClass
    public static void clean() {
        SystemHandler.getInstance().setUsers(new HashMap<>());
        SystemHandler.getInstance().setStores(new HashMap<>());
    }

    @Test
    public void successful(){
        String output1 = editShoppingCart.edit("Rami Levi", "apple", 1);
        assertEquals("The product has been updated successfully", output1);
        String output2 = editShoppingCart.edit("Rami Levi", "water", 0);
        assertEquals("The product has been updated successfully", output2);
        String output3 = editShoppingCart.view();
        assertEquals("Your ShoppingCart details: \nStore name: Rami Levi\nProduct name: apple price: 2.0 amount: 1\n", output3);
    }

    @Test
    public void productIsNotExist(){
        String output = editShoppingCart.edit("Rami Levi", "fish", 2);
        assertEquals("The product doesn’t exist in your shopping cart", output);
    }

    @Test
    public void storeDoesNotExist(){
        String output1 = editShoppingCart.edit("Shufersal", "milki", 1);
        assertEquals("This store doesn't exist", output1);
        String output2 = editShoppingCart.edit("Golda", "ice cream" , 1);
        assertEquals("This store doesn't exist", output2);
    }

    @Test
    public void emptyInput(){
        String output1 = editShoppingCart.edit("", "apple", 1);
        assertEquals("Must enter product name, store name and amount", output1);
        String output2 = editShoppingCart.edit(null, "apple", 1);
        assertEquals("Must enter product name, store name and amount", output2);
        String output3 = editShoppingCart.edit("Rami levi", "", 2);
        assertEquals("Must enter product name, store name and amount", output3);
        String output4 = editShoppingCart.edit("Rami levi", null, 2);
        assertEquals("Must enter product name, store name and amount", output4);
        String output5 = editShoppingCart.edit("Rami levi", "apple", -1);
        assertEquals("Must enter product name, store name and amount", output5);
    }

}
