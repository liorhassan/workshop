package AcceptanceTests;

import ServiceLayer.ShoppingCartHandler;
import ServiceLayer.StoreHandler;
import ServiceLayer.UsersHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.json.simple.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class UC4_2 {

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
        (new StoreHandler()).openNewStore("BUG", "digital products");
        (new StoreHandler()).UpdateInventory("Lalin", "Body Cream ocean", 40.0, "BeautyProducts", "Velvety and soft skin lotion with ocean scent", 50);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub musk", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new StoreHandler()).UpdateInventory("Castro", "white T-Shirt", 50.0, "Clothing", "white v t-shirt", 1000);
        (new StoreHandler()).UpdateInventory("Castro", "jeans", 120.0, "Clothing", "blue jeans", 1000);
        (new StoreHandler()).UpdateInventory("Castro", "black skirt", 100.0, "Clothing", "black mini-skirt", 1000);
        (new StoreHandler()).UpdateInventory("Castro", "shoes", 100.0, "Clothing", "shoes", 50);
        (new StoreHandler()).UpdateInventory("BUG", "Ipad", 1000.0, "Clothing", "ipad 128 G", 50);
        (new StoreHandler()).UpdateInventory("BUG", "hp laptop", 3000.0, "Clothing", "white laptop", 50);
        (new StoreHandler()).UpdateInventory("BUG", "Dell laptop", 3000.0, "Clothing", "white laptop", 50);



        (new StoreHandler()).addDiscountCondProductAmount("Castro", "black skirt", 50, 1);
        (new StoreHandler()).addDiscountCondBasketProducts("Castro", "white T-Shirt","jeans", 50, 1);
        (new StoreHandler()).addDiscountRevealedProduct("Castro", "white T-Shirt", 20);
        (new StoreHandler()).addDiscountCondProductAmount("Castro", "shoes", 50, 1);


        (new UsersHandler()).logout();
        (new UsersHandler()).register("zuzu", "1234");
    }

    @AfterClass
    public static void clean(){
        (new UsersHandler()).resetUsers();
        (new StoreHandler()).resetStores();
    }
    @Test
    public void revealedDiscountForProduct() {
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub vanil", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);

        (new StoreHandler()).addDiscountRevealedProduct("Lalin","Body Scrub vanil", 50 );
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub vanil", 2);
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
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub pear", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);
        (new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 100, true);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub pear", 3);
        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("135.0", result);
        shoppingCartHandler.purchaseCart();
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub pear", 1);

        result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("50.0", result);
        shoppingCartHandler.purchaseCart();
        (new UsersHandler()).logout();
    }


    @Test
    public void onProductAmountDiscountForBasket() {
        (new UsersHandler()).login("toya", "1234", false);
        (new StoreHandler()).UpdateInventory("Lalin", "Body Scrub apple", 50.0, "BeautyProducts", "Deep cleaning with natural salt crystals with a musk scent", 50);

        (new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 2, false);
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub apple", 1);
        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("50.0", result);
        shoppingCartHandler.purchaseCart();
        shoppingCartHandler.AddToShoppingBasket("Lalin", "Body Scrub apple", 3);
        result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("135.0", result);
        shoppingCartHandler.purchaseCart();

        //shoppingCartHandler.editCart("Lalin", "Body Scrub apple", 0);
        (new UsersHandler()).logout();
    }

    @Test
    public void policyXORtest() {
        (new UsersHandler()).login("toya", "1234", false);

        //(new StoreHandler()).addDiscountForBasketPriceOrAmount("Lalin",  10, 2, false);
        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-Shirt", 4);
        shoppingCartHandler.AddToShoppingBasket("Castro", "jeans", 1);
        // rev t=shirt xor condJeans
        String message;

        JSONObject jsonObj1 = new JSONObject();
        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObjXOR = new JSONObject();

        jsonObj1.put("type", "simple");
        jsonObj1.put("discountId", 2);

        jsonObj2.put("type", "simple");
        jsonObj2.put("discountId", 1);

        jsonObjXOR.put("type", "compose");
        jsonObjXOR.put("operand1", jsonObj1);
        jsonObjXOR.put("operand2", jsonObj2);
        jsonObjXOR.put("operator", "XOR");
        jsonObjXOR.put("store", "Castro");

        message = jsonObjXOR.toString();
        (new StoreHandler()).addDiscountPolicy(message);

        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("220.0", result);
        shoppingCartHandler.purchaseCart();
       // shoppingCartHandler.editCart("Castro", "jeans", 0);
      //  shoppingCartHandler.editCart("Castro", "white T-Shirt", 0);
        (new StoreHandler()).removeDiscountPolicies("Castro");
        //assertEquals("120", result);
        (new UsersHandler()).logout();
    }

    @Test
    public void policyIFtest() {
        (new UsersHandler()).login("toya", "1234", false);

        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-Shirt", 4);
        shoppingCartHandler.AddToShoppingBasket("Castro", "black skirt", 1);
        //if skirt then rev t-shirt

        String message;

        JSONObject jsonObj1 = new JSONObject();
        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObjIF = new JSONObject();

        jsonObj1.put("type", "simple");
        jsonObj1.put("discountId", 0);

        jsonObj2.put("type", "simple");
        jsonObj2.put("discountId", 2);

        jsonObjIF.put("type", "compose");
        jsonObjIF.put("operand1", jsonObj1);
        jsonObjIF.put("operand2", jsonObj2);
        jsonObjIF.put("operator", "IF_THEN");
        jsonObjIF.put("store", "Castro");

        message = jsonObjIF.toString();
        (new StoreHandler()).addDiscountPolicy(message);

        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("300.0", result);
        shoppingCartHandler.purchaseCart();
        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-Shirt", 4);
        shoppingCartHandler.AddToShoppingBasket("Castro", "black skirt", 2);
        result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("310.0", result);
        shoppingCartHandler.purchaseCart();
        //hoppingCartHandler.editCart("Castro", "black skirt", 0);
        //shoppingCartHandler.editCart("Castro", "white T-Shirt", 0);
        (new StoreHandler()).removeDiscountPolicies("Castro");
        //assertEquals("120", result);
        (new UsersHandler()).logout();
    }

    @Test
    public void policyANDtest() {
        (new UsersHandler()).login("toya", "1234", false);

        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-Shirt", 4);
        shoppingCartHandler.AddToShoppingBasket("Castro", "black skirt", 2);
        shoppingCartHandler.AddToShoppingBasket("Castro", "shoes", 2);
        //if skirt and jeans then shoes
        String message;

        JSONObject jsonObj1 = new JSONObject();
        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObj3 = new JSONObject();
        JSONObject jsonObjIF = new JSONObject();
        JSONObject jsonObjAND = new JSONObject();

        jsonObj1.put("type", "simple");
        jsonObj1.put("discountId", 0);

        jsonObj2.put("type", "simple");
        jsonObj2.put("discountId", 1);

        jsonObj3.put("type", "simple");
        jsonObj3.put("discountId", 3);

        jsonObjAND.put("type", "compose");
        jsonObjAND.put("operand1", jsonObj1);
        jsonObjAND.put("operand2", jsonObj2);
        jsonObjAND.put("operator", "AND");
        jsonObjAND.put("store", "Castro");

        jsonObjIF.put("type", "compose");
        jsonObjIF.put("operand1", jsonObjAND);
        jsonObjIF.put("operand2", jsonObj3);
        jsonObjIF.put("operator", "IF_THEN");
        jsonObjIF.put("store", "Castro");

        message = jsonObjIF.toString();
        (new StoreHandler()).addDiscountPolicy(message);

        String result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("460.0", result);
        shoppingCartHandler.purchaseCart();
        shoppingCartHandler.AddToShoppingBasket("Castro", "white T-Shirt", 4);
        shoppingCartHandler.AddToShoppingBasket("Castro", "black skirt", 2);
        shoppingCartHandler.AddToShoppingBasket("Castro", "jeans", 1);
        shoppingCartHandler.AddToShoppingBasket("Castro", "shoes", 2);
        result = shoppingCartHandler.getCartTotalPrice();
        assertEquals("520.0", result);
        shoppingCartHandler.purchaseCart();
        (new StoreHandler()).removeDiscountPolicies("Castro");
        //assertEquals("120", result);
        (new UsersHandler()).logout();
    }

    public void purchasePolicyProductTest(){
        (new UsersHandler()).login("toya", "1234", false);

        shoppingCartHandler.AddToShoppingBasket("BUG", "hp laptop", 1);
        (new StoreHandler()).addPurchasePolicyProduct("BUG", "ipad", 1, false,true);
        try{
            shoppingCartHandler.purchaseCart();
        }
        catch (Exception e){
            assertEquals("Your purchase doesn’t match the store’s policy", e.getMessage());
            (new StoreHandler()).removePurchasePolicies("BUG");

        }
        (new StoreHandler()).removePurchasePolicies("BUG");

    }

    public void purchasePolicyStoreTest(){
        (new UsersHandler()).login("toya", "1234", false);

        shoppingCartHandler.AddToShoppingBasket("BUG", "hp laptop", 1);
        shoppingCartHandler.AddToShoppingBasket("BUG", "ipad", 2);

        (new StoreHandler()).addPurchasePolicyStore("BUG",  4, false,true);
        try{
            shoppingCartHandler.purchaseCart();
        }
        catch (Exception e){
            assertEquals("Your purchase doesn’t match the store’s policy", e.getMessage());
            (new StoreHandler()).removePurchasePolicies("BUG");
        }
        (new StoreHandler()).removePurchasePolicies("BUG");

    }

    public void purchasePolicyXorTest(){
        (new UsersHandler()).login("toya", "1234", false);

        shoppingCartHandler.AddToShoppingBasket("BUG", "hp laptop", 1);
        shoppingCartHandler.AddToShoppingBasket("BUG", "Dell laptop", 1);

        (new StoreHandler()).addPurchasePolicyProduct("BUG", "hp laptop", 1, false,true);
        (new StoreHandler()).addPurchasePolicyProduct("BUG", "Dell laptop", 1, false,true);

        JSONObject jsonObj1 = new JSONObject();
        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObjXOR = new JSONObject();

        jsonObj1.put("type", "simple");
        jsonObj1.put("policyId", 0);

        jsonObj2.put("type", "simple");
        jsonObj2.put("policyId", 1);

        jsonObjXOR.put("type", "compose");
        jsonObjXOR.put("operand1", jsonObj1);
        jsonObjXOR.put("operand2", jsonObj2);
        jsonObjXOR.put("operator", "XOR");
        jsonObjXOR.put("store", "BUG");
        try{
            shoppingCartHandler.purchaseCart();
        }
        catch (Exception e){
            assertEquals("Your purchase doesn’t match the store’s policy", e.getMessage());
            (new StoreHandler()).removePurchasePolicies("BUG");
        }
        (new StoreHandler()).removePurchasePolicies("BUG");
    }
}
