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
        server.createContext("/", he -> {
            String pathToRoot = new File("html\\").getAbsolutePath();
            String path = he.getRequestURI().getPath();
            try {
                path = path.substring(1);
                path = path.replaceAll("//", "/");
                if (path.length() == 0)
                    path = "html\\HomeGuest.html";

                boolean fromFile = new File(pathToRoot + path).exists();
                InputStream in = fromFile ? new FileInputStream(pathToRoot + path)
                        : ClassLoader.getSystemClassLoader().getResourceAsStream(pathToRoot + path);

                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                OutputStream gout =  new DataOutputStream(bout);
                byte[] tmp = new byte[4096];
                int r;
                while ((r=in.read(tmp)) >= 0)
                    gout.write(tmp, 0, r);
                gout.flush();
                gout.close();
                in.close();
                byte[] data = bout.toByteArray();

                if (path.endsWith(".js"))
                    he.getResponseHeaders().set("Content-Type", "text/javascript");
                else if (path.endsWith(".html"))
                    he.getResponseHeaders().set("Content-Type", "text/html");
                else if (path.endsWith(".css"))
                    he.getResponseHeaders().set("Content-Type", "text/css");
                else if (path.endsWith(".json"))
                    he.getResponseHeaders().set("Content-Type", "application/json");
                else if (path.endsWith(".svg"))
                    he.getResponseHeaders().set("Content-Type", "image/svg+xml");
                if (he.getRequestMethod().equals("HEAD")) {
                    he.getResponseHeaders().set("Content-Length", "" + data.length);
                    he.sendResponseHeaders(200, -1);
                    return;
                }

                he.sendResponseHeaders(200, data.length);
                he.getResponseBody().write(data);
                he.getResponseBody().close();
            } catch (NullPointerException t) {
                System.err.println("Error retrieving: " + path);
            } catch (Throwable t) {
                System.err.println("Error retrieving: " + path);
                t.printStackTrace();
            }

        });

        server.createContext("/tradingSystem", he -> {
            final Headers headers = he.getResponseHeaders();
            String line;
            String response = "";

            try {

                File newFile = new File("C:\\Nofar\\שנה ג\\סמסטר ו\\סדנא\\workshop\\html\\html\\ShoppingCart.html");
                System.out.println(newFile.getPath());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));

                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            headers.add("html", "text/html");
//            headers.set("login", String.format("application/json; charset=%s", UTF8));
            he.sendResponseHeaders(STATUS_OK, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
                });
        server.createContext("/tradingSystem/login", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"login\":{\"username\":\"noy\", \"password\":\"123\", \"adminMode\":\"true\"}}");
                String username = (requestJson.containsKey("username")) ? (String)requestJson.get("username") : null;
                String password = (requestJson.containsKey("password")) ? (String)(requestJson.get("password")) : null;
                boolean adminMode = (requestJson.containsKey("adminMode")) ? (((String)(requestJson.get("adminMode"))).equals("true") ? true : false) : false;
                String response = usersHandler.login(username, password, adminMode);
                headers.set("login", String.format("application/json; charset=%s", UTF8));
                final byte[] ResponseBytes = response.getBytes(UTF8);
                he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                he.getResponseBody().write(ResponseBytes);
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
                JSONObject requestJson =  (JSONObject)parser.parse("{\"username\":\"noy\", \"password\":\"123\"}");
                String username = (requestJson.containsKey("username")) ? (String)requestJson.get("username") : null;
                String password = (requestJson.containsKey("password")) ? (String)(requestJson.get("password")) : null;
                String response = usersHandler.register(username, password);
                headers.set("register", String.format("application/json; charset=%s", UTF8));
                final byte[] ResponseBytes = response.getBytes(UTF8);
                he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                he.getResponseBody().write(ResponseBytes);
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
                String response = usersHandler.logout();
                headers.set("logout", String.format("application/json; charset=%s", UTF8));
                final byte[] ResponseBytes = response.getBytes(UTF8);
                he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                he.getResponseBody().write(ResponseBytes);
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
                String username = (requestJson.containsKey("username")) ? (String)requestJson.get("username") : null;
                String response = usersHandler.addAdmin(username);
                headers.set("addAdmin", String.format("application/json; charset=%s", UTF8));
                final byte[] ResponseBytes = response.getBytes(UTF8);
                he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                he.getResponseBody().write(ResponseBytes);
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
                JSONObject requestJson =  (JSONObject)parser.parse("{\"storename\":\"Pull&Bear\", \"description\":\"clothes\"}");
                String storename = (requestJson.containsKey("storewname")) ? (String)requestJson.get("storename") : null;
                String description = (requestJson.containsKey("description")) ? (String)requestJson.get("description") : null;
                String response = storeHandler.openNewStore(storename, description);
                headers.set("openNewStore", String.format("application/json; charset=%s", UTF8));
                final byte[] ResponseBytes = response.getBytes(UTF8);
                he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                he.getResponseBody().write(ResponseBytes);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        server.createContext("/tradingSystem/addStoreOwner", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"username\":\"noy\", \"storename\":\"Pull&Bear\"}");
                String storename = (requestJson.containsKey("username")) ? (String)requestJson.get("username") : null;
                String description = (requestJson.containsKey("storename")) ? (String)requestJson.get("storename") : null;
                String response = storeHandler.openNewStore(storename, description);
                headers.set("addStoreOwner", String.format("application/json; charset=%s", UTF8));
                final byte[] ResponseBytes = response.getBytes(UTF8);
                he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                he.getResponseBody().write(ResponseBytes);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });


        //-----------------------------------------ShoppingCartHandler cases------------------------------------------
        server.createContext("/tradingSystem/cart", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"viewCart\": \"\"}");
                    String e = usersHandler.register("noy", "1234");
                    String d = usersHandler.login("noy", "1234", false);
                    String c = storeHandler.openNewStore("kravitz", "writing tools");
                    storeHandler.openNewStore("Pull&Bear", "clothes");
                    String f = storeHandler.UpdateInventory("kravitz", "pilot", 10, "Clothing", "black pen", 100);
                    storeHandler.UpdateInventory("Pull&Bear", "skirt", 50, "Clothing", "skirt", 5);
                    usersHandler.logout();
                    String a = cartHandler.AddToShoppingBasket("kravitz", "pilot", 5);
                    String b = cartHandler.AddToShoppingBasket("Pull&Bear", "skirt", 2);
                String response = cartHandler.viewCart();
                headers.set("viewCart", String.format("application/json; charset=%s", UTF8));
                final byte[] ResponseBytes = response.getBytes(UTF8);
                he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
                he.getResponseBody().write(ResponseBytes);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        server.start();
    }
}

