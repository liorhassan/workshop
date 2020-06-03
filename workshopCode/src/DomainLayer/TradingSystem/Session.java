package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.User;

import java.util.List;
import java.util.UUID;

public class Session {

    private UUID session_id;

    public User getLoggedin_user() {
        return loggedin_user;
    }

    public void setLoggedin_user(User loggedin_user) {
        this.loggedin_user = loggedin_user;
    }

    private User loggedin_user;
    private List<Product> lastSearchResult;

    public boolean isAdminMode() {
        return adminMode;
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
    }

    private boolean adminMode;

    public Session() {
        session_id = UUID.randomUUID();
        loggedin_user = null;
        lastSearchResult = null;
        adminMode = false;
    }

    public UUID getSession_id() {
        return session_id;
    }

    public void clearSearchResults(){
        lastSearchResult.clear();
    }

    public void addToSearchResults(Product p){
        lastSearchResult.add(p);
    }

    public List<Product> getLastSearchResult() {
        return lastSearchResult;
    }
}
