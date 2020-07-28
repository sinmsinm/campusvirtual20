package cat.udl.asic.jobs;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

// import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.quartz.JobExecutionException;

// serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.db.api.SqlService;

import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.authz.api.GroupProvider;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.site.api.Site;
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
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;


/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */


public class espaisTitulacioSincro implements Job {

	static Logger log = Logger.getLogger(
            espaisTitulacioSincro.class.getName());

	static String  sqlSelectETCrear = "SELECT CODI_ET FROM UDL_CM_ESTATS_ET "
					+ " WHERE ANYACA = ? "
					+ " AND ESTAT = 0 " ; 
	

	static String  sqlUpdateEstat = "UPDATE UDL_CM_ESTATS_ET "
					+ " SET ESTAT = 1, DATA_CREACIO_ET = ?	"
					+ " WHERE ANYACA = ? "
					+ " AND CODI_ET= ? ";
	
	private int iSiteTitleMaxLength = 90;

	private static String plantillaId= "plantillaET";
	
	public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm";
	
	private SecurityService instanciaSecurityService;
	private SiteService instanciaSiteService;
	private AuthzGroupService instanciaAuthzGroupService;
	private CourseManagementService instanciaCourseManagementService;
	private GroupProvider instanciaGroupProvider;
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

	    log.debug("Executem init() de espaisTitulacioSincro");

