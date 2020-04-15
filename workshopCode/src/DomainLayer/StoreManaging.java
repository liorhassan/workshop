package DomainLayer;

public class StoreManaging {
    User manager;
    Store store;
    User appointer;

    public StoreManaging(User manager, Store store, User appointer) {
        this.manager = manager;
        this.store = store;
        this.appointer = appointer;
    }

    public User getAppointer() {
        return appointer;
    }
}
