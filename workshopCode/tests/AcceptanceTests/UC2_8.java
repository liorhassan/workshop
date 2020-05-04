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

    @Before
    public void setUp(){
        shoppingCartHandler = new ShoppingCartHandler();
    }

    @BeforeClass
    public static void init(){
        UC3_2.init(); // user toya is logged in
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).openNewStore("Castro", "clothes for women and men");
        (new StoreHandler()).openNewStore("Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory("Castro", "white T-shirt", 25, "Clothing", "white T-shirt for men", 3);
        (new StoreHandler()).UpdateInventory("Castro", "jeans skirt", 50, "Clothing", "mini jeans skirt for women", 4);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Cream ocean", 40, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub musk", 50, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 20);
    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).logout();
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
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
        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-shirt", 3);
        (new UsersHandler()).logout();
        (new UsersHandler()).login("maor", "1234", false);
        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-shirt", 2);
        shoppingCartHandler.purchaseCart();
        (new UsersHandler()).logout();
        (new UsersHandler()).login("toya", "1234", false);
        String result = shoppingCartHandler.purchaseCart();
        assertEquals("There is currently no stock of 3 white T-shirt products", result);

}

}
