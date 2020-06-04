package AcceptanceTests;

import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UC4_2 {

    private static ShoppingCartHandler shoppingCartHandler;

    @Before
    public void setUp(){
        shoppingCartHandler = new ShoppingCartHandler();
    }

    @BeforeClass
    public static void init(){
        UC3_2.init(); // user toya is logged in
        (new StoreHandler()).openNewStore("Castro", "clothes for women and men");
        (new StoreHandler()).openNewStore("Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory("Lalin", "Body Cream ocean", 40.0, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub musk", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new UsersHandler()).logout();
        (new UsersHandler()).register("zuzu", "1234");
    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }
    @Test
    public void revealedDiscountForProduct() {
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub vanil", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);

        (new StoreHandler()).addDiscountRevealedProduct("Lalin","Body Scrub vanil", 50 );
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub vanil", 2);
        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("50.0", result);
        shoppingCartHandler.purchaseCart();
        (new UsersHandler()).logout();
    }

    @Test
    public void conditionalDiscountForProduct() {
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).addDiscountCondProductAmount("Lalin","Body Scrub musk", 50,2);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub musk", 2);

        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("100.0", result);
        shoppingCartHandler.purchaseCart();
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub musk", 3);

        result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("125.0", result);
        shoppingCartHandler.purchaseCart();
        (new UsersHandler()).logout();
    }


    @Test
    public void onCostDiscountForBasket() {
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub pear", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 100, true);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub pear", 3);
        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("135.0", result);
        shoppingCartHandler.purchaseCart();
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub pear", 1);

        result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("50.0", result);
        shoppingCartHandler.purchaseCart();
        (new UsersHandler()).logout();
    }


    @Test
    public void onProductAmountDiscountForBasket() {
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub apple", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);

        (new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 2, false);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub apple", 1);
        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("50.0", result);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub apple", 2);
        result = shoppingCartHandler.getCartTotalPrice();
        //assertEquals("120", result);
        (new UsersHandler()).logout();
    }
}
