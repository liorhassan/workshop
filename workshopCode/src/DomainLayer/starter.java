package DomainLayer;

import CommunicationLayer.Controller;
import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.SystemFacade;

import java.io.IOException;

public class starter {
    public static void main(String[] args) throws IOException {
        PersistenceController.initiate();
        (new SystemInitHandler()).initSystem();
        Controller.start();
        SystemFacade.getInstance().init();
    }
}
