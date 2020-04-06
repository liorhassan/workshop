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
    private HashMap<String,User> usersMap;
    private HashMap<String,Store> storeMap;
    private List<User> adminsList;


    private SystemHandler() {
        usersMap = new HashMap<>();
        storeMap = new HashMap<>();
        adminsList = new ArrayList<>();
        activeUser = null;
    }

    public User getUserByName(String username){
        return usersMap.get(username);
    }

    public Store getStoreByName(String storeName){
        return storeMap.get(storeName);
    }

    public User getActiveUser(){
        return activeUser;
    }

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