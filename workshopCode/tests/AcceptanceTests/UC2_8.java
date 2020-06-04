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
        (new StoreHandler()).openNewStore("Castro", "clothes for women and men");
        (new StoreHandler()).openNewStore("Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory("Castro", "white T-shirt", 5.0, "Clothing", "white T-shirt for men", 50);
        (new StoreHandler()).UpdateInventory("Castro", "jeans skirt", 50.0, "Clothing", "mini jeans skirt for women", 50);
        (new StoreHandler()).UpdateInventory("Castro", "Michael Kors bag", 1000.0, "Clothing", "bag", 50);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Cream ocean", 40.0, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub musk", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new UsersHandler()).logout();
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("zuzu", "1234");
    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void emptyCart() {
        (new UsersHandler()).login("noy", "1234", false);
        try {
            shoppingCartHandler.purchaseCart();
        }
        catch(Exception e){
            assertEquals("The shopping cart is empty", e.getMessage());
        }
        (new UsersHandler()).logout();

    }


    @Test
    public void valid() {
        (new UsersHandler()).login("noy", "1234", false);
        shoppingCartHandler.AddToShoppingBasket("Castro", "jeans skirt", 2);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Cream ocean", 5);
        String result = shoppingCartHandler.purchaseCart();
        assertEquals("{\"SUCCESS\":\"Purchasing completed successfully\"}", result);
        (new UsersHandler()).logout();
    }


    @Test
    public void productNotInStock() {

        (new UsersHandler()).login("maor", "1234", false);
        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-shirt", 50);
        (new UsersHandler()).logout();
        (new UsersHandler()).login("toya", "1234", false);
        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-shirt", 50);
        shoppingCartHandler.purchaseCart();
        (new UsersHandler()).logout();
        (new UsersHandler()).login("maor", "1234", false);
        try {
            shoppingCartHandler.purchaseCart();
        }
        catch(Exception e) {
            assertEquals("There is currently no stock of 50 white T-shirt products", e.getMessage());
        }
        (new UsersHandler()).logout();

    }


    @Test
    public void paymentFail() {

        (new UsersHandler()).login("rachel", "1234", false);
        shoppingCartHandler.AddToShoppingBasket("Castro", "Michael Kors bag", 2);
        try {
            shoppingCartHandler.purchaseCart();
        }
        catch (Exception e) {
            assertEquals("Payment failed", e.getMessage());
        }
        String amountInInventory = (new StoreHandler()).checkAmountInInventory("Michael Kors bag", "Castro");
        assertEquals("50", amountInInventory);
        (new UsersHandler()).logout();
    }

    @Test
    public void supplementFail() {
        (new UsersHandler()).login("zuzu", "1234", false);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub musk", 2);

        try{
            shoppingCartHandler.purchaseCart();
        }
        catch(Exception e) {
            assertEquals("supplement failed", e.getMessage());
        }
        (new UsersHandler()).logout();
        String amountInInventory = (new StoreHandler()).checkAmountInInventory("Body Scrub musk", "Lalin");
        assertEquals("50", amountInInventory);
        (new UsersHandler()).logout();
    }

    @Test
    public void revealedDiscountForProduct() {
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).addDiscountRevealedProduct("Lalin","Body Scrub musk", 50 );
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub musk", 2);
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
        (new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 100, true);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub musk", 3);
        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("135.0", result);
        shoppingCartHandler.purchaseCart();
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub musk", 1);

        result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("50.0", result);
        shoppingCartHandler.purchaseCart();
        (new UsersHandler()).logout();
    }


    @Test
    public void onProductAmountDiscountForBasket() {
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 2, false);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub musk", 1);
        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("50", result);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub musk", 2);
        result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("120", result);
        (new UsersHandler()).logout();
    }



}
