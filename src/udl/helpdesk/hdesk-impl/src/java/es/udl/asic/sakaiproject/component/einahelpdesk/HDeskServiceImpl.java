package es.udl.asic.sakaiproject.component.einahelpdesk;

import es.udl.asic.sakaiproject.service.einahelpdesk.HDeskService;

//pel pool
import org.apache.commons.dbcp.BasicDataSource;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;

//import com.mchange.v2.c3p0.*;
import java.util.*;
//pel SQL SERVICE
//import org.sakaiproject.service.framework.sql.*;
//import org.sakaiproject.exception.IdUnusedException;
//import org.sakaiproject.exception.IdUsedException;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HDeskServiceImpl implements HDeskService{
	
	String dialect,driver,url,user,passwd;
	//ComboPooledDataSource cpds = null;
	//ObjectPool connectionPool=null;
	Configuration cfg=null;
	private static SessionFactory sessionFactory=null;

	public void init(){
		System.out.println("\n\nCarregant implementacio del servei helpdesk....\n\n");
	}
	
	/* public Connection getConnection() {
		Connection conn = null;
		
		if (cpds == null) {
			cpds = setupDataSource();
			}
		try {
		//	Class.forName("oracle.jdbc.driver.OracleDriver");
			conn =(Connection) cpds.getConnection();
			} catch (Exception ex) {
			System.out.println("Error al obtindre la connexio");
		}
		return conn;
	}	
	*/
	
	public Session getSession(){
		Session session=null;
		if (sessionFactory==null){
			sessionFactory=GenerarFactoria();
		}
		try{
		 session = sessionFactory.openSession();
			}catch (Exception ex) {
			System.out.println("Error al obtindre la sessio @@@@@@@@@");
			ex.printStackTrace();
		}
		return session;
	}
	/* public ComboPooledDataSource setupDataSource() {

		try {
		
		System.out.println("\n***Creo el pool***\n");
		connectionPool = new GenericObjectPool(null);
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				url, user,passwd);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory, connectionPool, null, null, false, true);
		PoolingDataSource dataSource = new PoolingDataSource(connectionPool);
		
		//Class.forName("oracle.jdbc.driver.OracleDriver");
		cpds = new ComboPooledDataSource(); 
		//cpds.setDriverClass("oracle.jdbc.driver.OracleDriver"); //loads the jdbc driver
		//cpds.setDriverClass(driver);
		
		cpds.setJdbcUrl(url); 
		cpds.setUser(user);
		cpds.setPassword(passwd); 
		cpds.setMaxStatements( 50 );
		cpds.setInitialPoolSize(5);
		cpds.setMaxIdleTime(1800);
		cpds.setMaxStatementsPerConnection(15);
		cpds.setAcquireIncrement(1);
		cpds.setMaxAdministrativeTaskTime(1000);
		
		
		cpds.setAcquireIncrement(5);
		//cpds.setDriverClass(driver);//ªªªªªªªªªªªªªªªªªªªªªªªªªªª
		cpds.setCheckoutTimeout(5000);
		cpds.setIdleConnectionTestPeriod(1800); 
		
						
		return cpds;
		} catch (Exception e) {
			e.printStackTrace();}
		return null;
	}
	*/
	
public SessionFactory GenerarFactoria(){
	try {
		sessionFactory = cfg.buildSessionFactory();
		//System.out.println("Factoria creada_>"+sessionFactory.toString());
	}
	catch (Exception e) {
		System.err.print(e);
	}
	return sessionFactory;
}

/*
 public void closeConnection(Connection conn)
	{
		try{conn.close();}catch(Exception e){}
	}
*/

public void closeSession(Session session){
	try{
		session.flush();
		session.close();
	}
	catch(Exception e){}
}

public void setProps(Configuration cfg){
	//System.out.println("**************Aqui hi arribo");
	this.cfg=cfg;
}

/*public void shutdown(){
	//System.out.println ("Parem les connexions");
      if (connectionPool!= null){
              try{
            	  connectionPool.close();
                  //System.out.println("He tancat el pool");
              }catch(Exception ex){
            	  System.out.println("No s'ha pogut tancar el pool");
              }
      }
}*/
	

}
