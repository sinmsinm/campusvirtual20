package cat.udl.asic.jobs;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


//import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
// import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.quartz.JobExecutionException;

import org.sakaiproject.authz.api.AuthzGroup;
// serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;

import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.AcademicSession;

import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.user.api.UserNotDefinedException;

import org.sakaiproject.section.api.SectionManager;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.Site;

import org.sakaiproject.entity.api.EntityManager;

// codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;

import java.util.Random;
/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */


public class desconnexioCM implements Job {

	static Logger log = Logger.getLogger(
			desconnexioCM.class.getName());

	static String  sqlSelectAssDesconnectar = "SELECT DISTINCT SGP.SITE_ID ID_SITE FROM SAKAI_SITE_GROUP_PROPERTY SGP, "  
				  + "(SELECT DISTINCT SITE_ID FROM SAKAI_SITE_PROPERTY WHERE "
				  + " TO_CHAR(NAME) = 'sections_externally_maintained' "
				  + " AND TO_CHAR(VALUE) = 'true') sites_externally_managed , "
				  + "(SELECT DISTINCT SITE_ID FROM SAKAI_SITE_PROPERTY WHERE "
				  + " TO_CHAR(NAME) = 'term' "
				  + " AND TO_CHAR(VALUE) = ? )sites_term "
				  + " WHERE " 
				  + " SGP.SITE_ID =	sites_externally_managed.SITE_ID " 
				  + " AND SGP.SITE_ID = sites_term.SITE_ID " 
				  + " AND TO_CHAR(NAME) = 'sections_category'" 
				   //Els valors de les sections categories es podrien agafar potser de la taula UDL_CM_SEC_CATEGORY
				  + " AND (TO_CHAR(VALUE) IN ('GRUP_MAT','GRUP_PM','GRUP_TIT','GRUP_TA','GRUP_ET')  or TO_CHAR(VALUE) like 'GRUP_ET%')";

	
	private SectionManager instanciaSectionManager;
	private SecurityService instanciaSecurityService;
	private SiteService instanciaSiteService;
	private CourseManagementService instanciaCourseManagementService;
	private SqlService instanciaSqlService;	
    protected EntityManager entityManager;
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
		
        log.debug("Executem init() de desconnexioCM");
        
		// Fem que la classe actual no hereti les propietats de rootLogger
		log.setAdditivity(false);
	}
	
	public void setSectionManager(SectionManager instanciaSectionManager) {
	    this.instanciaSectionManager = instanciaSectionManager;
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
	
	public void setCourseManagementService(CourseManagementService instanciaCourseManagementService) {
	    this.instanciaCourseManagementService = instanciaCourseManagementService;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca de desconnexio de les sections");
		Connection sakaiConnection = null;		
		
		PreparedStatement sakaiStatement1 = null; 
		
			try {	        	
	  			
				List anysAcademics = instanciaCourseManagementService.getCurrentAcademicSessions();
        		Iterator iterAnyAcad = anysAcademics.iterator();
        		AcademicSession academicSession  = (AcademicSession) iterAnyAcad.next();
        		String any_academic = academicSession.getDescription();
        		String anyAcademic = academicSession.getEid();
        		if (any_academic.equals("9999")) //doctorat no s'ha de desconnectar
        		{
        			academicSession  = (AcademicSession) iterAnyAcad.next();
        			anyAcademic = academicSession.getEid();
        			any_academic = academicSession.getDescription();
        		}
				
							
        		// recuperem la informació de les sections a desconnectar      		
      			log.debug("Agafem una connexio");
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        				       
        		sakaiStatement1 = sakaiConnection.prepareStatement(sqlSelectAssDesconnectar);
        		sakaiStatement1.setString(1, anyAcademic);
        		
        		log.debug("Executem la consulta per recuperar les assignatures a desconnectar");        		
        		ResultSet rst = sakaiStatement1.executeQuery();
        		
        		while (rst.next()) {
        				
        			String idSite = rst.getString("ID_SITE");        				        					    	  		
    	  			String id_llarg_site = desconnectarSections(idSite);

            		if (id_llarg_site.equals("error"))
            		{
            			log.info("Error al desconnectar les sections del site "+idSite);
            		}
            		else{	     
            			log.info("Espai "+idSite+" desconnectat correctament");	 
            		}        					
			            			    
	    	  	}
        		rst.close();
        		sakaiStatement1.close();              		
        }		
        
        catch (SQLException e) {
        	log.error("EXCEPCIO SQL(desconnexioCM) ");
        	log.error("EXCEPCIO SQL "+e);
        }
        catch (Exception ex) {
            log.error("EXCEPCIO (desconnexioCM) ");
            log.error(ex.getClass().getName() + " :: " + ex.getMessage());
        }
        finally {
        	try {
                if(sakaiStatement1 != null) sakaiStatement1.close();
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
	

	private String desconnectarSections(String idEspai) throws JobExecutionException {
		
		log.debug("+++ desconnectarSections +++");
		log.debug("Espai "+idEspai);		
		
		String courseUuid = "/site/"+idEspai;
		
		try {
				log.debug("Habilitem el security advisor");
				System.out.println("Habilitem el security advisor");
				enableSecurityAdvisor();
				
				/*DESCONNECTEM SECTIONS*/
				log.debug("Set Externally Managed espai: "+courseUuid);	
				System.out.println("Set Externally Managed espai: "+courseUuid);
				
				instanciaSectionManager.setExternallyManaged(courseUuid, false);		    		   	    			
			
				/*AFEGIM LA PROPIETAT disconnection_date AL SITE*/
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		    	Date today = Calendar.getInstance().getTime();        
		    	String reportDate = df.format(today);		    			    	

		    	Reference ref = entityManager.newReference(courseUuid);
				String siteId = ref.getId();							

				Site site;
				try {
					site = instanciaSiteService.getSite(siteId);														
					ResourceProperties props = site.getProperties();
					// Update the site									
					props.addProperty("disconnection_date", reportDate);
					
	                site.setProviderGroupId(null);

	                // Add members to the site based on the current (provided) memberships
	                Set members = site.getMembers();
	                for(Iterator memberIter = members.iterator(); memberIter.hasNext();) {
	                    Member member = (Member)memberIter.next();
	                    if(member.isProvided()) {
	                        site.addMember(member.getUserId(), member.getRole().getId(), member.isActive(), false);
	                    }
	                } 

					try {
						instanciaSiteService.save(site);
						if(log.isDebugEnabled()) log.debug("Saved site " + site.getTitle());					
					} catch (IdUnusedException ide) {
						log.error("Error saving site... could not find site " + site, ide);
					} catch (PermissionException pe) {
						log.error("Error saving site... permission denied for " + site, pe);
					}
				} catch (IdUnusedException e) {
					throw new RuntimeException("Can not find site " + courseUuid, e);
				}
								
						
			} catch (Exception e) {  
				log.error("EXCEPCIO (desconnectarSections)");
				log.error(e.getClass().getName() + " :: " + e.getMessage());
				return "error";
			} finally {
				log.debug("Deshabilitem el security advisor");
				disableSecurityAdvisor();
			}

		return courseUuid;
	} 

	
}
