package IntegrationTests;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

public class PurchaseIntegation {

    @BeforeClass
    public static void init() throws SQLException {
        (new UsersHandler()).register("toya", "1234");
        UUID se = SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"toya", "1234", false);

        (new StoreHandler()).openNewStore(se,"Castro", "clothes for women and men");
        (new StoreHandler()).UpdateInventory(se,"Castro", "white T-shirt", 5.0, "Clothing", "white T-shirt for men", 50);
        (new StoreHandler()).UpdateInventory(se,"Castro", "jeans skirt", 50.0, "Clothing", "mini jeans skirt for women", 50);
        (new UsersHandler()).logout(se);
        (new UsersHandler()).register("noy", "1234");
        (new UsersHandler()).register("maor", "1234");
        (new UsersHandler()).register("rachel", "1234");
        (new UsersHandler()).register("zuzu", "1234");

    }

    @AfterClass
    public static void clean() throws SQLException {
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }

    @Test
    public void validPurchaseCheck() throws SQLException {
        UUID se = SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"noy", "1234", false);

        SystemFacade.getInstance().addToShoppingBasket(se,"Castro", "white T-shirt", 2);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        ConcurrentHashMap< Product, Integer> inv = store.getInventory();
        int sizePurchaseHBefore = store.getPurchaseHistory().getStorePurchases().size();

        int amountBefore = inv.get(store.getProductByName("white T-shirt"));

        String result = (new ShoppingCartHandler()).purchaseCart(se);
        assertEquals("{\"SUCCESS\":\"Purchasing completed successfully\"}", result);

        int amountAfter = inv.get(store.getProductByName("jeans skirt"));
        assertEquals(amountBefore-2, amountAfter);
        int sizePurchaseHAfter = store.getPurchaseHistory().getStorePurchases().size();
        assertEquals(sizePurchaseHBefore+1, sizePurchaseHAfter);
        SystemFacade.getInstance().logout(se);
    }

    @Test
    public void ExtSysFailPurchaseCheck() throws SQLException {
        UUID se = SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"noy", "1234", false);

        SystemFacade.getInstance().addToShoppingBasket(se,"Castro", "Michael Kors bag", 2);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        ConcurrentHashMap<Product, Integer> inv = store.getInventory();
        int sizePurchaseHBefore = store.getPurchaseHistory().getStorePurchases().size();

        int amountBefore = inv.get(store.getProductByName("Michael Kors bag"));

        try{
            String result = (new ShoppingCartHandler()).purchaseCart(se);
        }
        catch (Exception e){
            assertEquals("Payment failed", e.getMessage());

        }
        int amountAfter = inv.get(store.getProductByName("jeans skirt"));
        assertEquals(amountBefore, amountAfter);

        int sizePurchaseHAfter = store.getPurchaseHistory().getStorePurchases().size();
        assertEquals(sizePurchaseHBefore, sizePurchaseHAfter);

        SystemFacade.getInstance().logout(se);
    }

    @Test
    public void notAvailableAmountPurchaseCheck() throws SQLException {
        UUID se = SystemFacade.getInstance().createNewSession();

        (new UsersHandler()).login(se,"noy", "1234", false);

        SystemFacade.getInstance().addToShoppingBasket(se,"Castro", "white T-shirt", 100);
        Store store = SystemFacade.getInstance().getStoreByName("Castro");
        ConcurrentHashMap< Product, Integer> inv = store.getInventory();
        int sizePurchaseHBefore = store.getPurchaseHistory().getStorePurchases().size();

        int amountBefore = inv.get(store.getProductByName("white T-shirt"));

        try{
            String result = (new ShoppingCartHandler()).purchaseCart(se);
        }
        catch (Exception e){
            assertEquals("Payment failed", e.getMessage());

        }
        int amountAfter = inv.get(store.getProductByName("jeans skirt"));
        assertEquals(amountBefore, amountAfter);

        int sizePurchaseHAfter = store.getPurchaseHistory().getStorePurchases().size();
        assertEquals(sizePurchaseHBefore, sizePurchaseHAfter);

        SystemFacade.getInstance().logout(se);
    }



}
