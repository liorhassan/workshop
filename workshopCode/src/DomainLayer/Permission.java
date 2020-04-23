package DomainLayer;

public class Permission {

    private String allowedAction;

    public Permission(String action){
        this.allowedAction = action;
    }

    public String getAllowedAction(){
        return this.allowedAction;
    }
}
