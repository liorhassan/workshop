package AcceptanceTests;

import DomainLayer.*;
import ServiceLayer.ViewInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class UC2_4 {

    private ViewInfo viewInfo;

    @Before
    public void setUp(){
        this.viewInfo = new ViewInfo();
    }

    @BeforeClass
    public static void init(){
        User user = SystemHandler.getInstance().getActiveUser();
        Store s = new Store("Lalin", "beauty products", user, new StoreOwning(null));
        SystemHandler.getInstance().getStores().put("Lalin", s);
        s.addToInventory("Body Cream ocean", 40, Category.BeautyProducts, "Velvety and soft skin lotion with ocean scent", 1);
        s.addToInventory("Body Scrub musk", 50, Category.BeautyProducts, "Deep cleaning with natural salt crystals with a musk scent", 1);
    }

    @Test
    public void valid() {
        String result = viewInfo.execute("Lalin");
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
        String result = viewInfo.execute("Swear");
        assertEquals("This store doesn't exist in this trading system", result);
    }

    @Test
    public void storeNameEmpty(){
        String result = viewInfo.execute("");
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
