package AcceptanceTests;

import ServiceLayer.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UC4_10 {

    private static ViewPurchaseHistoryHandler storePurchaseHistory;
    private static UsersHandler usersHandler;
    private static StoreHandler storeHandler;
    private static StoreManagerHandler storeManagerHandler;
    private static ShoppingCartHandler shoppingCartHandler;

    @Before
    public void setUp(){
        storePurchaseHistory = new ViewPurchaseHistoryHandler();
        usersHandler = new UsersHandler();
        storeHandler = new StoreHandler();
        storeManagerHandler = new StoreManagerHandler();
        shoppingCartHandler = new ShoppingCartHandler();
    }

    @BeforeClass
    public static void init(){
        usersHandler.register("noy", "1234");
        usersHandler.register("rachel", "1234");
        usersHandler.register("maor", "1234");
        usersHandler.register("zuzu", "1234");
        usersHandler.login("noy", "1234");
        storeHandler.openNewStore("Lalin", "beauty products");
        storeHandler.UpdateInventory("Lalin", "Body Cream ocean", 40, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        storeHandler.UpdateInventory("Lalin", "Body Scrub musk", 50, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 20);
        storeManagerHandler.addStoreManager("rachel", "Lalin");
        storeManagerHandler.addStoreManager("maor", "Lalin");

        List<String> p = new LinkedList<>();
        p.add("View store purchase history");
        storeManagerHandler.editManagerPermissions("rachel", p, "Lalin");

        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Cream ocean", 5);
        shoppingCartHandler.purchaseCart();
    }


    @AfterClass
    public static void clean() {

    }

    @Test
    public void valid() {
        //noy - store owner
        String result = storePurchaseHistory.ViewPurchaseHistoryOfStore("Lalin");
        String expectedResult = "Shopping history:\nPurchase #1:\n + " +
                "Product name: Body Cream ocean price: 50 amount: 5" +
                "\ntotal money paid: 250";
        assertEquals(expectedResult, result);

        //rachel - store manager with the required permissions
        usersHandler.logout();
        usersHandler.login("rachel", "1234");
        result = storePurchaseHistory.ViewPurchaseHistoryOfStore("Lalin");
        assertEquals(expectedResult, result);
    }

    @Test
    public void storeDoesntExists() {
        String result = storePurchaseHistory.ViewPurchaseHistoryOfStore("Swear");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void noPermissions() {
        //zuzu - neither a manager nor a store owner
        usersHandler.logout();
        usersHandler.login("zuzu", "1234");
        String result = storePurchaseHistory.ViewPurchaseHistoryOfStore("Lalin");
        assertEquals("You are not allowed to view this store's purchasing history", result);

        //maor - manager without required permission
        usersHandler.logout();
        usersHandler.login("maor", "1234");
        result = storePurchaseHistory.ViewPurchaseHistoryOfStore("Lalin");
        assertEquals("You are not allowed to view this store's purchasing history", result);
    }

    @Test
    public void emptyStore() {
        String result = storePurchaseHistory.ViewPurchaseHistoryOfStore("");
        assertEquals("Must enter store name", result);
    }

}
