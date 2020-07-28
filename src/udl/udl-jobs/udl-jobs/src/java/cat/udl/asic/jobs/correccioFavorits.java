package cat.udl.asic.jobs;


import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


//serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;

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


/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */


public class correccioFavorits implements Job {

	static Logger log = Logger.getLogger(
			correccioFavorits.class.getName());

	// consulta d'identificadors d'usuari amb preferències
	static String  sqlSelectUserIds = "SELECT distinct p.preferences_id USERID, u.eid USEREID FROM sakai_preferences p, sakai_user_id_map u WHERE p.preferences_id = u.user_id";
		
	private SecurityService instanciaSecurityService;
	private SiteService instanciaSiteService;
	private SqlService instanciaSqlService;
    private PreferencesService instanciaPreferencesService;

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
		
        log.debug("Executem init() de correccioFavorits");
        
		// Fem que la classe actual no hereti les propietats de rootLogger
		log.setAdditivity(false);
	}
	
	public void setSecurityService(SecurityService instanciaSecurityService) {
	    this.instanciaSecurityService = instanciaSecurityService;
	}
	
	public void setSiteService(SiteService instanciaSiteService) {
	    this.instanciaSiteService = instanciaSiteService;
	}
	
	public void setSqlService(SqlService instanciaSqlService) {
	    this.instanciaSqlService = instanciaSqlService;
	}
    
    public void setPreferencesService(PreferencesService instanciaPreferencesService) {
	    this.instanciaPreferencesService = instanciaPreferencesService;
	}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca: correcció de favorits");		
		
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;
		String SEEN_SITES_PROPERTY = "autoFavoritesSeenSites";
		String userid = "";
		String usereid = "";
		//Activem l'opció per permetre canviar dades
		enableSecurityAdvisor();

        try { 
    		
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		sakaiConnection.setAutoCommit(false);
        		
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectUserIds);
        				    		     		
        		log.debug("correccioFavorits. Executem la consulta per recuperar els identificadors d-usuari");        		
        		ResultSet rst = sakaiStatement.executeQuery();        		

        		while (rst.next()) {
    				
        			userid = rst.getString("USERID"); 	
        			usereid = rst.getString("USEREID"); 
    				log.info("correccioFavorits. Tractem "+usereid);
    				
    				PreferencesEdit edit = instanciaPreferencesService.edit(userid);
    				ResourcePropertiesEdit props = edit.getPropertiesEdit(org.sakaiproject.user.api.PreferencesService.SITENAV_PREFS_KEY);

    				List<Site> userSites = instanciaSiteService.getUserSites(false,userid);
    				props.removeProperty(SEEN_SITES_PROPERTY);
    				for (Site userSite : userSites) {
    					String siteid = userSite.getId();
    					log.debug("correccioFavorits. Usuari "+usereid+" site "+siteid);
    					props.addPropertyToList(SEEN_SITES_PROPERTY,siteid);
    				}
    				log.debug("correccioFavorits. Desem els canvis a les prefs ");
    				instanciaPreferencesService.commit(edit);
        		}
        }
        catch (SQLException e) {
	    	log.error("EXCEPCIO SQL(correccioFavorits) ");
	    	log.error("EXCEPCIO SQL "+e);
	    }
	    catch (Exception ex) {
	        log.error("EXCEPCIO (correccioFavorits) ");
	        log.error(ex.getClass().getName() + " :: " + ex.getMessage());
	    }
	    finally {
	    	disableSecurityAdvisor();
	    	try {
	            if(sakaiStatement != null) sakaiStatement.close();
	        } catch (SQLException e) {
	        	log.error("EXCEPCIO SQL(correccioFavorits) al tancar statement");
	            log.error("SQLException: " +e);
	        }
	       
	        if(sakaiConnection != null) instanciaSqlService.returnConnection(sakaiConnection);	
	    }
        log.info("Tasca executada: correccioFavorits");	
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
