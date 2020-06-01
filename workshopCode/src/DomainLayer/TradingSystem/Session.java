package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.User;

import java.util.List;
import java.util.UUID;

public class Session {

    private UUID session_id;
    private User loggedin_user;
    private List<Product> lastSearchResult;
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
