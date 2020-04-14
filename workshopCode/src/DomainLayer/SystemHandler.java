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
    private ShoppingCart guestShoppingCart;


    private SystemHandler() {
        users = new HashMap<>();
        stores = new HashMap<>();
        adminsList = new ArrayList<>();
        activeUser = null;
        guestShoppingCart = new ShoppingCart(null);
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

    //function for handling UseCase 2.2
    public void register(String username) {
        if (emptyString(username))
            throw new IllegalArgumentException("Username cannot be empty");
        if (users.containsKey(username))
            throw new IllegalArgumentException("This username already exists in the system. Please choose a different one");
        users.put(username, new User());
    }

    //function for handling UseCase 2.5
    public List<Product> searchProducts(String name, Category category, String description){
        if(emptyString(name)&&category==null&&emptyString(description))
            throw new IllegalArgumentException("Must enter search parameter");
        List<Product> matching=  new ArrayList<>();
        for(Store s : stores.values()){
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

    //function for handling UseCase 2.5
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

    //function for handling UseCase 2.6
    public void addToShoppingBasket(String store, String product){
        if (emptyString(store) || emptyString(product))
            throw new IllegalArgumentException("Must enter store name and product name");
        if (!stores.containsKey(store))
            throw new IllegalArgumentException("The store doesn't exist in the trading system");
        if (!stores.get(store).checkIfProductAvailable(product))
            throw new IllegalArgumentException("The product isn't available in the store");
        activeUser.getShoppingCart().addProduct(product, stores.get(store));
    }
    // function for handling UseCase 2.3
    public void login(String username, boolean adminMode){
        if(activeUser != null){
            throw new IllegalArgumentException("first logout");
        }

        if (username == null || username.equals(""))                                                       //check legal input
            throw new IllegalArgumentException("Username or password cannot be empty");
        if (!users.containsKey(username))
            throw new IllegalArgumentException("This user is not registered");
        User user = users.get(username);
        if(adminMode){                                                                              //check if admin user
            if(adminsList.contains(user)){
                this.adminMode = true;
                activeUser = user;
            }
            else
                throw new IllegalArgumentException("you are should be admin");
            }
        else {
            this.adminMode = false;
            activeUser = user;
        }
    }

    //function for handling UseCase 3.1
    public String logout(){
        activeUser = null;
        return "You have been successfully logged out!";
    }

    //function for handling Use Case 2.7
    public String viewSoppingCart(){

        return activeUser.getShoppingCart().view();
    }



    // function for use case 2.7
    public String editShoppingCart(String storeName, String productName, int amount){
        if(emptyString(storeName) || emptyString(productName) || amount < 0 )
            throw new IllegalArgumentException("Must enter product name, store name and amount");
        Store store = stores.get(storeName);
        if(store == null)
            throw new IllegalArgumentException("This store doesn't exist");

        return activeUser.getShoppingCart().edit(store, productName, amount);

    }

    //function for handling Use Case 4.1
    public String updateInventory(String storeName, String productName, double productPrice, Category productCategory, String productDescription, int amount){
        if (emptyString(storeName) || emptyString(productName) || productCategory == null || emptyString(productDescription))
            throw new IllegalArgumentException("Must enter store name and product info");
        if (!stores.containsKey(storeName))
            throw new IllegalArgumentException("This store doesn't exist");
        if (!activeUser.hasEditPrivileges(storeName))
            throw new IllegalArgumentException("Must have editing privileges");
        Store s = stores.get(storeName);
        if (!s.hasProduct(productName)) {
            s.addToInventory(productName, productPrice, productCategory, productDescription);
            return "The product has been added";
        }
        else {
            s.updateInventory(productName, productPrice, productCategory, productDescription);
            return "The product has been updated";
        }
    }

    // function for handling Use Case 4.7
    public String removeManager(String username,String storename){
        if(emptyString(username) || emptyString(storename))
            throw new IllegalArgumentException("Must enter username and store name");
        Store store = stores.get(storename);
        if(store == null)
            throw new IllegalArgumentException("This store doesn't exist");
        User user = users.get(username);
        if(user == null)
            throw new IllegalArgumentException("This username doesn't exist");
        if(!store.isOwner(activeUser))
            throw new RuntimeException("You must be this store owner for this command");
        if(store.getAppointer(user) != activeUser)
            throw new RuntimeException("This username is not one of this store's managers appointed by you");
        store.removeManager(user);
        return "Manager removed successfully";
    }

    public String appointManager(String username, String storename){
        if(emptyString(username) || emptyString(storename))
            throw new IllegalArgumentException("Must enter username and store name");
        Store store = stores.get(storename);
        if(store == null)
            throw new IllegalArgumentException("This store doesn't exist");
        User appointed_user = users.get(username);
        if(appointed_user == null)
            throw new IllegalArgumentException("This username doesn't exist");
        if(!store.isOwner(activeUser))
            throw new RuntimeException("You must be this store owner for this command");
        if(store.getManagments().containsKey(appointed_user))
            throw new RuntimeException("This username is already one of the store's managers");
        store.addManager(appointed_user, activeUser);
        return "Username has been added as one of the store managers successfully";
    }

    private boolean emptyString(String arg){
        return arg == null || arg.equals("");
    }

    // function for handling Use Case 3.2 written by Nufar
    public String openNewStore(String storeName, String storeDescription) {
        if (storeName == null || storeDescription == null || storeName.equals("") || storeDescription.equals(""))
            throw new IllegalArgumentException("Must enter store name and description");
        if (stores.get(storeName) != null)
            throw new RuntimeException("Store name already exists, please choose a different one");
        Store newStore = new Store(storeName, storeDescription, this.activeUser);
        this.stores.put(storeName, newStore);
        return "The new store is now open!";
    }

    // function for handling Use Case 3.7 - written by Nufar
    public UserPurchaseHistory getUserPurchaseHistory() {
        if (activeUser == null)
            throw new RuntimeException("There is no active user");
        if (activeUser.getUsername() == null)
            throw new RuntimeException("Only subscribed users can view purchase history");
        return activeUser.getPurchaseHistory();
    }
}