		// Fem que la classe actual no hereti les propietats de rootLogger
		log.setAdditivity(false);
	}
	
	public void setSecurityService(SecurityService instanciaSecurityService) {
	    this.instanciaSecurityService = instanciaSecurityService;
	}
	
	public void setSiteService(SiteService instanciaSiteService) {
	    this.instanciaSiteService = instanciaSiteService;
	}
	
	public void setCourseManagementService(CourseManagementService instanciaCourseManagementService) {
	    this.instanciaCourseManagementService = instanciaCourseManagementService;
	}
	
	public void setAuthzGroupService(AuthzGroupService instanciaAuthzGroupService) {
	    this.instanciaAuthzGroupService = instanciaAuthzGroupService;
	}
	
	public void setGroupProvider(GroupProvider instanciaGroupProvider) {
	    this.instanciaGroupProvider = instanciaGroupProvider;
	}
	
	public void setUserDirectoryService(UserDirectoryService instanciaUserDirectoryService) {
	    this.instanciaUserDirectoryService = instanciaUserDirectoryService;
	}
	
	public void setSqlService(SqlService instanciaSqlService) {
	    this.instanciaSqlService = instanciaSqlService;
	}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca de creació de espais de titulació usant CM");
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;		
		
        try {
        		String eidCourseOffering = "";     
        		// recuperem l'any acadèmic
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
				
        		
        		// recuperem la informació d'assignatures per les quals cal crear espais
        		
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		// posem a false per a que faci commit a cada actualització d'estat de la taula
        		sakaiConnection.setAutoCommit(false);
        		
        		
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectETCrear);
        		sakaiStatement.setString(1, term);
        		        	        	
        		log.debug("Executem la consulta per recuperar les assignatures que tenen estat 0");
        		ResultSet rst = sakaiStatement.executeQuery();        		
        		        		        		        
        		while (rst.next()) {
	    	  			eidCourseOffering = rst.getString("CODI_ET");
	    	  			CourseOffering oferta = instanciaCourseManagementService.getCourseOffering(eidCourseOffering);
	    	  				    	  		
	    	  			String idSite = "coord"+ eidCourseOffering.substring(3)+"-"+any_academic;
	    	  			
	    	  			String nomEspai = oferta.getTitle();
	    	  			int llargada = nomEspai.length();
	    	  			if ( llargada > iSiteTitleMaxLength ) {
	    	  				nomEspai = nomEspai.substring(0, 70); //agafem els primers 70 caràcters,
	    	  				nomEspai = nomEspai.concat("...");
	    	  				nomEspai = nomEspai.concat(oferta.getTitle().substring(llargada-20));
	    				}	    	  				    	  				    	  				    	  
	    	  			
	    	  			// creem l'espai corresponent al course offering recuperat	    	  			
	    	  			String retorn = createSite(idSite,nomEspai, term);
	            		if (retorn.equals("error"))
	            		{
	            			log.info("Error al crear el site "+idSite);
	            		}
	            		else{
	            			log.info("Espai de comunicació "+idSite+" creat correctament");
	            			log.debug("Afegim els proveïdors corresponents");
	            			List<String> llistaProveidors = new Vector<String>();
	            			Set sections = instanciaCourseManagementService.getSections(eidCourseOffering);
	        				Iterator iter = sections.iterator();
	        				while (iter.hasNext()) {
	        							Section seccio = (Section) iter.next();
	        							String categoria = seccio.getCategory();
	        							// a l'espai només afegim els grups d'espais de titulacio
	        							if (categoria.startsWith("GRUP_ET") ) {
	        								//log.debug("Afegim al site la secció: "+seccio.getEid());
	        								llistaProveidors.add(seccio.getEid());
	        							}
	        							
	        				}
	            			String retorn2 = addSiteProviders(idSite,llistaProveidors);
	            			if (retorn2.equals("error"))
	                		{
	                			log.info("Error al crear els  grups de "+idSite);
	                			log.info("Esborrem l'espai de comunicació  "+idSite);
	            				try{	            					
	            					log.debug("Habilitem el security advisor");
	            					enableSecurityAdvisor();
	            					Site site = instanciaSiteService.getSite(idSite);
	            					instanciaSiteService.removeSite(site);
	            				} catch (Exception e) {  
	            					log.error("EXCEPCIO (esborrant espai de comunicació "+idSite+")");
	            					log.error(e.getClass().getName() + " :: " + e.getMessage());	            					
	            				} finally {
	            				log.debug("Deshabilitem el security advisor");
	            				disableSecurityAdvisor();
	            				}
	                		}
	            			else if (retorn2.equals("correcte")) {
	            				log.info("Grups de "+idSite+" afegits correctament");
	            				//addSiteManager(strSiteId, SITE_CONTACT_NAME, SITE_CONTACT_EMAIL);
	            				log.debug("Treiem usuari admin del site");
	            				removeUserFromSite(idSite, "admin");
	            				log.debug("Actualitzem a estat 1 a la taula de CM");
	            				
	            				//Agafem la data actual i la convertim a string	            				
	            				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	            				Date data = new Date();
	            				String dataStr = sdf.format(data);
	            			
	            				try {
	    	    	  				// update de l'estat a 1	
	            					PreparedStatement sakaiStatement2 = null;
	            					sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);        		            					
	            					sakaiStatement2.setString(1,dataStr);
	            					sakaiStatement2.setString(2,term);
	            					sakaiStatement2.setString(3,eidCourseOffering);   
	            					sakaiStatement2.executeUpdate();    	    	  					            				
	            						    	    	  				
	    	    	  				// després de cada actualització fem commit
	    	    	  				sakaiConnection.commit();
	    	    	  				sakaiStatement2.close();
	    	    	  				
	    	    	  			}catch (SQLException e) {
	    	    	            	log.error("EXCEPCIO SQL al tancar statement2 - "+idSite+")") ;
	    	    	                log.error("SQLException: " +e);
	    	    	            }	
	            			}	
	            		}		
	    	  	}
					
        }
        catch (SQLException e) {
        	log.error("EXCEPCIO SQL(espaisTitulacioSincro) ");
        	log.error("EXCEPCIO SQL "+e);
        }
        catch (Exception ex) {
            log.error("EXCEPCIO (espaisTitulacioSincro) ");
            log.error(ex.getClass().getName() + " :: " + ex.getMessage());
            return;
        }
        finally {
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
	
	
	public String getPlantillaId() {
		return plantillaId;
	}

	public void setPlantillaId(String plantillaId) {
		this.plantillaId = plantillaId;
	}
	
	private String createSite(String idEspai, String nomEspai, String term) throws JobExecutionException {
		
		log.debug("+++ createEspaiComunicació +++");
		log.debug("Espai "+idEspai);
		log.debug("Nom "+nomEspai);

		String siteTitle;
        
        siteTitle = nomEspai;		
		
		// ara el sufix del període acadèmic ja va incorporat al nom
		//siteTitle = strTmp + " (" + getSiteId() + getSiteIdCourseSuffix() + ")";
		//log.debug("Alta espai: "+getSiteId());
		//log.debug("Aquest espai tindrà el nom: "+siteTitle);
		// strSiteId = "" + siteId + getSiteIdCourseSuffix();
		//log.debug("Espai: "+getSiteId()+ ". Usem la plantilla: "+getPlantillaId());
		//log.info("++Espai "+getSiteId()+ " usem la plantilla: "+getPlantillaId());
		
		try {
				log.debug("Habilitem el security advisor");
				enableSecurityAdvisor();
				log.debug("Cridem el servei per crear un espai tipus curs");
				
				// log.debug("Current User "+instanciaUserDirectoryService.getCurrentUser().getId());
				// creem l'espai de tipus curs
				Site site = instanciaSiteService.addSite(idEspai, instanciaSiteService.getSite(getPlantillaId()));
				// s'ha d'usar aquest mètode del servei per a que faci la còpia de la plantilla
				log.debug("Esborrem el realm que ha creat el servei");
				// cal esborrar el realm corresponent al site perquè no el crea correctament
				AuthzGroup realm_a_esborrar = instanciaAuthzGroupService.getAuthzGroup(site.getReference());
				instanciaAuthzGroupService.removeAuthzGroup(realm_a_esborrar);
				log.debug("Creem el realm a partir de la plantilla");
				// a partir del realm de la plantilla creem directament el realm corresponent al nou site
				AuthzGroup realm = instanciaAuthzGroupService.getAuthzGroup(instanciaSiteService.getSite(getPlantillaId()).getReference());
				AuthzGroup re = instanciaAuthzGroupService.addAuthzGroup(site.getReference(), realm, "admin");
				re.removeMembers();
				// afegim l'usuari admin a l'espai creat amb el rol administrador (Instructor en aquest cas)
				re.addMember("admin", re.getMaintainRole(), true, false);
				instanciaAuthzGroupService.save(re);
				site.setTitle(siteTitle);
				site.setDescription("");
				site.setShortDescription("");				
				site.setPublished(true);
				
				ResourcePropertiesEdit resourcePropertiesEdit = site.getPropertiesEdit();
				resourcePropertiesEdit.addProperty("term", term);
				resourcePropertiesEdit.addProperty("tipus_espai", "espai_comunicacio");
				resourcePropertiesEdit.addProperty("term_eid", term);
				
				log.debug("Cridem el servei per desar els canvis");
				instanciaSiteService.save(site);
			} catch (Exception e) {  
				log.error("EXCEPCIO (createEspaiComunicació)");
				log.error(e.getClass().getName() + " :: " + e.getMessage());
				return "error";
			} finally {
				log.debug("Deshabilitem el security advisor");
				disableSecurityAdvisor();
			}

		return idEspai;
	} 
	
	private String addSiteProviders(String siteId, List<String> providersList) {
		
		log.debug("+++ addSiteProviders +++");
		log.debug("Espai "+siteId);
		log.debug("Proveïdors "+providersList);
		
		try {
				log.debug("Habilitem el security advisor");
				enableSecurityAdvisor();
				Site site = instanciaSiteService.getSite(siteId);
				String realm = site.getReference();
				log.debug("Editem el realm del site");
				AuthzGroup realmEdit = instanciaAuthzGroupService.getAuthzGroup(realm);
				String[] providers = new String[providersList.size()];
				providers = (String[]) providersList.toArray(providers);
				//log.debug("Cridem packId del groupProvider");
				String providerId = instanciaGroupProvider.packId(providers);
				realmEdit.setProviderGroupId(providerId);
				instanciaAuthzGroupService.save(realmEdit);
				instanciaSiteService.save(site);
				return "correcte";
				
		} catch (Exception e) {  
			log.error("EXCEPCIO (addSiteProviders)");
			log.error(e.getClass().getName() + " :: " + e.getMessage());
			return "error";
		}	finally {
			log.debug("Deshabilitem el security advisor");
			disableSecurityAdvisor();
		}
	}
	
	/*private void addSiteManager(String siteId, String contactName, String contactEmail) throws JobExecutionException  {
		log.debug("addSiteManager");

		try {
			Site site = siteService.getSite(siteId);

			ResourcePropertiesEdit resourcePropertiesEdit = site.getPropertiesEdit();
			resourcePropertiesEdit.addProperty("contact-name", contactName);
			resourcePropertiesEdit.addProperty("contact-email", contactEmail);
			siteService.save(site);

		}catch (Exception e) {  
			log.error("EXCEPCIO (addSiteManager)");
			log.debug(e.getClass().getName() + " :: " + e.getMessage());
		}
		
		return;
	} */
	
	private void removeUserFromSite(String siteId, String userLogin) throws JobExecutionException {
		log.debug("+++ removeUserFromSite +++ ");

        try {
        		log.debug("Habilitem el security advisor");
        		enableSecurityAdvisor();
        		try {
        			String userId = instanciaUserDirectoryService.getUserId(userLogin);
        		}catch(UserNotDefinedException Unex){
        			log.error("EXCEPCIO (removeUserFromSite) --> Usuari no definit");
        			log.error(Unex.getClass().getName() + " :: " + Unex.getMessage());
				return;
        		}
        		String userId = instanciaUserDirectoryService.getUserByEid(userLogin).getId();
        		Site site = instanciaSiteService.getSite(siteId);
				String realm = site.getReference();
        		AuthzGroup authzGroup = instanciaAuthzGroupService.getAuthzGroup(realm);	
        		
        		if (authzGroup.getMember(userId) != null) {
        			authzGroup.removeMember(userId);
        			instanciaAuthzGroupService.save(authzGroup);
        		}
        		else
        			return;
        }
       	catch (Exception e) {
			log.error("EXCEPCIO (removeUserFromSite)");
			log.error(e.getClass().getName() + " :: " + e.getMessage());
			
			return;
       	} finally {
			log.debug("Deshabilitem el security advisor");
			disableSecurityAdvisor();
		}

        return;
	} 
	
}
