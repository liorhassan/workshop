package DataAccessLayer;

import DomainLayer.TradingSystem.Models.Product;
import DomainLayer.TradingSystem.Models.Store;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Properties;

public class PersistenceController {

    // Create the SessionFactory when you start the application.
    private static SessionFactory SESSION_FACTORY;

    public static void closeFactory() {
        SESSION_FACTORY.close();
    }

    /**
          * Initialize the SessionFactory instance.
          */
    public static void initiate() {
        // Create a Configuration object.
        Configuration config = new Configuration();

        // TODO: add here all consistent objects
        config.addAnnotatedClass(Product.class);
        config.addAnnotatedClass(Store.class);

        // Configure using the application resource named hibernate.cfg.xml.
        config.configure();
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

    public static void create(Object model) {
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


    public static void delete(Object model) {
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
    public static List<Product> readAllProducts(String storeName, boolean byStore) {
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
                cr.select(root).where(cb.equal(root.get("storeName"), storeName));
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

    public static void update(Object updatedModel) {
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
}
