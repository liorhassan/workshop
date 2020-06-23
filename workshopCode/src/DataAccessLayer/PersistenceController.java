package DataAccessLayer;

import DomainLayer.Security.UserDetails;
import DomainLayer.TradingSystem.Models.*;
import DomainLayer.TradingSystem.Permission;
import DomainLayer.TradingSystem.ProductItem;
import DomainLayer.TradingSystem.StoreManaging;
import DomainLayer.TradingSystem.StoreOwning;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class PersistenceController {

    // Create the SessionFactory when you start the application.
    private static SessionFactory SESSION_FACTORY;

    private static Configuration config;

    public static void closeFactory() {
        SESSION_FACTORY.close();
    }

    /**
          * Initialize the SessionFactory instance.
          */
    public static void initiate(boolean isProduction) {
        // Create a Configuration object.
        config = new Configuration();

        // TODO: add here all consistent objects
        config.addAnnotatedClass(Product.class);
        config.addAnnotatedClass(Store.class);
        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(ShoppingCart.class);
        config.addAnnotatedClass(Basket.class);
        config.addAnnotatedClass(ProductItem.class);
        config.addAnnotatedClass(Purchase.class);
        config.addAnnotatedClass(StoreManaging.class);
        config.addAnnotatedClass(StoreOwning.class);
        config.addAnnotatedClass(Permission.class);

        config.addAnnotatedClass(UserDetails.class);

        // Configure using the application resource named hibernate.cfg.xml.
        if (isProduction)
            config.configure("hibernate.cfg.xml");
        else
            config.configure("TestSchemaHibernate.cfg.xml");

        // Extract the properties from the configuration file.
        Properties prop = config.getProperties();

        // Create StandardServiceRegistryBuilder using the properties.
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(prop);

        // Build a ServiceRegistry
        ServiceRegistry registry = builder.build();

        // Create the SessionFactory using the ServiceRegistry
        SESSION_FACTORY = config.buildSessionFactory(registry);
    }

    private static void checkConnection() throws SQLException {
        Properties p = config.getProperties();

        String url = p.getProperty("hibernate.connection.url");
        String user = p.getProperty("hibernate.connection.username");
        String password = p.getProperty("hibernate.connection.password");

        try {
            DriverManager.getConnection(url, user, password).close();
        } catch (SQLException e) {
            throw new SQLException("Storage service is unavailable, hang in there we're on it :)");
        }
    }

    public static void create(Object model) throws SQLException {
        checkConnection();

        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();

            // Save the product
            session.save(model);
            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
    }


    public static void delete(Object model) throws SQLException {

        checkConnection();

        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();

            // Delete the object
            session.delete(model);

            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
    }


    public static void update(Object updatedModel) throws SQLException {
        checkConnection();

        // Create a session
        Session session = SESSION_FACTORY.openSession();

        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();

            // Update the student
            session.update(updatedModel);

            // Commit the transaction
            transaction.commit();
        } catch (HibernateException ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
    }

    //byStore = false --> retrieve all products in the database
    //byStore = true --> retrieve all products that belong to the store
    public static List<Product> readAllProducts(String storeName, boolean byStore) throws SQLException {
        checkConnection();

        List<Product> data = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Product> cr = cb.createQuery(Product.class);
            Root<Product> root = cr.from(Product.class);
            if(byStore)
                cr.select(root).where(cb.equal(root.get("storeName"), storeName), cb.isTrue(root.get("inStock")));
            Query<Product> query = session.createQuery(cr);
            data = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return data;
    }

    public static List<User> readAllUsers(boolean isAdmin) throws SQLException {
        checkConnection();

        List<User> users = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
//            transaction = session.beginTransaction();
//            CriteriaBuilder cb = session.getCriteriaBuilder();
//            CriteriaQuery<User> cr = cb.createQuery(User.class);
//            Root<User> rootEntry = cr.from(User.class);
//            CriteriaQuery<User> all = cr.select(rootEntry);
//            Query<User> query = session.createQuery(all);
//            users = query.getResultList();

            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cr = cb.createQuery(User.class);
            Root<User> root = cr.from(User.class);
            if(isAdmin)
                cr.select(root).where(cb.isTrue(root.get("isAdmin")));
            Query<User> query = session.createQuery(cr);
            users = query.getResultList();

            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return users;
    }

    public static List<Store> readAllStores() throws SQLException {
        checkConnection();

        List<Store> stores = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Store> cr = cb.createQuery(Store.class);
            Root<Store> rootEntry = cr.from(Store.class);
            CriteriaQuery<Store> all = cr.select(rootEntry);
            Query<Store> query = session.createQuery(all);
            stores = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return stores;

    }

    public static List<Purchase> readAllPurchases(String storeName) throws SQLException {
        checkConnection();

        List<Purchase> purchases = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Purchase> cr = cb.createQuery(Purchase.class);
            Root<Purchase> root = cr.from(Purchase.class);
            cr.select(root).where(cb.equal(root.get("ownerName"), storeName));
            Query<Purchase> query = session.createQuery(cr);
            purchases = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return purchases;
    }

    public static List<StoreManaging> readAllManagers(String storeName) throws SQLException {
        checkConnection();

        List<StoreManaging> manages = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<StoreManaging> cr = cb.createQuery(StoreManaging.class);
            Root<StoreManaging> root = cr.from(StoreManaging.class);
            cr.select(root).where(cb.equal(root.get("storeName"), storeName));
            Query<StoreManaging> query = session.createQuery(cr);
            manages = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return manages;
    }

    public static List<StoreOwning> readAllOwners(String storeName) throws SQLException {
        checkConnection();

        List<StoreOwning> owners = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<StoreOwning> cr = cb.createQuery(StoreOwning.class);
            Root<StoreOwning> root = cr.from(StoreOwning.class);
            cr.select(root).where(cb.equal(root.get("storeName"), storeName));
            Query<StoreOwning> query = session.createQuery(cr);
            owners = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return owners;
    }

    public static ShoppingCart readUserCart(String username) throws SQLException {
        checkConnection();

        List<ShoppingCart> carts = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShoppingCart> cr = cb.createQuery(ShoppingCart.class);
            Root<ShoppingCart> root = cr.from(ShoppingCart.class);
            cr.select(root);
            cr.where(cb.and(cb.equal(root.get("userName"), username),
                     cb.isFalse(root.get("isHistory"))));
            Query<ShoppingCart> query = session.createQuery(cr);
            carts = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        if(!carts.isEmpty()){
            return carts.get(0);
        }
        return null;

    }

    public static List<ProductItem> readAllProductItems(int basketId) throws SQLException {
        checkConnection();

        List<ProductItem> pi = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ProductItem> cr = cb.createQuery(ProductItem.class);
            Root<ProductItem> root = cr.from(ProductItem.class);
            cr.select(root).where(cb.equal(root.get("basketId"), basketId));
            Query<ProductItem> query = session.createQuery(cr);
            pi = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return pi;

    }

    public static List<Basket> readAllBasket(int Cartid) throws SQLException {
        checkConnection();

        List<Basket> baskets = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Basket> cr = cb.createQuery(Basket.class);
            Root<Basket> root = cr.from(Basket.class);
            cr.select(root).where(cb.equal(root.get("cartId"), Cartid));
            Query<Basket> query = session.createQuery(cr);
            baskets = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return baskets;

    }

    public static List<Purchase> readPurchaseHistory(String ownerName) throws SQLException {
        checkConnection();

        List<Purchase> purchases = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Purchase> cr = cb.createQuery(Purchase.class);
            Root<Purchase> root = cr.from(Purchase.class);
            cr.select(root).where(cb.equal(root.get("ownerName"), ownerName));
            Query<Purchase> query = session.createQuery(cr);
            purchases = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return purchases;
    }

    public static ShoppingCart readCartById(int cartId) throws SQLException {
        checkConnection();

        List<ShoppingCart> cart = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShoppingCart> cr = cb.createQuery(ShoppingCart.class);
            Root<ShoppingCart> root = cr.from(ShoppingCart.class);
            cr.select(root).where(cb.equal(root.get("id"), cartId));
            Query<ShoppingCart> query = session.createQuery(cr);
             cart = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        if(cart == null || cart.isEmpty()){
            return new ShoppingCart();
        }
        return cart.get(0);
    }

    public static List<Permission> readAllPermissions(String storeName, String appointeeName) throws SQLException {
        checkConnection();

        List<Permission> perms = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Permission> cr = cb.createQuery(Permission.class);
            Root<Permission> root = cr.from(Permission.class);
            cr.select(root).where(cb.equal(root.get("storeName"), storeName), cb.equal(root.get("appointee"), appointeeName));
            Query<Permission> query = session.createQuery(cr);
            perms = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }

        return perms;

    }

    public static UserDetails readUserDetails(String username) throws SQLException {
        checkConnection();

        List<UserDetails> userDetails = null;
        // Create a session
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = null;
        try {
            // Begin a transaction
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserDetails> cr = cb.createQuery(UserDetails.class);
            Root<UserDetails> root = cr.from(UserDetails.class);
            cr.select(root).where(cb.equal(root.get("username"), username));
            Query<UserDetails> query = session.createQuery(cr);
            userDetails = query.getResultList();


            // Commit the transaction
            transaction.commit();
        } catch (Exception ex) {
            // If there are any exceptions, roll back the changes
            if (transaction != null) {
                transaction.rollback();
            }
            // Print the Exception
            ex.printStackTrace();
        } finally {
            // Close the session
            session.close();
        }
        return userDetails.get(0);
    }

}
