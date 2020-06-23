package DomainLayer;

import CommunicationLayer.Controller;
import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SystemInitHandler;

import java.io.IOException;
import java.sql.SQLException;

public class starter {
    public static void main(String[] args) throws IOException, SQLException {
        PersistenceController.initiate(true);
        SystemFacade.getInstance().init();
        (new SystemInitHandler()).initSystem();
        Controller.start();
    }
}
