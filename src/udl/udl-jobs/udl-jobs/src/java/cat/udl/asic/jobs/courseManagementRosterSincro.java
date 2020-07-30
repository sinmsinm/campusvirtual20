package cat.udl.asic.jobs;

import java.util.regex.Pattern;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

//codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.authz.api.SecurityService;


/**
 *
 */
public class courseManagementRosterSincro implements Job{
	//private static final Log log = LogFactory.getLog(courseManagementRosterSincro.class);
 
	static Logger log = Logger.getLogger(
            courseManagementSincro.class.getName());
	
	private CourseManagementService courseManagementService;
	private AuthzGroupService authzGroupService;
	private SiteService siteService;
	private SessionManager sessionManager;
	private SqlService instanciaSqlService;
	private SecurityAdvisor securityAdvisor;
	private SecurityService instanciaSecurityService;
	
	private String termEidPropertyName;
	
	private String termEid; 
	
	static String  sqlSelectRealms = "select site_id from udl_cm_realms_to_update where estat = 0 and anyaca = ?"; 
	static String  sqlUpdateEstat = "update udl_cm_realms_to_update set estat = 1 where site_id = ? and anyaca = ?";
	
	protected static Pattern termEidPattern = Pattern.compile(".*(?i)term=");	

	
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

	    log.debug("Executem init() de courseManagementRosterSincro");

	    // Fem que la classe actual no hereti les propietats de rootLogger
		log.setAdditivity(false);
	}
	
	public void execute(JobExecutionContext context) {
		
		log.info("Executant la tasca de sincronitzacio realms");

		actAsAdmin();
				
		//recuperem term del nom del job
		String jobName = context.getJobDetail().getKey().getName();
		if (jobName != null) {
			String[] splitJobName = termEidPattern.split(jobName);			
			if (splitJobName.length == 2) {
				setTermEid(splitJobName[1]);
			}
		}						

		if (termEid == null) {
			// Default is to synchronize sites connected to all academics sessions
			// which are marked as current.
			List<AcademicSession> academicSessions = courseManagementService.getCurrentAcademicSessions();
			for (AcademicSession academicSession : academicSessions) {
				refreshSitesForAcademicSession(academicSession.getEid());
			}
		} else {
			
			refreshSitesForAcademicSession(termEid);
		}
	}
	
	protected void refreshSitesForAcademicSession(String academicSessionEid) {		
	
		log.info("Synchronizing site groups for term=" + academicSessionEid);
					
		//recuperar sites de taula udl_cm
		List<Site> sites_to_update = new ArrayList<Site>();
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;		
		
        try {
        		String site_id = "";        		
        		sakaiConnection = instanciaSqlService.borrowConnection();         		
        		sakaiConnection.setAutoCommit(false);        		
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectRealms);
        		sakaiStatement.setString(1,academicSessionEid);  
        		ResultSet rst = sakaiStatement.executeQuery();
        		
        		while (rst.next()) {        			
        			site_id = rst.getString("SITE_ID");        			        		
        			
        			try{
        				Site site_realm = siteService.getSite(site_id);
        				sites_to_update.add(site_realm);
        			}
        			catch(IdUnusedException e){
        				log.info("No ha trobat cap site amb id =" + site_id);
        			}
       		}       
        		
		} catch (Exception e) {  
			log.error("EXCEPCIO (refreshSitesForAcademicSession: Construint la llista de sites a updatar)");
			log.error(e.getClass().getName() + " :: " + e.getMessage());
		}        
        
        for (Site site : sites_to_update) {
		// Currently there's no exposed way to refresh provided groups for a site. Instead,
		// it occurs as a side-effect of calling "save" on the site's associated AuthzGroup.
		// Sync The groups
			
        Collection<Group> groups = site.getGroups();
        // Currently there's no exposed way to refresh provided groups for a site. Instead,
        // it occurs as a side-effect of calling "save" on the site's associated AuthzGroup.
                    
       try {
        	    
                for (Group group : groups) {
                		log.info("Trying to sync group id for site " + site.getId() +" and group " + group.getId());
                        AuthzGroup authzGroup = authzGroupService.getAuthzGroup("/site/" + site.getId() + "/group/" + group.getId());
                        authzGroupService.save(authzGroup);
                }
                log.info("Trying to sync for site " + site.getId());
                AuthzGroup authzGroup = authzGroupService.getAuthzGroup("/site/" + site.getId());
                authzGroupService.save(authzGroup);

                //Un cop actualitzat els realms dels grups + site -> posem estat a 1
				try {	  				
					PreparedStatement sakaiStatement2 = null;
					sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);        		            					
					sakaiStatement2.setString(1,site.getId());
					sakaiStatement2.setString(2,academicSessionEid);  
					sakaiStatement2.executeUpdate();    	    	  					            				
						    	    	  				
	  				// després de cada actualització fem commit
	  				sakaiConnection.commit();	    	    	  			
	  	            sakaiStatement2.close();
	  	            
	  			}catch (SQLException e) {
	            	log.error("EXCEPCIO (Update UDL_CM_REALMS_TO_UPD "+site.getId()+")");
	                log.error("SQLException: " +e);
	            }	
        
        } catch (GroupNotDefinedException e) {
                        log.warn("AuthzGroup for site " + site.getId() + " not found", e);
                        continue;
        } catch (AuthzPermissionException e) {
                        log.warn("Unable to synchronize AuthzGroup for site " + site.getId(), e);
                        continue;
        }catch (Exception ex){
        		ex.printStackTrace ();
        		continue;
        }
              	
       }
        //abans d'acabar tanquem statement i connexió
        try {
            if(sakaiStatement != null) sakaiStatement.close();
        } catch (SQLException e) {
        	log.error("EXCEPCIO SQL al tancar statement");
         log.error("SQLException: " +e);
        }          
        if(sakaiConnection != null) instanciaSqlService.returnConnection(sakaiConnection);	
            
        /*** FI CANVI UDL ***/
        
	} 

	/**
	 * Convenience routine to support the frequent testing need to switch authn/authz identities.
	 * TODD Find some central place for this frequently-needed helper logic. It can easily be made
	 * static.
	 */
	public void actAsAdmin() {
		String userId = "admin";
		Session session = sessionManager.getCurrentSession();
		session.setUserEid(userId);
		session.setUserId(userId);
		authzGroupService.refreshUser(userId);
	}
	
	public void setSqlService(SqlService instanciaSqlService) {
	    this.instanciaSqlService = instanciaSqlService;
	}
	public void setSecurityService(SecurityService instanciaSecurityService) {
	    this.instanciaSecurityService = instanciaSecurityService;
	}
	public void setCourseManagementService(CourseManagementService courseManagementService) {
		this.courseManagementService = courseManagementService;
	}

	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	private void enableSecurityAdvisor() {
		instanciaSecurityService.pushAdvisor(securityAdvisor);
	}

	/**
	 * Remove security advisor
	 */
	private void disableSecurityAdvisor() {
		instanciaSecurityService.popAdvisor();
	}
	
	/**
	 * @param termEidPropertyName
	 *            site property to match against an academic session ID; THIS IS
	 *            NOT CURRENTLY PART OF A DOCUMENTED SERVICE API
	 */
	public void setTermEidPropertyName(String termEidPropertyName) {
		this.termEidPropertyName = termEidPropertyName;
	}

	/**
	 * @param termEid
	 *            academic session to synchronize against; if left null, all
	 *            current academic sessions are checked
	 */
	public void setTermEid(String termEid) {
		this.termEid = termEid;
	}
}
