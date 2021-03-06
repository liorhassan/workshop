package ServiceLayer;

import DomainLayer.TradingSystem.SystemFacade;

import java.util.UUID;

public class SessionHandler {
    public UUID openNewSession(){
        return SystemFacade.getInstance().createNewSession();
    }

    public String closeSession(UUID session_id){
        try {
            SystemFacade.getInstance().closeSession(session_id);
            return "Session Ended";
        }
        catch (Exception e){
            return  e.getMessage();
        }
    }
}
