package AcceptanceTests;

import DomainLayer.*;
import DomainLayer.Models.Store;
import DomainLayer.Models.User;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import ServiceLayer.ViewInfoHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class UC2_4 {

    private ViewInfoHandler viewInfo;
    private static StoreHandler storeHandler;
    private static UsersHandler usersHandler;

    @Before
    public void setUp(){
        this.viewInfo = new ViewInfoHandler();
        this.storeHandler = new StoreHandler();
        this.usersHandler = new UsersHandler();
    }

    @BeforeClass
    public static void init(){
        usersHandler.register("noy", "1234");
        usersHandler.login("noy", "1234", false);
        storeHandler.openNewStore("Lalin", "beauty products");
        storeHandler.UpdateInventory("Lalin", "Body Cream ocean", 40, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 1);
        storeHandler.UpdateInventory("Lalin", "Body Scrub musk", 50, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 1);
    }


    @AfterClass
    public static void clean() {
        usersHandler.logout();
        usersHandler.resetUsers();
        storeHandler.resetStores();
    }

    @Test
    public void valid() {
        String result = viewInfo.viewStoreinfo("Lalin");
        String expectefResult = "Store name: Lalin description: beauty products" +
                "\n products:\n" +
                "  Body Cream ocean- 40$\n  Body Scrub musk- 50$\n";
        assertEquals(expectefResult, result);

        result = viewInfo.viewProductInfo("Body Cream ocean", "Lalin");
        expectefResult = "Body Cream ocean: Velvety and soft skin lotion with ocean scent\nprice: 40$";
        assertEquals(expectefResult, result);
    }

    @Test
    public void storeDoesntExist(){
        String result = viewInfo.viewStoreinfo("Swear");
        assertEquals("This store doesn't exist in this trading system", result);
    }

    @Test
    public void storeNameEmpty(){
        String result = viewInfo.viewStoreinfo("");
        assertEquals("The store name is invalid", result);
    }

    @Test
    public void productDoesntExist(){
        String result = viewInfo.viewProductInfo("Body oil", "Lalin");
        assertEquals("This product is not available for purchasing in this store", result);
    }

    @Test
    public void productNameEmpty(){
        String result = viewInfo.viewProductInfo("", "Lalin");
        assertEquals("The product name is invalid", result);
    }
}
