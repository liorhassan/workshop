package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.User;
import org.json.simple.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "adminstats")
public class AdministrativeStatistics {

    @Column(name = "guestCount")
    private int guestCount;

    @Column(name = "subscribedCount")
    private int subscribedCount;

    @Column(name = "managerCount")
    private int managerCount;

    @Column(name = "ownerCount")
    private int ownerCount;


    @Id
    @Column(name = "date", unique = true)
    private Date date;

    public AdministrativeStatistics(){
        guestCount = 0;
        subscribedCount = 0;
        managerCount = 0;
        ownerCount = 0;
        date = new Date(new java.util.Date().getTime());
    }

    public String handleConnection(Session se){
        User newLog = se.getLoggedin_user();
        if(newLog.getUsername() == null) {
            guestCount++;
            return "New Guest Connection";
        }
        else if(newLog.getStoreOwnings().size() > 0) {
            ownerCount++;
            return "New Owner Connection: " + newLog.getUsername();
        }
        else if(newLog.getStoreManagements().size() > 0) {
            managerCount++;
            return "New Manager Connection: " + newLog.getUsername();
        }
        else {
            subscribedCount++;
            return "New Subscribed Connection: " + newLog.getUsername();
        }
    }

    public JSONObject getStatistics(){
        JSONObject output = new JSONObject();
        output.put("date", date.toString());
        output.put("guestCount", guestCount);
        output.put("subscribedCount", subscribedCount);
        output.put("managerCount", managerCount);
        output.put("ownerCount", ownerCount);
        return output;
    }

    public Date getDate(){
        return date;
    }

}
