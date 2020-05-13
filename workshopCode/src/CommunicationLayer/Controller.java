package CommunicationLayer;


import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import ServiceLayer.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Controller{

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8080;
    //Handlers
    private static UsersHandler usersHandler = new UsersHandler();
    private static SearchHandler searchHandler = new SearchHandler();
    private static ShoppingCartHandler cartHandler = new ShoppingCartHandler();
    private static StoreHandler storeHandler = new StoreHandler();
    private static StoreManagerHandler storeManagerHandler = new StoreManagerHandler();
    private static ViewInfoHandler viewInfoHandler = new ViewInfoHandler();
    private static ViewPurchaseHistoryHandler purchaseHistoryHandler = new ViewPurchaseHistoryHandler();

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int STATUS_OK = 200;
    private static final int STATUS_NOT_OK = 300;
    private static final int STATUS_METHOD_NOT_ALLOWED = 400;

    public static void main(String[] args) throws IOException{
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 1);

        //-----------------------------------------UsersHandler cases------------------------------------------
        server.createContext("/tradingSystem/login", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"login\":{\"username\":\"noy\", \"password\":\"123\", \"adminMode\":\"true\"}}");
                if(requestJson.containsKey("login")) {
                    JSONObject login = (JSONObject)requestJson.get("login");
                    String username = (login.containsKey("username")) ? (String)login.get("username") : null;
                    String password = (login.containsKey("password")) ? (String)(login.get("password")) : null;
                    boolean adminMode = (login.containsKey("adminMode")) ? (((String)(login.get("adminMode"))).equals("true") ? true : false) : false;
                    String response = usersHandler.login(username, password, adminMode);
                    headers.set("login", String.format("application/json; charset=%s", UTF8));
                    final byte[] ResponseBytes = response.getBytes(UTF8);
                    he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                    he.getResponseBody().write(ResponseBytes);
                }
                else{
                    //???????
                    he.sendResponseHeaders(STATUS_NOT_OK, 0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        server.createContext("/tradingSystem/register", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"register\":{\"username\":\"noy\", \"password\":\"123\"}}");
                if(requestJson.containsKey("register")) {
                    JSONObject register = (JSONObject)requestJson.get("register");
                    String username = (register.containsKey("username")) ? (String)register.get("username") : null;
                    String password = (register.containsKey("password")) ? (String)(register.get("password")) : null;
                    String response = usersHandler.register(username, password);
                    headers.set("register", String.format("application/json; charset=%s", UTF8));
                    final byte[] ResponseBytes = response.getBytes(UTF8);
                    he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                    he.getResponseBody().write(ResponseBytes);
                }
                else{
                    //???????
                    he.sendResponseHeaders(STATUS_NOT_OK, 0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        server.createContext("/tradingSystem/logout", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"logout\": \"\"}");
                if(requestJson.containsKey("logout")) {
                    String response = usersHandler.logout();
                    headers.set("logout", String.format("application/json; charset=%s", UTF8));
                    final byte[] ResponseBytes = response.getBytes(UTF8);
                    he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                    he.getResponseBody().write(ResponseBytes);
                }
                else{
                    //???????
                    he.sendResponseHeaders(STATUS_NOT_OK, 0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        server.createContext("/tradingSystem/addAdmin", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"addAdmin\": {\"username\":\"noy\"}}");
                if(requestJson.containsKey("addAdmin")) {
                    JSONObject addAdmin = (JSONObject)requestJson.get("addAdmin");
                    String username = (addAdmin.containsKey("username")) ? (String)addAdmin.get("username") : null;
                    String response = usersHandler.addAdmin(username);
                    headers.set("addAdmin", String.format("application/json; charset=%s", UTF8));
                    final byte[] ResponseBytes = response.getBytes(UTF8);
                    he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                    he.getResponseBody().write(ResponseBytes);
                }
                else{
                    //???????
                    he.sendResponseHeaders(STATUS_NOT_OK, 0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //-----------------------------------------StoreHandler cases------------------------------------------
        server.createContext("/tradingSystem/openNewStore", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"openNewStore\": {\"storename\":\"Pull&Bear\", \"description\":\"clothes\"}}");
                if(requestJson.containsKey("openNewStore")) {
                    JSONObject openNewStore = (JSONObject)requestJson.get("openNewStore");
                    String storename = (openNewStore.containsKey("storewname")) ? (String)openNewStore.get("storename") : null;
                    String description = (openNewStore.containsKey("description")) ? (String)openNewStore.get("description") : null;
                    String response = storeHandler.openNewStore(storename, description);
                    headers.set("openNewStore", String.format("application/json; charset=%s", UTF8));
                    final byte[] ResponseBytes = response.getBytes(UTF8);
                    he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                    he.getResponseBody().write(ResponseBytes);
                }
                else{
                    //???????
                    he.sendResponseHeaders(STATUS_NOT_OK, 0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });
            server.start();
    }
}

