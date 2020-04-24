package AcceptanceTests;


import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UC2_8 {

    private static ShoppingCartHandler shoppingCartHandler;
    private static UsersHandler usersHandler;
    private static StoreHandler storeHandler;

    @Before
    public void setUp(){
        shoppingCartHandler = new ShoppingCartHandler();
        usersHandler = new UsersHandler();
        storeHandler = new StoreHandler();
    }

    @BeforeClass
    public static void init(){
        usersHandler.register("noy", "1234");
        usersHandler.login("noy", "1234");
        storeHandler.openNewStore("Castro", "clothes for women and men");
        storeHandler.openNewStore("Lalin", "beauty products");
        storeHandler.UpdateInventory("Castro", "white T-shirt", 25, "Clothing", "white T-shirt for men", 3);
        storeHandler.UpdateInventory("Castro", "jeans skirt", 50, "Clothing", "mini jeans skirt for women", 2);
        storeHandler.UpdateInventory("Lalin", "Body Cream ocean", 40, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        storeHandler.UpdateInventory("Lalin", "Body Scrub musk", 50, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 20);
    }

    @AfterClass
    public static void clean(){
        usersHandler.logout();
        usersHandler.resetUsers();
        storeHandler.resetStores();
    }

    @Test
    public void emptyCart() {
        String result = shoppingCartHandler.purchaseCart();
        assertEquals("The shopping cart is empty", result);
    }


    @Test
    public void valid() {
        shoppingCartHandler.AddToShoppingBasket("Castro", "jeans skirt", 2);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Cream ocean", 5);
        String result = shoppingCartHandler.purchaseCart();
        assertEquals("Purchasing completed successfully", result);
    }


    @Test
    public void productNotInStock() {
        shoppingCartHandler.AddToShoppingBasket("Castro", "jeans skirt", 3);
        String result = shoppingCartHandler.purchaseCart();
        assertEquals("There is currently no stock of 3 jeans skirt products", result);

        shoppingCartHandler.AddToShoppingBasket("Lalin", "body oil", 3);
        result = shoppingCartHandler.purchaseCart();
        assertEquals("There is currently no stock of 3 jeans skirt products", result);
    }

}
