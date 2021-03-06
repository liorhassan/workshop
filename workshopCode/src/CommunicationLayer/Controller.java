package CommunicationLayer;


import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ServiceLayer.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;


public class Controller {

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
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private static ClientWebSocket clientWS;

    public static void start() throws IOException {

        clientWS = new ClientWebSocket();
        clientWS.start();
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 1);

        //-----------------------------------------Handle HTML\JS Requests------------------------------------------
        server.createContext("/", he -> {
            String pathToRoot = new File("workshopCode\\src\\PresentationLayer\\").getAbsolutePath().concat("\\");
            String path = he.getRequestURI().getPath();
            try {
                path = path.substring(1);
                path = path.replaceAll("//", "/");
                if (path.length() == 0)
                    path = "html\\Main.html";

                boolean fromFile = new File(pathToRoot + path).exists();
                InputStream in = fromFile ? new FileInputStream(pathToRoot + path)
                        : ClassLoader.getSystemClassLoader().getResourceAsStream(pathToRoot + path);

                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                OutputStream gout = new DataOutputStream(bout);
                byte[] tmp = new byte[4096];
                int r;
                while ((r = in.read(tmp)) >= 0)
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

        //-----------------------------------------UsersHandler cases------------------------------------------
        //accept: {session_id:"", userName:"", password:"", adminMode: true/false}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/login", he -> {
            final Headers headers = he.getResponseHeaders();

            try {
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("userName")) ? (String) requestJson.get("userName") : null;
                String password = (requestJson.containsKey("password")) ? (String) (requestJson.get("password")) : null;
                boolean adminMode = (requestJson.containsKey("adminMode")) ? (boolean) (requestJson.get("adminMode")) : false;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = usersHandler.login(UUID.fromString(session_id),userName, password, adminMode);
                headers.set("login", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("login", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {userName:"", password:""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/register", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("userName")) ? (String) requestJson.get("userName") : null;
                String password = (requestJson.containsKey("password")) ? (String) (requestJson.get("password")) : null;
                String response = usersHandler.register(userName, password);
                headers.set("register", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("login", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id:""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/logout", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = usersHandler.logout(UUID.fromString(session_id));
                headers.set("logout", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (Exception e) {
                headers.set("logout", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {userName: ""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/addAdmin", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("userName")) ? (String) requestJson.get("userName") : null;
                String response = usersHandler.addAdmin(userName);
                headers.set("addAdmin", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                sendERROR(he, e.getMessage());
            }
            finally {
                he.close();
            }
        });
        //-----------------------------------------SearchHandler cases------------------------------------------
        //accept: {session_id: "",name: "", category:"", keyWords:["", "", ""]}
        //retrieve: [{name:"", price:"", store:"", description:""}, ..]  OR  ERROR
        server.createContext("/tradingSystem/search", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String name = (requestJson.containsKey("name")) ? (String) requestJson.get("name") : null;
                String category = (requestJson.containsKey("category")) ? (String) requestJson.get("category") : null;
                JSONArray keys = (requestJson.containsKey("keywords")) ? ((JSONArray) requestJson.get("keywords")) : new JSONArray();
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String[] keywords = new String[keys.size()];
                for (int i = 0; 0 < keys.size(); i++)
                    keywords[i] = (String) keys.get(i);

                String response = searchHandler.searchProduct(UUID.fromString(session_id),name, category, keywords);
                headers.set("search", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("search", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", maxPrice:Integer, minPrice:Integer, category:""}
        //retrieve: [{name:"", price:"", store:"", description:""}, ..]  OR ERROR
        server.createContext("/tradingSystem/filter", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                Integer maxPrice = (requestJson.containsKey("maxPrice")) && (!requestJson.get("maxPrice").toString().equals("")) ? Integer.parseInt(requestJson.get("maxPrice").toString()) : null;
                Integer minPrice = (requestJson.containsKey("minPrice")) && (!requestJson.get("minPrice").toString().equals("")) ? Integer.parseInt(requestJson.get("minPrice").toString()) : null;
                String category = (requestJson.containsKey("category")) ? (String) requestJson.get("category") : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = searchHandler.filterResults(UUID.fromString(session_id), minPrice, maxPrice, category);
                headers.set("filter", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("filter", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });


        //-----------------------------------------StoreHandler cases------------------------------------------
        //accept: {session_id:"", store:"", description:""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/openNewStore", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                String description = (requestJson.containsKey("description")) ? (String) requestJson.get("description") : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = storeHandler.openNewStore(UUID.fromString(session_id), storeName, description);
                headers.set("openNewStore", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("openNewStore", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });
        //accept: {session_id:"", user: "", store:"", status:"approve/reject"}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/approveCandidate", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user")) ? (String) requestJson.get("user") : null;
                String storeName = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                Boolean status = (requestJson.containsKey("status")) ? requestJson.get("status").toString().equals("approve") : false;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = storeHandler.responseToAppointmentRequest(UUID.fromString(session_id), userName, storeName, status);
                headers.set("approveCandidate", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("approveCandidate", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", user: "", store:""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/addStoreOwner", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{\"user\":\"noy\", \"store\":\"Pull&Bear\"}");
                String userName = (requestJson.containsKey("user")) ? (String) requestJson.get("user") : null;
                String storeName = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = storeHandler.addStoreOwner(UUID.fromString(session_id), userName, storeName);
                headers.set("addStoreOwner", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("addStoreOwner", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", store:""}
        //retrieve: [{name: ""},...]
        server.createContext("/tradingSystem/newOwnerCandidates", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                //String response = storeHandler.getOwnerCandidates(session_id, storeName); TODO: implement missing functionality
                JSONArray ja = new JSONArray();
                JSONObject jo1 = new JSONObject();
                JSONObject jo2 = new JSONObject();
                jo1.put("name","test1");
                jo2.put("name","test2");
                ja.add(jo1);
                ja.add(jo2);
                String response = ja.toJSONString();
                headers.set("newOwnerCandidates", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("newOwnerCandidates", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", user: "", store:""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/removeStoreOwner", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user")) ? ((String) requestJson.get("user")) : null;
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                //String response = storeHandler.removeStoreOwner(session_id, userName, storeName);
                String response = "";//TODO
                headers.set("removeStoreOwner", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("removeStoreOwner", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id:"", store:"", product: "", price:"", category:"", desc:"", amount:int}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/updateInventory", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));

                String storeName = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                String productName = (requestJson.containsKey("product")) ? (String) requestJson.get("product") : null;
                Double price = (requestJson.containsKey("price")) && (!requestJson.get("price").toString().equals("")) ? Double.parseDouble(requestJson.get("price").toString()) : null;
                String category = (requestJson.containsKey("category")) ? (String) requestJson.get("category") : null;
                String desc = (requestJson.containsKey("desc")) ? (String) requestJson.get("desc") : null;
                Integer amount = (requestJson.containsKey("amount")) && (!requestJson.get("amount").toString().equals("")) ? Integer.parseInt(requestJson.get("amount").toString()) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = storeHandler.UpdateInventory(UUID.fromString(session_id), storeName, productName, price, category, desc, amount);

                headers.set("updateInventory", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("updateInventory", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {store:""}
        //retrieve: [{discountId:int, discountString:""},..]
        server.createContext("/tradingSystem/getDiscounts", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                String response = storeHandler.viewDiscounts(storeName);
                headers.set("getDiscounts", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("getDiscounts", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {type:simple, subType:DiscountBasketPriceOrAmount, amount:int, percent:int, onPriceOrAmountOfProducts:boolean}
        //accept: {type:simple, subType:DiscountRevealedProduct, productName:String, discountPercent:int}
        //accept: {type:simple, subType:DiscountCondBasketProducts, productConditionName:String, conditionAmount:int, discountProductName:String, discountPercent:int}}
        //accept: {type:simple, subType:DiscountCondProductAmount , productName:String, minAmount:int, discountPercent:int}
        server.createContext("/tradingSystem/addDiscount", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String response = "";
                String type = (requestJson.containsKey("type")) ? (String) requestJson.get("type") : null;
                String subtype = (requestJson.containsKey("subtype")) ? (String) requestJson.get("subtype") : null;
                String store = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                switch(type){
                    case "simple":
                        switch(subtype){
                            case "DiscountBasketPriceOrAmount":
                                int amount = (requestJson.containsKey("amount")) ? Integer.parseInt((String) requestJson.get("amount")) : null;
                                int percent = (requestJson.containsKey("percent")) ? Integer.parseInt((String) requestJson.get("percent")) : null;
                                boolean onPriceOrAmountOfProducts = (requestJson.containsKey("onPriceOrAmountOfProducts")) ? (boolean) (requestJson.get("onPriceOrAmountOfProducts")) : false;
                                response = storeHandler.addDiscountForBasketPriceOrAmount(store, percent, amount, onPriceOrAmountOfProducts);
                                break;
                            case "DiscountRevealedProduct":
                                String product = (requestJson.containsKey("productName")) ? (String) requestJson.get("productName") : null;
                                int percent1 = (requestJson.containsKey("discountPercent")) ? Integer.parseInt((String) requestJson.get("discountPercent")) : null;
                                response = storeHandler.addDiscountRevealedProduct(store, product, percent1);
                                break;
                            case "DiscountCondBasketProducts":
                                String productConditionName = (requestJson.containsKey("productConditionName")) ? (String) requestJson.get("productConditionName") : null;
                                int amount2 = (requestJson.containsKey("conditionAmount")) ? Integer.parseInt((String) requestJson.get("conditionAmount")) : null;
                                String product2 = (requestJson.containsKey("discountProductName")) ? (String) requestJson.get("discountProductName") : null;
                                int percent2 = (requestJson.containsKey("discountPercent")) ? Integer.parseInt((String) requestJson.get("discountPercent")) : null;
                                response = storeHandler.addDiscountCondBasketProducts(store, product2, productConditionName, percent2, amount2);
                                break;
                            case "DiscountCondProductAmount"://TODO: I pressed the add discount button and it doesnt work
                                String product3 = (requestJson.containsKey("productName")) ? (String) requestJson.get("productName") : null;
                                int amount3 = (requestJson.containsKey("minAmount")) ? Integer.parseInt((String) requestJson.get("minAmount")) : null;//TODO: there is a problem with minAmount = ""
                                int percent3 = (requestJson.containsKey("discountPercent")) ? Integer.parseInt((String) requestJson.get("discountPercent")) : null;
                                response = storeHandler.addDiscountCondProductAmount(store, product3, percent3, amount3);
                                break;
                        }
                        break;

                    case "compose":

                        break;
                }

                headers.set("addDiscount", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (Exception e) {
                headers.set("addDiscount", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });


        //accept: {rator: , operand1:, operand2:}
        server.createContext("/tradingSystem/addDiscountPolicy", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
              String response = storeHandler.addDiscountPolicy(new String(requestByte));
                headers.set("addDiscountPolicy", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (Exception e) {
                headers.set("addDiscountPolicy", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {type: simple, subtype: "PurchasePolicyProduct, productName: name, amount: int, minOrMax: bolean}
        // /PurchasePolicyStore}
        server.createContext("/tradingSystem/addPurchasePolicy", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                String response = "";
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String type = (requestJson.containsKey("type")) ? (String) requestJson.get("type") : null;
                String subtype = (requestJson.containsKey("subtype")) ? (String) requestJson.get("subtype") : null;
                String store = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                switch (type){
                    case "simple":
                        if(subtype.equals("PurchasePolicyProduct")){
                            int amount = (requestJson.containsKey("amount")) ? Integer.parseInt((String) requestJson.get("amount")) : null;
                            String product = (requestJson.containsKey("productName")) ? (String) requestJson.get("productName") : null;
                            boolean minOrMax = (requestJson.containsKey("minOrMax")) ? (boolean) (requestJson.get("minOrMax")) : false;
                            boolean standalone = (requestJson.containsKey("standalone")) ? (boolean) (requestJson.get("standalone")) : false;
                            response = storeHandler.addPurchasePolicyProduct(store, product, amount, minOrMax, standalone);
                        }else if(subtype.equals("PurchasePolicyStore")){
                            int limit = (requestJson.containsKey("limit")) ? Integer.parseInt((String) requestJson.get("limit")) : null;
                            boolean minOrMax = (requestJson.containsKey("minOrMax")) ? (boolean) (requestJson.get("minOrMax")) : false;
                            boolean standalone = (requestJson.containsKey("standalone")) ? (boolean) (requestJson.get("standalone")) : false;
                            response = storeHandler.addPurchasePolicyStore(store, limit, minOrMax, standalone);
                        }
                        break;
                    case "compose":
                        response = storeHandler.addPurchasePolicy(new String(requestByte));
                }
                headers.set("addDiscountPolicy", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (Exception e) {
                headers.set("addDiscountPolicy", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });


        server.createContext("/tradingSystem/getSimplePolicies", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? (String) requestJson.get("store") : null;
                String response = storeHandler.viewPurchasePolicies(storeName);
                headers.set("getSimplePolicies", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("getSimplePolicies", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //-----------------------------------------ShoppingCartHandler cases------------------------------------------
        //accept: {store: "", product:"", amount: ""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/cart", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = cartHandler.viewCart(UUID.fromString(session_id));
                headers.set("viewCart", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (Exception e) {
                headers.set("viewCart", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", store: "", product:"", amount: ""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/editCart", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String productName = (requestJson.containsKey("product")) ? ((String) requestJson.get("product")) : null;
                int amount = (requestJson.containsKey("amount")) ? Integer.parseInt((String) requestJson.get("amount")) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = cartHandler.editCart(UUID.fromString(session_id), storeName, productName, amount);
                headers.set("editCart", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("editCart", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", store: "", product:"", amount: ""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/addToShoppingBasket", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String productName = (requestJson.containsKey("product")) ? ((String) requestJson.get("product")) : null;
                int amount = (requestJson.containsKey("amount")) ? Integer.parseInt(requestJson.get("amount").toString()) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = cartHandler.AddToShoppingBasket(UUID.fromString(session_id), storeName, productName, amount);
                headers.set("addToShoppingBasket", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("addToShoppingBasket", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: ""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/purchaseCart", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = cartHandler.purchaseCart(UUID.fromString(session_id));
                headers.set("purchaseCart", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (Exception e) {
                headers.set("purchaseCart", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //-----------------------------------------StoreManagerHandler cases------------------------------------------
        //accept: {session_id: "", user: "", store:""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/addStoreManager", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user")) ? ((String) requestJson.get("user")) : null;
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = storeManagerHandler.addStoreManager(UUID.fromString(session_id), userName, storeName);
                headers.set("addStoreManager", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("addStoreManager", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", user: "", store:""}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/removeStoreManager", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user")) ? ((String) requestJson.get("user")) : null;
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = storeManagerHandler.removeStoreManager(UUID.fromString(session_id), userName, storeName);
                headers.set("removeStoreManager", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("removeStoreManager", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", user: "", store:"", permission: ["", "",..]}
        //retrieve: {SUCCESS: msg} OR ERROR
        server.createContext("/tradingSystem/editPermission", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user")) ? ((String) requestJson.get("user")) : null;
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                JSONArray perm = requestJson.containsKey("permission") ? (JSONArray) requestJson.get("permission") : new JSONArray();
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                List<String> permissions = new LinkedList<>();
                for (int i = 0; i < perm.size(); i++)
                    permissions.add((String) perm.get(i));
                String response = storeManagerHandler.editManagerPermissions(UUID.fromString(session_id), userName, permissions, storeName);
                headers.set("editPermissions", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("editPermission", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //-----------------------------------------ViewInfoHandler cases------------------------------------------
        //accept: {store:""}
        //retrieve: {name:"", description:"", products:[{productName:"", price:Integer}]}  OR ERROR
        server.createContext("/tradingSystem/storeInfo", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String response = viewInfoHandler.viewStoreinfo(storeName);
                headers.set("storeInfo", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("storeInfo", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {store:"", product:""}
        //retrieve: {name:"", description:"", price:Integer}  OR ERROR
        server.createContext("/tradingSystem/productInfo", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String productName = (requestJson.containsKey("product")) ? ((String) requestJson.get("product")) : null;
                String response = viewInfoHandler.viewProductInfo(storeName, productName);
                headers.set("productInfo", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("productInfo", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });
        //-----------------------------------------PurchaseHistory cases------------------------------------------
        //accept: {session_id: "", store:""}
        //retrieve: [[{name:"", price:Integer, store:"", amount:Integer}],[{...},...]]  OR ERROR
        server.createContext("/tradingSystem/storePurchaseHistory", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";

                String response = purchaseHistoryHandler.ViewPurchaseHistoryOfStore(UUID.fromString(session_id), storeName);

                headers.set("storePurchaseHistory", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("storePurchaseHistory", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: "", store:""}
        //retrieve: [[{name:"", price:Integer, store:"", amount:Integer}],[{...},...]]  OR ERROR
        server.createContext("/tradingSystem/storePurchaseHistoryAdmin", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = purchaseHistoryHandler.viewPurchaseHistoryOfStoreAsAdmin(UUID.fromString(session_id), storeName);
                headers.set("storePurchaseHistoryAdmin", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("storePurchaseHistoryAdmin", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id: ""}
        //retrieve: [[{name:"", price:Integer, store:"", amount:Integer}, {...}], ...]  OR ERROR
        server.createContext("/tradingSystem/userPurchaseHistory", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = purchaseHistoryHandler.viewLoggedInUserPurchaseHistory(UUID.fromString(session_id));
                headers.set("userPurchaseHistory", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (Exception e) {
                headers.set("userPurchaseHistory", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {session_id:"", user:""}
        //retrieve: [[{name:"", price:Integer, store:"", amount:Integer}, ...], ...]  OR ERROR
        server.createContext("/tradingSystem/userPurchaseHistoryAdmin", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user")) ? ((String) requestJson.get("user")) : null;
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = purchaseHistoryHandler.viewPurchaseHistoryOfUserAsAdmin(UUID.fromString(session_id), userName);
                headers.set("usePurchaseHistoryAdmin", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("userPurchaseHistoryAdmin", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //-----------------------------------------Other cases----------------------------------------------------
        //accept: {session_id: ""}
        //retrieve: {name:"", type:"", options:["",..]}
        server.createContext("/tradingSystem/myStores", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";

                String response = storeHandler.getMyStores(UUID.fromString(session_id));

                headers.set("myStores", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("myStores", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });



        //retrieve: [{name:"", price:"", store:"", description:""}, ..]
        server.createContext("/tradingSystem/allProducts", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                String response = viewInfoHandler.getAllProduct();
                headers.set("allProducts", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } finally {
                he.close();
            }
        });

        //retrieve: {"", "", ...}
        server.createContext("/tradingSystem/allCategories", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                String response = viewInfoHandler.getAllCategories();
                headers.set("allCategories", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);

            } finally {
                he.close();
            }
        });

        //accept: {session_id}
        //retrieve: {"", "", ...}
        server.createContext("/tradingSystem/isLoggedIn", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String session_id = (requestJson.containsKey("session_id")) ?  (requestJson.get("session_id").toString()) : "";
                String response = usersHandler.isLoggedIn(UUID.fromString(session_id));
                headers.set("isLoggedIn", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                headers.set("myStores", String.format("application/json; charset=%s", UTF8));
                sendERROR(he, e.getMessage());
            } finally {
                he.close();
            }
        });

        //accept: {store:""}
        //retrieve: [{name:"", description:"", price:int, amount:Integer, category:""}, ...]
        server.createContext("/tradingSystem/getStoreProducts", he -> {
            final Headers headers = he.getResponseHeaders();
            try {
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject) parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store")) ? ((String) requestJson.get("store")) : null;

                String response = storeHandler.getStoreProducts(storeName);
                headers.set("getStoreProducts", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });


        server.start();
    }



    public static void sendResponse(HttpExchange he, String response) throws IOException {
        final byte[] ResponseBytes = response.getBytes(UTF8);
        he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
        he.getResponseBody().write(ResponseBytes);
    }

    public static void sendERROR(HttpExchange he, String response) throws IOException {
        final byte[] ResponseBytes = response.getBytes(UTF8);
        he.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, ResponseBytes.length);
        he.getResponseBody().write(ResponseBytes);
    }

}

