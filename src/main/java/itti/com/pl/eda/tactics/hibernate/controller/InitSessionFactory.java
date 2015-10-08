/**
 * 
 * @author Sebastian Hennebrueder
 * created Feb 22, 2006
 * copyright 2006 by http://www.laliluna.de
 */
package itti.com.pl.eda.tactics.hibernate.controller;

import java.util.Map;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * Singletone factory for Hibernate sessions
 * @author marcin
 *
 */
public class InitSessionFactory {

	/**
	 * Default constructor.
	 */
	private InitSessionFactory() {
	}

	private static String CONFIG_FILE_LOCATION = "hibernate.cfg.xml";

	private static final Configuration cfg = new Configuration();
	private static Map<String, String> connAttrs = null;

	private static org.hibernate.SessionFactory sessionFactory;


	/**
	 * initialises the configuration if not yet done and returns the current
	 * instance
	 * 
	 * @return sessionFactory singletone object
	 */
	public static SessionFactory getInstance() {
		if (sessionFactory == null)
			initSessionFactory();
		return sessionFactory;
	}


	/**
	 * Returns the ThreadLocal Session instance. 
	 * @return Session
	 * @throws HibernateException
	 */
	public Session openSession() {
		return sessionFactory.getCurrentSession();
	}


	/**
	 * The behaviour of this method depends on the session context you have
	 * configured. This factory is intended to be used with a hibernate.cfg.xml
	 * including the following property <property
	 * name="current_session_context_class">thread</property> This would return
	 * the current open session or if this does not exist, will create a new
	 * session
	 * @return session object
	 */
	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}


	private static synchronized void initSessionFactory() {

		Logger logger = Logger.getLogger(InitSessionFactory.class);

		if (sessionFactory == null) {

			try {
				cfg.configure(CONFIG_FILE_LOCATION);
				if(connAttrs != null){
					changeDefCfg();
				}

				String sessionFactoryJndiName = cfg.getProperty(Environment.SESSION_FACTORY_NAME);

				if (sessionFactoryJndiName != null) {
					cfg.buildSessionFactory();
					logger.debug("get a jndi session factory");
					sessionFactory = (SessionFactory) (new InitialContext())
							.lookup(sessionFactoryJndiName);
				} else{
					logger.debug("classic factory");
					sessionFactory = cfg.buildSessionFactory();
				}
			} catch (Exception e) {
				System.err.println("%%%% Error Creating HibernateSessionFactory %%%%");
				e.printStackTrace();
				throw new HibernateException(
						"Could not initialize the Hibernate configuration");
			}
		}
	}


	private static void changeDefCfg() {

		for (String param : connAttrs.keySet()) {
			String val = connAttrs.get(param);
			if(cfg.getProperty(param) != null){
				cfg.setProperty(param, val);
			}
		}
	}


	/**
	 * closes session factory and releases all database resources
	 */
	public static void close(){
		if (sessionFactory != null)
			sessionFactory.close();
		sessionFactory = null;
	
	}

	/**
	 * method to overwrite default hibernate attributes (stored in configuration file)
	 * @param hibernateAttr map of new attributes
	 */
	public static void setAttributes(Map<String, String> hibernateAttr) {
		connAttrs = hibernateAttr;
	}
}
