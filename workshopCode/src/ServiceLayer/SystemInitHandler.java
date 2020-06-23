package ServiceLayer;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

public class SystemInitHandler {
    private final String INIT_FILE_FOLDER = "./configurations/";
    private SessionHandler sessionHandler = new SessionHandler();
    private SearchHandler searchHandler = new SearchHandler();
    private ShoppingCartHandler shoppingCartHandler = new ShoppingCartHandler();
    private StoreHandler storeHandler = new StoreHandler();
    private StoreManagerHandler storeManagerHandler = new StoreManagerHandler();
    private UsersHandler usersHandler = new UsersHandler();
    private ViewInfoHandler viewInfoHandler = new ViewInfoHandler();
    private ViewPurchaseHistoryHandler viewPurchaseHistoryHandler = new ViewPurchaseHistoryHandler();

    public void initSystem(String fileName){
        UUID session_id = sessionHandler.openNewSession();
        File initFile = new File(INIT_FILE_FOLDER + fileName);
        String s = initFile.getAbsolutePath();
        try{
            Scanner scn = new Scanner(initFile);
            while(scn.hasNextLine()){
                executeCommand(scn.nextLine(), session_id);
            }
        }
        catch (Exception e){
            usersHandler.register("Admin159","951");
            usersHandler.addAdmin("Admin159");
        }
        sessionHandler.closeSession(session_id);
    }

    private void executeCommand(String command, UUID session_id){
        String[] splitedCommand =  command.split(", ");
        if (splitedCommand[0].equals("Register"))
            usersHandler.register(splitedCommand[1], splitedCommand[2]);
        else if (splitedCommand[0].equals("Login"))
            usersHandler.login(session_id, splitedCommand[1], splitedCommand[2], false);
        else if (splitedCommand[0].equals("Add Admin"))
            usersHandler.addAdmin(splitedCommand[1]);
        else if (splitedCommand[0].equals("Logout"))
            usersHandler.logout(session_id);
        else if (splitedCommand[0].equals("Add To ShoppingBasket"))
            shoppingCartHandler.AddToShoppingBasket(session_id, splitedCommand[1], splitedCommand[2], Integer.parseInt(splitedCommand[3]));
        else if (splitedCommand[0].equals("Purchase Cart"))
            shoppingCartHandler.purchaseCart(session_id);
        else if (splitedCommand[0].equals("Update Inventory"))
            storeHandler.UpdateInventory(session_id, splitedCommand[1], splitedCommand[2], Double.parseDouble(splitedCommand[3]), splitedCommand[4], splitedCommand[5], Integer.parseInt(splitedCommand[6]));
        else if (splitedCommand[0].equals("Open New Store"))
            storeHandler.openNewStore(session_id, splitedCommand[1], splitedCommand[2]);
        else if (splitedCommand[0].equals("Add Store Owner"))
            storeHandler.addStoreOwner(session_id, splitedCommand[1], splitedCommand[2]);
        else if (splitedCommand[0].equals("Response To Appointment Request"))
            storeHandler.responseToAppointmentRequest(session_id, splitedCommand[1], splitedCommand[2], Boolean.parseBoolean(splitedCommand[3]));
        else if (splitedCommand[0].equals("Add Store Manager"))
            storeManagerHandler.addStoreManager(session_id, splitedCommand[1], splitedCommand[2]);
        else if (splitedCommand[0].equals("Remove Store Manager"))
            storeManagerHandler.removeStoreManager(session_id, splitedCommand[1], splitedCommand[2]);
        else if (splitedCommand[0].equals("Edit Manager Permissions"))
            storeManagerHandler.editManagerPermissions(session_id, splitedCommand[1], Arrays.asList(Arrays.copyOfRange(splitedCommand, 3, splitedCommand.length)), splitedCommand[2]);
        else{
            System.out.println("Illegal command: "+splitedCommand[0]);
            throw new IllegalArgumentException();
        }
    }
}
