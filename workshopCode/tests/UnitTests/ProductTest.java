package UnitTests;

import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.Models.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProductTest {

    Product product;

    @BeforeClass
    public static void init() {
        PersistenceController.initiate(false);
    }

    @Before
    public void setUp() throws Exception {
        product = new Product("Shirt",null,"",40.0, "castro",1);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getKeyWords() {
        List<String> expected = new ArrayList<>();
        assertEquals(expected,product.getKeyWords());
        product.setDescription("a nice #shirt for you");
        expected.add("shirt");
        assertEquals(expected,product.getKeyWords());
        product.setDescription("a nice #shirt with a #toya printing");
        expected.add("toya");
        assertEquals(expected,product.getKeyWords());
        product.setDescription("#yolo #foryou");
        expected = new ArrayList<>();
        expected.add("yolo");
        expected.add("foryou");
        assertEquals(expected,product.getKeyWords());

    }
}