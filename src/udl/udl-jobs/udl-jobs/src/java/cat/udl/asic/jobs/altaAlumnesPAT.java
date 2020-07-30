package cat.udl.asic.jobs;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
// import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.quartz.JobExecutionException;


//serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
// import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.db.api.SqlService;

import org.sakaiproject.coursemanagement.api.CourseManagementService;
// import org.sakaiproject.authz.api.GroupProvider;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.user.api.UserNotDefinedException;
// import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.Member;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

import org.sakaiproject.exception.IdUnusedException;

// codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.Random;
/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */


public class altaAlumnesPAT implements Job {

	static Logger log = Logger.getLogger(
			altaAlumnesPAT.class.getName());

	// consulta d'alumnes amb estat 0 (pendents d'alta)
	static String  sqlSelectUsersAlta = "SELECT LOGIN, CODI_PLA, TORN FROM UDL_ALTES_PAT WHERE ESTAT = 0";
	
	// actualització de la taula (si estat = 1 vol dir 'alta ok', estat = 2 indica error, cal revisar) 	
	static String  sqlUpdateEstat = "UPDATE UDL_ALTES_PAT SET ESTAT = ? WHERE LOGIN = ? AND CODI_PLA = ?";

	//public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm";

	private SecurityService instanciaSecurityService;
	private UserDirectoryService instanciaUserDirectoryService;
	private SiteService instanciaSiteService;
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
		
        log.debug("Executem init() de altaAlumnesPAT");
        
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
	
	public void setSiteService(SiteService instanciaSiteService) {
	    this.instanciaSiteService = instanciaSiteService;
	}

	public void setCourseManagementService(CourseManagementService instanciaCourseManagementService) {
	    this.instanciaCourseManagementService = instanciaCourseManagementService;
	}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca: Alta dels alumnes als espais de tutoria");
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;

		String login_alu= "";
		String identificaSite = "";
		String codipla = "";
		String torn = "";
		String roleId = "Estudiant";
		int status = 1; // posarà estat=1 de donat d'alta OK
		int comptador = 0; // alumnes tractats
		
        try {
        		List anysAcademics = instanciaCourseManagementService.getCurrentAcademicSessions();
        		Iterator iterAnyAcad = anysAcademics.iterator();
        		AcademicSession academicSession  = (AcademicSession) iterAnyAcad.next();
        		String any_academic = academicSession.getDescription();
        		if (any_academic.equals("9999"))
        		{
        			academicSession  = (AcademicSession) iterAnyAcad.next();
        			any_academic = academicSession.getDescription();
        		}
  
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		sakaiConnection.setAutoCommit(false);
        	
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectUsersAlta);        		
        		
