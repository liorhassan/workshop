package DomainLayer.TradingSystem;

import javax.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @Column(name="id", unique = true)
    @GeneratedValue
    private int id;

    @Column(name = "action")
    private String allowedAction;

    @Column(name = "storeName")
    private String storeName;

    @Column(name = "appointee")
    private String appointee;

    public Permission(){}
    public Permission(String action)
    {
        this.allowedAction = action;
    }

    public String getAllowedAction(){
        return this.allowedAction;
    }

    public void setStoreName(String name){
        this.storeName = name;
    }

    public String getStoreName(){
        return this.storeName;
    }

    public void setAppointee(String name){
        this.appointee = name;
    }

    public String getAppointee(){
        return this.appointee;
    }

}
