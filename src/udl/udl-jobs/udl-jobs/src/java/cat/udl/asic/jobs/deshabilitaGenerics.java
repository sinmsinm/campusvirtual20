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


public class deshabilitaGenerics implements Job {

	static Logger log = Logger.getLogger(
			deshabilitaGenerics.class.getName());

	// consulta d'usuaris genèrics a crear
	static String  sqlSelectGenericUsers = "SELECT distinct usuari_generic usuari FROM udl_cm_estats_generics WHERE estat = 1 AND anyaca = ?";
	// consulta per comprovar que existeix l'usuari
	static String  sqlSelectCheckUser = "SELECT * FROM sakai_user WHERE user_id IN "
	+"(SELECT user_id FROM sakai_user_id_map WHERE eid = ?)";
	
	// update de l'estat a 2 (usuari deshabilitat) 	
	static String  sqlUpdateEstat = "UPDATE udl_cm_estats_generics SET ESTAT = 2 WHERE USUARI_GENERIC = ? ";
	// marquem l'usuari com a deshabilitat
	static String  sqlUpdateTipus = "UPDATE sakai_user SET type = 'disabledGeneric' WHERE  user_id IN "
	+"(SELECT user_id FROM sakai_user_id_map WHERE eid = ?)";
	
	private SecurityService instanciaSecurityService;
	private UserDirectoryService instanciaUserDirectoryService;
	private SqlService instanciaSqlService;
    private CourseManagementService instanciaCourseManagementService;

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
		
        log.debug("Executem init() de deshabilitaGenerics");
        
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
    
    public void setCourseManagementService(CourseManagementService instanciaCourseManagementService) {
	    this.instanciaCourseManagementService = instanciaCourseManagementService;
	}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca: deshabilitació dels usuaris generics");		
		
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;

		String usuari= "";		
		
		//Activem l'opció per permetre canviar dades
		enableSecurityAdvisor();

        try { 
    		
    			List anysAcademics = instanciaCourseManagementService.getCurrentAcademicSessions();
        		Iterator iterAnyAcad = anysAcademics.iterator();
        		AcademicSession academicSession  = (AcademicSession) iterAnyAcad.next();
        		String any_academic = academicSession.getDescription();
        		String term = academicSession.getEid();
        		if (any_academic.equals("9999"))
        			{
        				academicSession  = (AcademicSession) iterAnyAcad.next();
        				term = academicSession.getEid();
        				any_academic = academicSession.getDescription();
       				}

        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		sakaiConnection.setAutoCommit(false);
        		
        		log.debug("deshabilitaGenerics. Recuperem els usuaris generics que deshabilitarem");
        		
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectGenericUsers);
        		sakaiStatement.setString(1, term);
        				    		     		
        		log.debug("deshabilitaGenerics. Executem la consulta per recuperar els usuaris generics");        		
        		ResultSet rst = sakaiStatement.executeQuery();        		

        		while (rst.next()) {
    				
        			PreparedStatement sakaiStatementUser = null;
        			PreparedStatement sakaiStatement2 = null;
        			PreparedStatement sakaiStatement3 = null;
    				usuari = rst.getString("usuari"); 	
    				log.debug("deshabilitaGenerics. Comprovant que "+usuari+" existeix");
    				sakaiStatementUser = sakaiConnection.prepareStatement(sqlSelectCheckUser);
    				sakaiStatementUser.setString(1, usuari);
    				ResultSet rstUser = sakaiStatementUser.executeQuery();
    				boolean usuariExisteix = rstUser.next();
    				if (!usuariExisteix)
    				{
    					log.debug("deshabilitaGenerics. Usuari "+usuari+" no existeix");
    				}
    				else 
    				{
    					// Find the user id from a user eid  -- throws UserNotDefinedException
        				String userId = instanciaUserDirectoryService.getUserId(usuari);
        				// Get a locked user object for editing. Must commitEdit() to make official, or cancelEdit() when done!
        				// throws UserNotDefinedException, UserPermissionException, UserLockedException;
        				UserEdit genericEdit = instanciaUserDirectoryService.editUser(userId);
        				//password = createPasswd();
        				//genericEdit.setPassword(password);
        				// per disable
        				genericEdit.getProperties().addProperty("disabled", "true");
        				log.debug("deshabilitaGenerics. Usuari "+usuari+" deshabilitat");
        				// Commit the changes made to a UserEdit object, and release the lock. 
        				// The UserEdit is disabled, and not to be used after this call.
        				// throws UserAlreadyDefinedException;
        				instanciaUserDirectoryService.commitEdit(genericEdit);
    				}
    				try {
    					// update de l'estat a 2 (usuari tractat) 
    					sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);   	  						
  						sakaiStatement2.setString(1, usuari);
  						sakaiStatement2.executeUpdate();
    	  				// després de cada actualització fem commit
    	  				sakaiConnection.commit();
    	  				sakaiStatement2.close();
    				} catch (SQLException e) {
    	            	log.error("EXCEPCIO SQL(deshabilitaGenerics) execució update usuari");
    	                log.error("SQLException: " +e);
    	            }
    				if (usuariExisteix) {
    					try {
        					// update del tipus de l'usuari a deshabilitat
        					sakaiStatement3 = sakaiConnection.prepareStatement(sqlUpdateTipus);   	  						
      						sakaiStatement3.setString(1, usuari);
      						sakaiStatement3.executeUpdate();
        	  				// després de cada actualització fem commit
        	  				sakaiConnection.commit();
        	  				sakaiStatement3.close();
        				} catch (SQLException e) {
        					log.error("EXCEPCIO SQL(deshabilitaGenerics) execució update tipus");
        	                log.error("SQLException: " +e);
        				}			
    				}
        		}   		
        }
        catch (SQLException e) {
	    	log.error("EXCEPCIO SQL(deshabilitaGenerics) ");
	    	log.error("EXCEPCIO SQL "+e);
	    }
	    catch (Exception ex) {
	        log.error("EXCEPCIO (deshabilitaGenerics) ");
	        log.error(ex.getClass().getName() + " :: " + ex.getMessage());
	    }
	    finally {
	    	disableSecurityAdvisor();
	    	try {
	            if(sakaiStatement != null) sakaiStatement.close();
	        } catch (SQLException e) {
	        	log.error("EXCEPCIO SQL(deshabilitaGenerics) al tancar statement");
	            log.error("SQLException: " +e);
	        }
	       
	        if(sakaiConnection != null) instanciaSqlService.returnConnection(sakaiConnection);	
	    }
        log.info("Tasca executada: deshabilitats usuaris generics");	
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
