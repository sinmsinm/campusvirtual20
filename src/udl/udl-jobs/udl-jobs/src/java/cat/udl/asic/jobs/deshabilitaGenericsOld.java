package cat.udl.asic.jobs;


import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


//serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserNotDefinedException;


//classes que necessitem
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Locale;
import java.io.InputStream;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import java.net.MalformedURLException;
import java.io.IOException;
import org.sakaiproject.tool.api.Session;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;


//codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.AcademicSession;

/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */


public class deshabilitaGenericsOld implements Job {

	static Logger log = Logger.getLogger(
			deshabilitaGenericsOld.class.getName());

	// consulta d'usuaris genèrics a crear
	static String  sqlSelectGenericUsers = "select user_id usuari from sakai_user where first_name like 'Usuari Gen%'"
	+ "and user_id not in (select user_id from sakai_user_id_map where eid like '%-1617%')";
	
	// update de type a disabledGeneric	
	static String  sqlUpdateEstat = "update sakai_user set type = 'disabledGeneric' where user_id = ? ";

	private SecurityService instanciaSecurityService;
	private UserDirectoryService instanciaUserDirectoryService;
	private SqlService instanciaSqlService;

	private SecurityAdvisor securityAdvisor;
	
	// primer de tot cal donar permisos
	public void init() {

		// Create our security advisor.
		securityAdvisor = new SecurityAdvisor() {
			public SecurityAdvice isAllowed(String userId, String function,
					String reference) {
				return SecurityAdvice.ALLOWED;
			}
		};
		/* Preparem entorn per que log4j trobi el fitxer de configuració al directori /conf del tomcat */
		PropertyConfigurator.configure(System.getProperty("catalina.home") + "/conf/log4j.properties");
		
        log.debug("Executem init() de deshabilitaGenericsOld");
        
		// Fem que la classe actual no hereti les propietats de rootLogger
		log.setAdditivity(false);
	}
	
	public void setSecurityService(SecurityService instanciaSecurityService) {
	    this.instanciaSecurityService = instanciaSecurityService;
	}
	
	public void setUserDirectoryService(UserDirectoryService instanciaUserDirectoryService) {
	    this.instanciaUserDirectoryService = instanciaUserDirectoryService;
	}
	
	public void setSqlService(SqlService instanciaSqlService) {
	    this.instanciaSqlService = instanciaSqlService;
	}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca: deshabilitació dels usuaris generics Old");		
		
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;

		String usuari= "";		
		
		//Activem l'opció per permetre canviar dades
		enableSecurityAdvisor();

        try { 
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		sakaiConnection.setAutoCommit(false);
        		
        		log.debug("deshabilitaGenericsOld. Recuperem els usuaris generics que deshabilitarem");
        		
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectGenericUsers);
        				       		   		
        		log.debug("deshabilitaGenericsOld. Executem la consulta per recuperar els usuaris generics");        		
        		ResultSet rst = sakaiStatement.executeQuery();        		

        		while (rst.next()) {
    				
        			PreparedStatement sakaiStatement2 = null;
    				usuari = rst.getString("usuari"); 		
    				// Get a locked user object for editing. Must commitEdit() to make official, or cancelEdit() when done!
    				// throws UserNotDefinedException, UserPermissionException, UserLockedException;
    				UserEdit genericEdit = instanciaUserDirectoryService.editUser(usuari);
    				genericEdit.getProperties().addProperty("disabled", "true");
    				String userEid = instanciaUserDirectoryService.getUserEid(usuari);
    				log.debug("deshabilitaGenericsOld. Usuari "+userEid+" deshabilitat");
    				// Commit the changes made to a UserEdit object, and release the lock. 
    				// The UserEdit is disabled, and not to be used after this call.
    				// throws UserAlreadyDefinedException;
    				instanciaUserDirectoryService.commitEdit(genericEdit);
    				try {
    					// update de l'estat a 2 (usuari tractat) 
    					sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);   	  						
  						sakaiStatement2.setString(1, usuari);
  						sakaiStatement2.executeUpdate();
    	  				// després de cada actualització fem commit
    	  				sakaiConnection.commit();
    	  				sakaiStatement2.close();
    				} catch (SQLException e) {
    	            	log.error("EXCEPCIO SQL(deshabilitaGenericsOld) execució update usuari");
    	                log.error("SQLException: " +e);
    	            }
        		}
        }
        catch (SQLException e) {
	    	log.error("EXCEPCIO SQL(deshabilitaGenericsOld) ");
	    	log.error("EXCEPCIO SQL "+e);
	    }
	    catch (Exception ex) {
	        log.error("EXCEPCIO (deshabilitaGenericsOld) ");
	        log.error(ex.getClass().getName() + " :: " + ex.getMessage());
	    }
	    finally {
	    	disableSecurityAdvisor();
	    	try {
	            if(sakaiStatement != null) sakaiStatement.close();
	        } catch (SQLException e) {
	        	log.error("EXCEPCIO SQL(deshabilitaGenericsOld) al tancar statement");
	            log.error("SQLException: " +e);
	        }
	       
	        if(sakaiConnection != null) instanciaSqlService.returnConnection(sakaiConnection);	
	    }
        log.info("Tasca executada: deshabilitats usuaris generics antics");	
	}
	
	
	/**
	 * Setup a security advisor for this transaction
	 */
	private void enableSecurityAdvisor() {
		instanciaSecurityService.pushAdvisor(securityAdvisor);
	}

	/**
	 * Remove security advisor
	 */
	private void disableSecurityAdvisor() {
		instanciaSecurityService.popAdvisor();
	}
	
}
