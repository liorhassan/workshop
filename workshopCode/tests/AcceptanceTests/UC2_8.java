package AcceptanceTests;

import DomainLayer.*;
import ServiceLayer.PurchaseProducts;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class UC2_8 {

    private PurchaseProducts purchaseProducts;

    @Before
    public void setUp(){
        this.purchaseProducts = new PurchaseProducts();
    }

    @BeforeClass
    public static void init(){
        SystemHandler.getInstance().register("noy");
        SystemHandler.getInstance().login("noy");
        User user = SystemHandler.getInstance().getActiveUser();
        Store s1 = new Store("Castro", "clothes for women and men", user, new StoreOwning(null));
        Store s2 = new Store("Lalin", "beauty products", user, new StoreOwning(null));
        SystemHandler.getInstance().getStores().put("Castro", s1);
        SystemHandler.getInstance().getStores().put("Lalin", s2);
        s1.addToInventory("white T-shirt", 25, Category.Clothing, "white T-shirt for men", 3);
        s1.addToInventory("jeans skirt", 50, Category.Clothing, "mini jeans skirt for women", 2);
        s2.addToInventory("Body Cream ocean", 40, Category.BeautyProducts, "Velvety and soft skin lotion with ocean scent", 50);
        s2.addToInventory("Body Scrub musk", 50, Category.BeautyProducts, "Deep cleaning with natural salt crystals with a musk scent", 20);
        SystemHandler.getInstance().addToShoppingBasket("Castro", "jeans skirt", 2);
        SystemHandler.getInstance().addToShoppingBasket("Lalin", "Body Cream ocean", 5);
    }

    @AfterClass
    public static void clean(){
        SystemHandler.getInstance().logout();
        SystemHandler.getInstance().setUsers(new HashMap<>());
        SystemHandler.getInstance().getStores().remove("Castro");
        SystemHandler.getInstance().getStores().remove("Castro");
    }

    @Test
    public void valid() {
        String result = purchaseProducts.execute();
        assertEquals("Purchasing completed successfully", result);
    }

    @Test
    public void emptyCart() {
        SystemHandler.getInstance().getActiveUser().emptyCart();
        String result = purchaseProducts.execute();
        assertEquals("The shopping cart is empty", result);
    }

    @Test
    public void productNotInStock() {
        SystemHandler.getInstance().addToShoppingBasket("Castro", "jeans skirt", 3);
        String result = purchaseProducts.execute();
        assertEquals("There is currently no stock of 3 jeans skirt products", result);
    }

}
