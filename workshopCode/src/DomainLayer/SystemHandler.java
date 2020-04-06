package DomainLayer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemHandler {
    private static SystemHandler ourInstance = new SystemHandler();
    public static SystemHandler getInstance() {
        return ourInstance;
    }

    private User activeUser;
    private boolean adminMode;
    private HashMap<String, User> users;
    private HashMap<String, Store> stores;
    private List<User> adminsList;


    private SystemHandler() {
        users = new HashMap<>();
        stores = new HashMap<>();
        adminsList = new ArrayList<>();
        activeUser = null;
    }

    public User getUserByName(String username) {
        return users.get(username);
    }

    public Store getStoreByName(String storeName) {
        return stores.get(storeName);
    }

    public User getActiveUser() {
        return activeUser;
    }

    public void register(String username) {
        if (username == "" || username == null)
            throw new IllegalArgumentException("Username cannot be empty");
        if (users.containsKey(username))
            throw new IllegalArgumentException("This username already exists in the system. Please choose a different one");
        users.put(username, new User());
    }
    /**
     * function for handling UseCase 3.1
     */
    public String logout(){
        activeUser = null;
        return "You have been successfully logged out!";
    }

    /**
     * function for handling UseCase 2.5
     */
    public List<Product> searchProducts(String name, Category category, String description){
        if(emptyString(name)&&category==null&&emptyString(description))
            throw new IllegalArgumentException("Must enter search parameter");
        List<Product> matching=  new ArrayList<>();
        for(Store s : storeMap.values()){
            for(Product p : s.getProducts()){
                if(!emptyString(name)&&!p.getName().contains(name))
                    continue;
                if(category!=null&&category!=p.getCategory())
                    continue;
                if(!emptyString(description)&&!p.getDescription().contains(description))
                    continue;
                matching.add(p);
            }
        }
        if(matching.size()==0)
            throw new RuntimeException("There are no products that match these parameters");
        return matching;
    }

    /**
     * function for handling UseCase 2.5
     */
    public List<Product> filterResults(List<Product> toFilter, Integer minPrice, Integer maxPrice, Category category){
        List<Product> matching = new ArrayList<>();
        for(Product p : toFilter){
            if(category!=null&&category!=p.getCategory())
                continue;
            if(minPrice!=null&&p.getPrice()<minPrice)
                continue;
            if(maxPrice!=null&&p.getPrice()>minPrice)
                continue;
            matching.add(p);
        }
        if(matching.size()==0)
            throw new RuntimeException("There are no products that match this search filter");
        return matching;
    }

    private boolean emptyString(String arg){
        return arg==null||arg=="";
    }
}