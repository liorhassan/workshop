package ServiceLayer;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.SystemFacade;

import java.util.concurrent.ConcurrentHashMap;

public class PurchaseIntegation {

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
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub ocean", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new UsersHandler()).logout();
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("zuzu", "1234");

    }

    @Test
    public void validPurchaseCheck(){
        (new UsersHandler()).login("noy", "1234", false);

        (new ShoppingCartHandler()).AddToShoppingBasket("Castro", "Michael Kors bag", 2);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        ConcurrentHashMap< Product, Integer> inv = store.getInventory();
        int sizePurchaseHBefore = store.getPurchaseHistory().getStorePurchases().size();

        int amountBefore = inv.get(store.getProductByName("Michael Kors bag"));
        String result = (new ShoppingCartHandler()).purchaseCart();
        assertEquals("{\"SUCCESS\":\"Purchasing completed successfully\"}", result);

        int amountAfter = inv.get(store.getProductByName("jeans skirt"));
        assertEquals(amountBefore-2, amountAfter);
        int sizePurchaseHAfter = store.getPurchaseHistory().getStorePurchases().size();
        assertEquals(sizePurchaseHBefore+1, sizePurchaseHAfter);
        (new UsersHandler()).logout();
    }

    @Test
    public void ExtSysFailPurchaseCheck(){
        (new UsersHandler()).login("noy", "1234", false);

        (new ShoppingCartHandler()).AddToShoppingBasket("Castro", "white T-shirt", 2);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        ConcurrentHashMap< Product, Integer> inv = store.getInventory();
        int sizePurchaseHBefore = store.getPurchaseHistory().getStorePurchases().size();

        int amountBefore = inv.get(store.getProductByName("white T-shirt"));

        try{
            String result = (new ShoppingCartHandler()).purchaseCart();
        }
        catch (Exception e){
            assertEquals("Payment failed", e.getMessage());

        }
        int amountAfter = inv.get(store.getProductByName("jeans skirt"));
        assertEquals(amountBefore, amountAfter);

        int sizePurchaseHAfter = store.getPurchaseHistory().getStorePurchases().size();
        assertEquals(sizePurchaseHBefore, sizePurchaseHAfter);

        (new UsersHandler()).logout();
    }

    @Test
    public void notAvailableAmountPurchaseCheck(){
        (new UsersHandler()).login("noy", "1234", false);

        (new ShoppingCartHandler()).AddToShoppingBasket("Castro", "white T-shirt", 100);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        ConcurrentHashMap< Product, Integer> inv = store.getInventory();
        int sizePurchaseHBefore = store.getPurchaseHistory().getStorePurchases().size();

        int amountBefore = inv.get(store.getProductByName("white T-shirt"));

        try{
            String result = (new ShoppingCartHandler()).purchaseCart();
        }
        catch (Exception e){
            assertEquals("Payment failed", e.getMessage());

        }
        int amountAfter = inv.get(store.getProductByName("jeans skirt"));
        assertEquals(amountBefore, amountAfter);

        int sizePurchaseHAfter = store.getPurchaseHistory().getStorePurchases().size();
        assertEquals(sizePurchaseHBefore, sizePurchaseHAfter);

        (new UsersHandler()).logout();
    }



}
