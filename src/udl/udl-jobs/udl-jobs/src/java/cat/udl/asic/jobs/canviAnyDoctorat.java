package cat.udl.asic.jobs;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

// import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.quartz.JobExecutionException;

// serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.db.api.SqlService;

import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.authz.api.GroupProvider;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.authz.api.AuthzGroup;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

// codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;
//import org.apache.commons.lang.StringUtils;


/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */


public class canviAnyDoctorat implements Job {

	static Logger log = Logger.getLogger(
			canviAnyDoctorat.class.getName());

	static String  sqlSelectEspaisDOT = "SELECT DISTINCT CODI_ASS, CODI_GRUP FROM UDL_CM_ESTATS_GRUPS_DOT WHERE ESTAT = 0";
	static String  sqlUpdateEstatDOT = "UPDATE UDL_CM_ESTATS_GRUPS_DOT SET ESTAT = 2 WHERE CODI_GRUP = ?";

		
	// public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm";

	private SecurityService instanciaSecurityService;
	private SiteService instanciaSiteService;
	private AuthzGroupService instanciaAuthzGroupService;
	private CourseManagementService instanciaCourseManagementService;
	private GroupProvider instanciaGroupProvider;
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

	    log.debug("Executem init() de canviAnyDoctorat");

		// Fem que la classe actual no hereti les propietats de rootLogger
		log.setAdditivity(false);
	}
	
	public void setSecurityService(SecurityService instanciaSecurityService) {
	    this.instanciaSecurityService = instanciaSecurityService;
	}
	
	public void setSiteService(SiteService instanciaSiteService) {
	    this.instanciaSiteService = instanciaSiteService;
	}
	
	public void setAuthzGroupService(AuthzGroupService instanciaAuthzGroupService) {
	    this.instanciaAuthzGroupService = instanciaAuthzGroupService;
	}
	
	public void setCourseManagementService(CourseManagementService instanciaCourseManagementService) {
	    this.instanciaCourseManagementService = instanciaCourseManagementService;
	}	
	
	public void setGroupProvider(GroupProvider instanciaGroupProvider) {
	    this.instanciaGroupProvider = instanciaGroupProvider;
	}
	
	public void setSqlService(SqlService instanciaSqlService) {
	    this.instanciaSqlService = instanciaSqlService;
	}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca de canvi d'any de doctorat");
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;		
		
        try {
        		log.debug("Habilitem el security advisor");
        		enableSecurityAdvisor();
        		
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		// posem a false per a que faci commit a cada actualització d'estat de la taula
        		sakaiConnection.setAutoCommit(false);
        		
        		
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectEspaisDOT);
        		        		        	        
        		log.debug("Executem la consulta per recuperar els espais de doctorat");
        		ResultSet rst = sakaiStatement.executeQuery();    
        		boolean deleteGroup = false;
				ArrayList<Group> grToDel = null;
        		        		        		        
        		while (rst.next()) {
        				String idSite = rst.getString("CODI_ASS");
        				String idSection = rst.getString("CODI_GRUP");
        				log.debug("Tractem "+idSection+" de l'espai "+idSite);
        				Site site = instanciaSiteService.getSite(idSite);
        				String proveidorSite = site.getProviderGroupId();
        				String nouProveidorSite = null;
        				log.debug("Proveïdor actual del site "+proveidorSite);
        				if (proveidorSite.contains(idSection+"+")) {
        					nouProveidorSite = proveidorSite.replace(idSection+"+", "");
        				}
        				else if (proveidorSite.contains("+"+idSection)) {
        					nouProveidorSite = proveidorSite.replace("+"+idSection, "");
        				}
        				else {
        					log.debug("El proveïdor del site no conté la section eliminada");
        				}
        				if (nouProveidorSite != null) {
        					log.debug("El nou proveïdor és "+nouProveidorSite);
        					try {
        						String realm = site.getReference();
            					log.debug("Editem el realm "+realm+" del site "+idSite);
            					AuthzGroup realmEdit = instanciaAuthzGroupService.getAuthzGroup(realm);
            					realmEdit.setProviderGroupId(nouProveidorSite);
            					instanciaAuthzGroupService.save(realmEdit);
        					}
        					catch (Exception e) {  
        						log.error("EXCEPCIO (Error amb l'edició del realm del site)");
        						log.error(e.getClass().getName() + " :: " + e.getMessage());
        					}
        				}
        				log.debug("Eliminem el grup i el realm que corresponen a la secció donada de baixa");
        				for (Iterator iGroups = site.getGroups().iterator(); iGroups.hasNext();) {
        					Group group = (Group) iGroups.next();
        					String refGrup = group.getReference();
        					try {
        						AuthzGroup gRealm = instanciaAuthzGroupService.getAuthzGroup(refGrup);
        						String gProviderId = gRealm.getProviderGroupId();
        						if (gProviderId != null && !gProviderId.isEmpty()) {
        							if (gProviderId.equals(idSection)) {
        								log.debug("Aquest grup té com a proveïdor la secció "+idSection);
        								if (!instanciaCourseManagementService.isSectionDefined(idSection)) {
        									log.debug("La secció "+idSection+" ja no existeix a CM.");
        									log.debug("Eliminem el realm "+refGrup);
            								instanciaAuthzGroupService.removeAuthzGroup(refGrup);
            								deleteGroup = true;
            								if(grToDel == null)
            									grToDel = new ArrayList<Group>();
            								grToDel.add(group);
        								}
        							}
        						}
        					}
        					catch (Exception ex1) {
        						log.error("EXCEPCIO (Amb el grup "+refGrup+")");
        		                log.error("Exception: " +ex1);
        					}
        				}
        				try {
        					log.debug("Desem el site "+idSite);
            				instanciaSiteService.save(site);
            				if(deleteGroup){
            					log.debug("Eliminem el grup del site");
            					for (Group gd : grToDel){
            						site.removeGroup(gd);
            					}
            					instanciaSiteService.save(site);
            					grToDel.clear();
            				 }
            				deleteGroup = false;
        				}
        				catch (Exception ex2) {
    						log.error("EXCEPCIO (Al desar "+idSite+")");
    		                log.error("Exception: " +ex2);
    					}
        				
        				
        				//Eliminem la section de l'espai de coordinació de doctorat
        				
    	  				String idCoordDOT = "coordDOT";
        				idSection = rst.getString("CODI_GRUP");
        				log.debug("Tractem "+idSection+" de l'espai "+idCoordDOT);
        				site = instanciaSiteService.getSite(idCoordDOT);
        				proveidorSite = site.getProviderGroupId();
        				nouProveidorSite = null;
        				log.debug("Proveïdor actual del site "+proveidorSite);
        				
        				if (proveidorSite.contains(idSection+"+")) {
        					nouProveidorSite = proveidorSite.replace(idSection+"+", "");
        				}
        				else if (proveidorSite.contains("+"+idSection)) {
        					nouProveidorSite = proveidorSite.replace("+"+idSection, "");
        				}
        				else {
        					log.debug("El proveïdor del site no conté la section eliminada");
        				}
        				if (nouProveidorSite != null) {
        					log.debug("El nou proveïdor és "+nouProveidorSite);
        					try {
        						String realm = site.getReference();
            					log.debug("Editem el realm "+realm+" del site "+idCoordDOT);
            					AuthzGroup realmEdit = instanciaAuthzGroupService.getAuthzGroup(realm);
            					realmEdit.setProviderGroupId(nouProveidorSite);
            					instanciaAuthzGroupService.save(realmEdit);
        					}
        					catch (Exception e) {  
        						log.error("EXCEPCIO (Error amb l'edició del realm del site)");
        						log.error(e.getClass().getName() + " :: " + e.getMessage());
        					}
        				}
        				log.debug("Eliminem el grup i el realm que corresponen a la secció donada de baixa");
        				for (Iterator iGroups = site.getGroups().iterator(); iGroups.hasNext();) {
        					Group group = (Group) iGroups.next();
        					String refGrup = group.getReference();
        					try {
        						AuthzGroup gRealm = instanciaAuthzGroupService.getAuthzGroup(refGrup);
        						String gProviderId = gRealm.getProviderGroupId();
        						if (gProviderId != null && !gProviderId.isEmpty()) {
        							if (gProviderId.equals(idSection)) {
        								log.debug("Aquest grup té com a proveïdor la secció "+idSection);
        								if (!instanciaCourseManagementService.isSectionDefined(idSection)) {
        									log.debug("La secció "+idSection+" ja no existeix a CM.");
        									log.debug("Eliminem el realm "+refGrup);
            								instanciaAuthzGroupService.removeAuthzGroup(refGrup);
            								deleteGroup = true;
            								if(grToDel == null)
            									grToDel = new ArrayList<Group>();
            								grToDel.add(group);
        								}
        							}
        						}
        					}
        					catch (Exception ex1) {
        						log.error("EXCEPCIO (Amb el grup "+refGrup+")");
        		                log.error("Exception: " +ex1);
        					}
        				}
        				try {
        					log.debug("Desem el site "+idCoordDOT);
            				instanciaSiteService.save(site);
            				if(deleteGroup){
            					log.debug("Eliminem el grup del site");
            					for (Group gd : grToDel){
            						site.removeGroup(gd);
            					}
            					instanciaSiteService.save(site);
            					grToDel.clear();
            				 }
            				deleteGroup = false;
        				}
        				catch (Exception ex2) {
    						log.error("EXCEPCIO (Al desar "+idCoordDOT+")");
    		                log.error("Exception: " +ex2);
    					}
        				
        				        				
        				//Si tot ha anat bé posem estat a 1
                		try {	  				
            					PreparedStatement sakaiStatement2 = null;
            					sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstatDOT);        		            					
            					sakaiStatement2.setString(1,idSection);  
            					sakaiStatement2.executeUpdate();    	    	  					            				
            							    	    	  				
            		  			// després de cada actualització fem commit
            		  			sakaiConnection.commit();	    	    	  			
            		              sakaiStatement2.close();
            		  	            
            		  	}catch (SQLException e) {
            		            	log.error("EXCEPCIO (Update UDL_CM_ESTATS_GRUPS_DOT "+idSite+")");
            		                log.error("SQLException: " +e);
            		          }		
	    	  	}
        }
        catch (Exception ex) {
            log.error("EXCEPCIO (canviAnyDoctorat) ");
            log.error(ex.getClass().getName() + " :: " + ex.getMessage());
            return;
        }
        finally {
        	log.debug("Deshabilitem el security advisor");
			disableSecurityAdvisor();
        	try {
                if(sakaiStatement != null) sakaiStatement.close();
            } catch (SQLException e) {
            	log.error("EXCEPCIO SQL al tancar statement");
                log.error("SQLException: " +e);
            }          
            if(sakaiConnection != null) instanciaSqlService.returnConnection(sakaiConnection);	
        }
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