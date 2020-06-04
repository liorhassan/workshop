package DomainLayer.TradingSystem.Models;

import DomainLayer.TradingSystem.Category;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "products")
public class Product implements Serializable {

    @Id
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "storeName")
    private String storeName;


    @Column(name = "category")
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private int quantity;

    public Product() {}

    public Product(String name, Category category, String description, Double price, String storeName, int quantity) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.storeName = storeName;
        this.quantity = quantity;
    }

    public int getQuantity() { return this.quantity;}

    public void setQuantity(int newQnt) { this.quantity = newQnt; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getKeyWords(){
        Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
        Matcher mat = MY_PATTERN.matcher(description);
        List<String> strs=new ArrayList<>();
        while (mat.find()) {
            strs.add(mat.group(1));
        }
        return strs;
    }
}