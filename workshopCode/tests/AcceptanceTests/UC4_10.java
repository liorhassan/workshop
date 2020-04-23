package AcceptanceTests;

import DomainLayer.Category;
import DomainLayer.Store;
import DomainLayer.StoreOwning;
import DomainLayer.SystemHandler;
import ServiceLayer.ViewStorePurchaseHistory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        SystemHandler.getInstance().login("noy");
        //open new store
        Store s = new Store("Lalin", "beauty products", SystemHandler.getInstance().getActiveUser(), new StoreOwning(null));
        s.addToInventory("Body Cream ocean", 40, Category.BeautyProducts, "Velvety and soft skin lotion with ocean scent", 50);
        s.addToInventory("Body Scrub musk", 50, Category.BeautyProducts, "Deep cleaning with natural salt crystals with a musk scent", 20);
        SystemHandler.getInstance().addToShoppingBasket("Lalin", "Body Cream ocean", 5);
        SystemHandler.getInstance().purchaseCart();
    }


    @AfterClass
    public static void clean() {

    }

    @Test
    public void valid() {
        String result = storePurchaseHistory.execute("Lalin");
        assertEquals("There is currently no stock of 3 jeans skirt products", result);
    }

}
