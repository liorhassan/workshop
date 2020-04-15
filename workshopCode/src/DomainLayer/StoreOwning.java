package DomainLayer;

public class StoreOwning {

    User appointer;

    // first owner constructor
    public StoreOwning() {}

    public StoreOwning(User appointer) {
        this.appointer = appointer;
    }

    public User getAppointer() {
        return appointer;
    }

}
