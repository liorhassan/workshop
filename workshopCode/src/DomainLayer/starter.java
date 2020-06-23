package DomainLayer;

import CommunicationLayer.Controller;
import DataAccessLayer.PersistenceController;
import DomainLayer.TradingSystem.SystemFacade;
import ServiceLayer.SystemInitHandler;

import java.io.IOException;

public class starter {
    public static void main(String[] args) throws IOException {
        PersistenceController.initiate(true);
        SystemFacade.getInstance().init();
        (new SystemInitHandler()).initSystem("init.ini");
        Controller.start();
    }
}
