package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppointmentAgreement {

    private Set<User> waitingForResponse;
    private List<User> approved;
    private List<User> declined;
    private User theWaitingUser;
    private User theAppointerUser;


    public AppointmentAgreement(Set<User> waitingForResponse, User theAppointerUser) {

        this.waitingForResponse = waitingForResponse;
        this.approved = new ArrayList<>();
        this.declined = new ArrayList<>();
        this.theAppointerUser = theAppointerUser;
        waitingForResponse.remove(getTheAppointerUser());
    }


    public User getTheAppointerUser() {
        return theAppointerUser;
    }

    public void approve(User user){
        if(waitingForResponse.contains(user)){
            waitingForResponse.remove(user);
            approved.add(user);
        }
    }

    public void decline(User user){
        if(waitingForResponse.contains(user)){
            waitingForResponse.remove(user);
            declined.add(user);
        }
    }


    public Set<User> getWaitingForResponse() {
        return waitingForResponse;
    }

    public List<User> getApproved() {
        return approved;
    }

    public List<User> getDeclined() {
        return declined;
    }
}
