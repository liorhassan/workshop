package DomainLayer;

import CommunicationLayer.Controller;
import DataAccessLayer.PersistenceController;

import java.io.IOException;

public class starter {
    public static void main(String[] args) throws IOException {
        PersistenceController.initiate();
        Controller.start();
    }
}