        		log.debug("Executem la consulta per recuperar els alumnes de tutoria que cal fer alta");        		
        		ResultSet rst = sakaiStatement.executeQuery();
        		while (rst.next()) {
        				
        				PreparedStatement sakaiStatement2 = null;
	    	  			// dades alumne
        				login_alu = rst.getString("LOGIN");
	    	  			codipla = rst.getString("CODI_PLA");
	    	  			torn = rst.getString("TORN");
	  					comptador++;
	    	  			
	    	  			// recuperem l'usuari 
	    	  			String userId = getUser(login_alu);
	    	  			// identificador de l'espai on donar-lo d'alta
	    	  			identificaSite = "pat"+codipla+"-"+any_academic;
	    	  			
	    	  			int addMember = addMemberToSiteWithRole(identificaSite, userId, roleId);
	    	  			
	    	  			if (addMember == 0) {
	    	  				// l'alumne s'ha afegit correctament a l'espai de tutoria
	    	  				log.debug("Alumne "+login_alu+" afegit a espai de tutoria "+codipla);
	    	  				// afegim l'alumne a un grup de tutoria
	    	  				String nomGrup = escollirGrup(identificaSite, torn);
	    	  				if (nomGrup == null)
        					{
        						log.info("PROBLEMA al buscar un grup per a alumne "+login_alu+" del pla "+codipla); 
        						status = 2; // posarà estat=2 a la taula indicant problema a l'alta
        					}
	    	  				else
                    		{
                    			int iaddMemberToGroup1 = addMemberToGroup(identificaSite, nomGrup, userId, roleId);
                    			if (iaddMemberToGroup1 == 0) 
                        			{
                        				log.info("OK Alumne "+login_alu+" del pla "+codipla+" afegit al grup "+nomGrup);
                        				status = 1;
                        			}
                    			// salto l'error iaddMemberToGroup1 == 1 perque no pot ser que pertanyi al grup
                    			else {
                    				// possible error no identificat
                    				log.info("Alumne "+login_alu+" del pla "+codipla+" no afegit al grup.");
                    				log.info("Error: "+iaddMemberToGroup1);
                    				status = 2;
                    			}  
                    		}
	    	  			}	
	                    else {
	                    		// l'alumne no s'ha afegit a l'espai
	                    		if (addMember == 1) {
	                    			//alumne ja pertany a l'espai
	                    			log.info("Alumne "+login_alu+" ja pertany a espai de tutoria "+codipla);
	                    			log.debug("Comprovem si pertany a algun grup");
	                    		
	                    			int pertanyGrup = comprovarGrups(identificaSite, userId);
	                    			if (pertanyGrup == 0) {
	                    				log.debug("Alumne no pertany a cap grup. Afegim alumne a un grup");
	                    				String nomGrup = escollirGrup(identificaSite, torn);
	                    				if (nomGrup == null)
	                    					{
	                    						log.info("PROBLEMA al buscar un grup per a alumne "+login_alu+" del pla "+codipla); 
	                    						status = 2;
	                    					}
	                    				else
                                    		{
                                    			int iaddMemberToGroup1 = addMemberToGroup(identificaSite, nomGrup, userId, roleId);
                                    			if (iaddMemberToGroup1 == 0) 
                                        			{
                                        				log.info ("OK Alumne "+login_alu+" del pla "+codipla+" afegit al grup "+nomGrup);
                                        				status = 1;
                                        			}
                                    			// salto l'error iaddMemberToGroup1 == 1 perque no pot ser que pertanyi al grup
                                    			else {
                                    				// possible error no identificat
                                    				log.info("Alumne "+login_alu+" del pla "+codipla+" no afegit al grup.");
                                    				log.info("Error: "+iaddMemberToGroup1);
                                    				status = 2;
                                    			}  
                                    		}      
	                    				}
	                    			else {
	                    				log.info(login_alu+" ja pertany a un grup.");
	                    				status = 1;
                    					// li posarem estat = 1 perquè està correctament donat d'alta ja
	                    			}
	                    		   }
	                    		else {
	                    				if (addMember == 2)
	                    					{
	                    						log.info("Espai "+identificaSite+" no ha estat creat encara");
	                    						// el posem a estat 0 perquè resta pendent de donar d'alta
	                    						status = 0;
	                    					}
	                    				else {
	                    						// error no identificat
	                    						log.info("Error no especificat alta alumne");
	                    						// el posem a estat 2 per indicar que hi ha hagut un problema
	                    						status = 2;
	                    				}
	                    		}
	                     }
	    	  			
	                    if (status == 1)
	                    {
	                    	log.info("PROCÉS ALTA OK: Usuari "+login_alu+" del pla "+codipla+" afegit a espai de tutoria");
	                    }
	                    else {
	                    	log.info("ERROR PROCÉS ALTA: Usuari "+login_alu+" del pla "+codipla+". Estat "+status);
	                    }
	                    
	                    try {
    	    	  				// update de l'estat a 1 (alta usuari OK) o a 0 si cal repetir intent d'alta o a 2 si hi ha hagut error 	
    	  						sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);   
    	  						sakaiStatement2.setInt(1, status);
    	  						sakaiStatement2.setString(2, login_alu);
    	  						sakaiStatement2.setString(3, codipla);
    	  						sakaiStatement2.executeUpdate();
    	    	  				// després de cada actualització fem commit
    	    	  				sakaiConnection.commit();
    	    	  				sakaiStatement2.close();
    	    	  				
    	    	  			}catch (SQLException e) {
    	    	            	log.error("EXCEPCIO SQL execució actualització estat taula");
    	    	                log.error("SQLException: " +e);
    	    	         }	
        		}       	  			
        }
        catch (SQLException e) {
        	log.error("EXCEPCIO SQL(altaAlumnesPAT) ");
        	log.error("EXCEPCIO SQL "+e);
        }
        catch (Exception ex) {
            log.error("EXCEPCIO (altaAlumnesPAT) ");
            log.error(ex.getClass().getName() + " :: " + ex.getMessage());
        }
        finally {
        	try {
                if(sakaiStatement != null) sakaiStatement.close();
            } catch (SQLException e) {
            	log.error("EXCEPCIO SQL al tancar statement");
                log.error("SQLException: " +e);
            }
           
            if(sakaiConnection != null) instanciaSqlService.returnConnection(sakaiConnection);	
            log.info("Final tasca alta alumnes als espais de tutoria. "+comptador+" registres tractats."); 
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
	
	/**
	 * Recuperem l'identificador d'usuari corresponent al login
	 */
	private String getUser(String userLogin) throws JobExecutionException {
		
		String userId = null;
		log.debug("++ getUser ++");
		
		try {
			log.debug("Habilitem el security advisor");
			enableSecurityAdvisor();
			userId = instanciaUserDirectoryService.getUserId(userLogin);
		}
		catch(UserNotDefinedException Unex){
			log.debug("EXCEPCIO (Usuari no definit)");
			log.debug(Unex.getClass().getName() + " :: " + Unex.getMessage());
			return userId;
		}catch (Exception e) {  
			log.error("EXCEPCIO (getUser)");
			log.error(e.getClass().getName() + " :: " + e.getMessage());
		}finally {
			log.debug("Deshabilitem el security advisor");
			disableSecurityAdvisor();
		}
		return userId;
	}
	
	/**
	 * Afegim l'usuari a l'espai
	 */
	private int addMemberToSiteWithRole(String siteId, String userId, String roleId) {
		
		log.debug("++ addMemberToSiteWithRole ++");
		int estat;
		
		try {
				 log.debug("Habilitem el security advisor");
				 enableSecurityAdvisor();
                 Site site = instanciaSiteService.getSite(siteId);

                 if (site.getMember(userId) == null) {
                         site.addMember(userId,roleId,true,false);
                         instanciaSiteService.save(site);
                         estat = 0; // Alta correcta
                 }
                 else
                         estat = 1; // Member ja existent al site
			}
		catch (IdUnusedException idUnusedException) {
                 return 2; // Site no existent
        	}
		catch (Exception e) {
                 log.error("EXCEPCIO (addMemberToSiteWithRole)");
                 log.debug(e.getClass().getName() + " :: " + e.getMessage());
                 return -1; // Error no tractat
			} finally {
				log.debug("Deshabilitem el security advisor");
				disableSecurityAdvisor();
			}
        log.debug("---addMemberToSiteWithRole. Estat: " +estat);
        return estat;
	}
	
	/**
	 * Escollim un grup de tutoria
	 */
	
	private String escollirGrup(String siteId, String torn) {
		
		log.debug("++ Escollir grup ++");
		String identificadorGrup = "";
		String descTorn = "";
		boolean grupsTorn = false;
		int nombre_minim = 1000000;
		
		if (torn.equals("T") || torn.equals("ST"))
		{
			descTorn = "Tutoria tarda";
		}
		else {
			descTorn = "Tutoria matí";
		}
		
		try {
				log.debug("Habilitem el security advisor");
				enableSecurityAdvisor();
				Site site = instanciaSiteService.getSite(siteId);
				Collection llistaGrups = site.getGroups();
				log.debug("Hi ha "+llistaGrups.size()+" grups");
				Iterator i0 = llistaGrups.iterator();
				// recorrem la llista per eliminar el grup de tutors 
				// i comprovar si hi ha grups del torn adequat
				while (i0.hasNext())
		            {
					   Group grup = (Group) i0.next();
					   if (grup.getDescription() == null || grup.getDescription().equals("")) {
						   log.info("Grup "+grup.getTitle()+" sense descripció. No el considerem");
						   i0.remove();
					   }
					   else if (grup.getDescription().equals(descTorn)) {
						   grupsTorn = true;
					   }
					   else if (grup.getDescription().equals("Grup de tutors de l'espai")) {
						   i0.remove();
					   }
		            }
				if (grupsTorn) {
					Iterator i = llistaGrups.iterator();
					// eliminem de la llista els grups que no són del torn adequat
					while (i.hasNext())
		            {
					   Group grup = (Group) i.next();
					   if (!grup.getDescription().equals(descTorn)) {
						   i.remove();
					   }
		            }
				}
				else {
					log.info("No hi ha grups del torn adequat");
					// farem servir la llista completa de grups de tutoria 
				}
				
				// recorrem la llista per trobar quin és el nombre mínim de membres dels grups
				Iterator i2 = llistaGrups.iterator();
				while (i2.hasNext())
	            {
					Group grup2 = (Group) i2.next();
					if (grup2.getMembers().size() < nombre_minim)
		                  nombre_minim = grup2.getMembers().size();
	            }
				log.debug("Nombre mínim és "+nombre_minim);
				// considero només els grups amb aquest nombre mínim 
				Iterator i3 = llistaGrups.iterator();
				while (i3.hasNext())
	            {
					Group grup3 = (Group) i3.next();
					if (grup3.getMembers().size() > nombre_minim)
						 i3.remove();
	            }
				log.debug("Resten "+llistaGrups.size()+" amb "+nombre_minim+" elements");
				Iterator r = llistaGrups.iterator();
		        Group grupEscollit =  (Group) r.next();
		        log.debug("Escollint un dels grups at random");
		        // ens posem a l'inici de la llista dels que queden
		        Random generador = new Random();
		        int aleatori = generador.nextInt(llistaGrups.size());
		        log.debug("Nombre aleatori: "+aleatori);
		        // en seleccionem un aleatòriament
		        for (int k = 0; k < aleatori ; k++)
		           {          
		                 grupEscollit = (Group) r.next();
		           }          
		     
		        log.debug("El grup escollit és "+grupEscollit.getTitle());
		        return grupEscollit.getTitle();
						
		}catch (Exception e) {
	        log.error("EXCEPCIO (escollirGrup)");
	        log.error(e.getClass().getName() + " :: " + e.getMessage());
	        return null; // Error no tractat
	    }finally {
			log.debug("Deshabilitem el security advisor");
			disableSecurityAdvisor();
		}
		
		
	}
	
	/**
	 * Afegim l'usuari al grup
	 */
	
	
	private int addMemberToGroup(String siteId, String groupId, String userId, String roleId) {
          log.debug("++ addMemberToGroup ++");
          
          int estat = 0;

          try {
        	  		log.debug("Habilitem el security advisor");
        	  		enableSecurityAdvisor();
                    Site site = instanciaSiteService.getSite(siteId);
                    Collection collGroups = site.getGroups();
                    Iterator it = collGroups.iterator();
                    String groupTitle;
                    boolean existsGroup = false;

                    while (it.hasNext()) {
                       Group group = (Group) it.next();
                       groupTitle = group.getTitle();

                       if (groupTitle.equals(groupId)) {
                             Member member = group.getMember(userId);

                             if (member == null) {
                                 group.addMember(userId, roleId, true, false);
                                 estat = 0; // alta correcta
                                 instanciaSiteService.save(site);
                                      }
                              else {
                                  estat = 1; // Membre ja existent al grup
                               }
                               // S'hagi inserit o no el membre al grup, el grup és únic i deixem de buscar
                              existsGroup = true;
                                      break;
                              }
                       }
                    if (!existsGroup)
                       estat = 2; // Grup no trobat
                  }
               catch (Exception e){
                      log.error("EXCEPCIO (addMemberToGroup)");
                      log.error(e.getClass().getName() + " :: " + e.getMessage());
                      return -1; //Error 
              }finally {
      			log.debug("Deshabilitem el security advisor");
    			disableSecurityAdvisor();
    		}  
              log.debug("--- addMemberToGroup. Estat: "+estat);
              return estat;
    }

	/**
	 * Comprovar si usuari pretany a un grup de l'espai
	 */
	
	
	private int comprovarGrups(String siteId, String userId) {

		log.debug("++ comprovarGrups ++");
		log.debug("Comprovem si usuari pertany a algun grup");
		int estat = 0;

	       try {
	    	   log.debug("Habilitem el security advisor");
   	  			enableSecurityAdvisor();
	           Site site = instanciaSiteService.getSite(siteId);
	           Collection collGroups = site.getGroups();
	           Iterator it = collGroups.iterator();
	  
	           while (it.hasNext()) {
	                  Group group = (Group) it.next();
	                  Member member = group.getMember(userId);
	                  if (member == null)
	                    {
	                        estat = 0; // no pertany al grup
	                    }
	                  else
	                   {
	                      log.debug("Usuari pertany al grup "+group.getTitle());
	                      estat = 1;
	                      break;
	                   }
	           }
	       }
	       catch (Exception e){
	              log.error("EXCEPCIO (comprovarGrups)");
	              log.error(e.getClass().getName() + " :: " + e.getMessage());
	              return -1; //Error 
	      }finally {
      			log.debug("Deshabilitem el security advisor");
    			disableSecurityAdvisor();
    		}
	      log.debug("--- comprovarGrups. Estat "+estat);
	      return estat;
	}
	
}
