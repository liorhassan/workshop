package AcceptanceTests;


import ServiceLayer.SessionHandler;
import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UC2_8 {

    private static ShoppingCartHandler shoppingCartHandler;
    private static UUID session_id;

    @BeforeClass
    public static void init(){
        shoppingCartHandler = new ShoppingCartHandler();
        UC3_2.init(); // user toya is logged in
        session_id = UC3_2.session_id;
        (new StoreHandler()).openNewStore(session_id, "Castro", "clothes for women and men");
        (new StoreHandler()).openNewStore(session_id, "Lalin", "beauty products");
        (new StoreHandler()).UpdateInventory(session_id, "Castro", "white T-shirt", 5.0, "Clothing", "white T-shirt for men", 50);
        (new StoreHandler()).UpdateInventory(session_id, "Castro", "jeans skirt", 50.0, "Clothing", "mini jeans skirt for women", 50);
        (new StoreHandler()).UpdateInventory(session_id, "Castro", "Michael Kors bag", 1000.0, "Clothing", "bag", 50);
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Cream ocean", 40.0, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Scrub musk", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Scrub ocean", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("zuzu", "1234");
    }

    @AfterClass
    public static void clean(){
        UC3_2.clean();
    }


    @Test
    public void emptyCart() {
        (new UsersHandler()).login(session_id, "noy", "1234", false);
        try {
            shoppingCartHandler.purchaseCart(session_id);
        }
        catch(Exception e){
            assertEquals("The shopping cart is empty", e.getMessage());
        }
        (new UsersHandler()).logout(session_id );

    }


    @Test
    public void valid() {
        (new UsersHandler()).login(session_id, "noy", "1234", false);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Castro", "jeans skirt", 2);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Cream ocean", 5);
        String result = shoppingCartHandler.purchaseCart(session_id);
        assertEquals("{\"SUCCESS\":\"Purchasing completed successfully\"}", result);
        (new UsersHandler()).logout(session_id );
    }


    @Test
    public void productNotInStock() {

        (new UsersHandler()).login(session_id, "maor", "1234", false);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Castro", "white T-shirt", 50);
        (new UsersHandler()).logout(session_id);
        (new UsersHandler()).login(session_id, "toya", "1234", false);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Castro", "white T-shirt", 50);
        shoppingCartHandler.purchaseCart(session_id);
        (new UsersHandler()).logout(session_id );
        (new UsersHandler()).login(session_id, "maor", "1234", false);
        try {
            shoppingCartHandler.purchaseCart(session_id);
        }
        catch(Exception e) {
            assertEquals("There is currently no stock of 50 white T-shirt products", e.getMessage());
        }
        (new UsersHandler()).logout(session_id);

    }


    @Test
    public void paymentFail() {

        (new UsersHandler()).login(session_id, "rachel", "1234", false);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Castro", "Michael Kors bag", 2);
        try {
            shoppingCartHandler.purchaseCart(session_id);
        }
        catch (Exception e) {
            assertEquals("Payment failed", e.getMessage());
        }
        String amountInInventory = (new StoreHandler()).checkAmountInInventory("Michael Kors bag", "Castro");
        assertEquals("50", amountInInventory);
        (new UsersHandler()).logout(session_id);
    }

    @Test
    public void supplementFail() {
        (new UsersHandler()).login(session_id, "zuzu", "1234", false);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Scrub ocean", 2);

        try{
            shoppingCartHandler.purchaseCart(session_id);
        }
        catch(Exception e) {
            assertEquals("supplement failed", e.getMessage());
        }
        (new UsersHandler()).logout(session_id);
        String amountInInventory = (new StoreHandler()).checkAmountInInventory("Body Scrub ocean", "Lalin");
        assertEquals("50", amountInInventory);
        (new UsersHandler()).logout(session_id);
    }

    @Test
    public void revealedDiscountForProduct() {
        (new UsersHandler()).login(session_id, "toya", "1234", false);
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Scrub vanil", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);

        (new StoreHandler()).addDiscountRevealedProduct("Lalin","Body Scrub vanil", 50 );
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Scrub vanil", 2);
        String result = shoppingCartHandler.getCartTotalPrice(session_id);
        assertEquals("50.0", result);
        shoppingCartHandler.purchaseCart(session_id);
        (new UsersHandler()).logout(session_id);
    }

    @Test
    public void conditionalDiscountForProduct() {
        (new UsersHandler()).login(session_id, "toya", "1234", false);
        (new StoreHandler()).addDiscountCondProductAmount("Lalin","Body Scrub musk", 50,2);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Scrub musk", 2);

        String result = shoppingCartHandler.getCartTotalPrice(session_id);
        assertEquals("100.0", result);
        shoppingCartHandler.purchaseCart(session_id);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Scrub musk", 3);

        result = shoppingCartHandler.getCartTotalPrice(session_id);
        assertEquals("125.0", result);
        shoppingCartHandler.purchaseCart(session_id);
        (new UsersHandler()).logout(session_id);
    }


    @Test
    public void onCostDiscountForBasket() {
        (new UsersHandler()).login(session_id, "toya", "1234", false);
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Scrub pear", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 100, true);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Scrub pear", 3);
        String result = shoppingCartHandler.getCartTotalPrice(session_id);
        assertEquals("135.0", result);
        shoppingCartHandler.purchaseCart(session_id);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Scrub pear", 1);

        result = shoppingCartHandler.getCartTotalPrice(session_id);
        assertEquals("50.0", result);
        shoppingCartHandler.purchaseCart(session_id);
        (new UsersHandler()).logout(session_id);
    }


    @Test
    public void onProductAmountDiscountForBasket() {
        (new UsersHandler()).login(session_id, "toya", "1234", false);
        (new StoreHandler()).UpdateInventory(session_id, "Lalin", "Body Scrub apple", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);

        (new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 2, false);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Scrub apple", 1);
        String result = shoppingCartHandler.getCartTotalPrice(session_id);
        assertEquals("50.0", result);
        shoppingCartHandler.AddToShoppingBasket(session_id, "Lalin", "Body Scrub apple", 2);
        result = shoppingCartHandler.getCartTotalPrice(session_id);
        (new UsersHandler()).logout(session_id);
    }



}
