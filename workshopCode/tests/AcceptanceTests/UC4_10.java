package AcceptanceTests;

import DomainLayer.*;
import ServiceLayer.ViewStorePurchaseHistory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UC4_10 {

    ViewStorePurchaseHistory storePurchaseHistory;
    @Before
    public void setUp(){
        storePurchaseHistory = new ViewStorePurchaseHistory();
    }

    @BeforeClass
    public static void init(){
        SystemHandler.getInstance().register("noy");
        SystemHandler.getInstance().register("maor");
        SystemHandler.getInstance().register("zuzu");
        SystemHandler.getInstance().register("rachel");
        SystemHandler.getInstance().login("noy");
        //open new store
        Store s = new Store("Lalin", "beauty products", SystemHandler.getInstance().getActiveUser(), new StoreOwning(null));
        s.addToInventory("Body Cream ocean", 40, Category.BeautyProducts, "Velvety and soft skin lotion with ocean scent", 50);
        s.addToInventory("Body Scrub musk", 50, Category.BeautyProducts, "Deep cleaning with natural salt crystals with a musk scent", 20);
        SystemHandler.getInstance().appointManager("maor", "Lalin");
        SystemHandler.getInstance().appointManager("rachel", "Lalin");
        List<Permission> p = new LinkedList<>();
        p.add(new Permission("View store purchase history"));
        SystemHandler.getInstance().editPermissions("rachel", p, "Lalin");
        SystemHandler.getInstance().addToShoppingBasket("Lalin", "Body Cream ocean", 5);
        SystemHandler.getInstance().purchaseCart();
    }


    @AfterClass
    public static void clean() {

    }

    @Test
    public void valid() {
        //noy - store owner
        String result = storePurchaseHistory.execute("Lalin");
        String expectedResult = "Shopping history:\nPurchase #1:\n + " +
                "Product name: Body Cream ocean price: 50 amount: 5" +
                "\ntotal money paid: 250";
        assertEquals(expectedResult, result);

        //rachel - store manager with the required permissions
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().login("rachel");
        result = storePurchaseHistory.execute("Lalin");
        assertEquals(expectedResult, result);
    }

    @Test
    public void storeDoesntExists() {
        String result = storePurchaseHistory.execute("Swear");
        assertEquals("This store doesn't exist", result);
    }

    @Test
    public void noPermissions() {
        //zuzu - neither a manager nor a store owner
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().login("zuzu");
        String result = storePurchaseHistory.execute("Lalin");
        assertEquals("You are not allowed to view this store's purchasing history", result);

        //maor - manager without required permission
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().login("maor");
        result = storePurchaseHistory.execute("Lalin");
        assertEquals("You are not allowed to view this store's purchasing history", result);
    }

    @Test
    public void emptyStore() {
        String result = storePurchaseHistory.execute("");
        assertEquals("Must enter store name", result);
    }

}
