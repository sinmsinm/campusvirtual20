package cat.udl.asic.jobs;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
// import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.quartz.JobExecutionException;

// serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.email.api.EmailService;


import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.user.api.UserNotDefinedException;


// codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ArrayList;

import java.util.Random;
/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
//import org.sakaiproject.tool.cover.SessionManager;

import org.sakaiproject.emailtemplateservice.model.EmailTemplate;
import org.sakaiproject.emailtemplateservice.model.RenderedTemplate;
import org.sakaiproject.emailtemplateservice.service.EmailTemplateService;

public class notificaGrupsNous implements Job {

	static Logger log = Logger.getLogger(
			notificaGrupsNous.class.getName());

	// consulta els grups que s'han planificat nous i que s'han afegit al campus virtual (estat = 1)
	static String  sqlSelectGrupsNous = "select CODI_ASS, NOM_ASS, CODI_GRUP, NOM_GRUP, EMAIL, LOGIN" +
			" from udl_cm_estats_grups_nous where estat = 1";
	
	// update de l'estat a 1 (usuari tractat) 	
	static String  sqlUpdateEstat = "UPDATE UDL_CM_ESTATS_GRUPS_NOUS SET ESTAT = ?, MAIL_SENT=1, DATA_MAIL_NOTIF = ? WHERE CODI_ASS  = ? AND CODI_GRUP = ?";
	
	private static String NOTIFY_NEW_GROUP ="notificacio.nouGrup";
	private static String FILE_NOTIFY_NEW_GROUP_TEMPLATE = "cat/udl/asic/jobs/templates/notifyNewGroup.xml";
	
	private static final String ADMIN = "admin";
	
	public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm";

	private SecurityService instanciaSecurityService;
	private UserDirectoryService instanciaUserDirectoryService;
	private SqlService instanciaSqlService;
	private EmailService instanciaEmailService;
	private SessionManager instanciaSessionManager;
    private EmailTemplateService emailTemplateService;

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
		
        log.debug("Executem init() de notificaGrupsNous");
        
		// Fem que la classe actual no hereti les propietats de rootLogger
		log.setAdditivity(false);
		
		//do we need to load data?
		Map<String, String> replacementValues = new HashMap<String, String>();
		
		// put placeholders for replacement values 
		replacementValues.put("codi_ass", "");
        replacementValues.put("nom_ass", "");
        replacementValues.put("codi_grup", "");
        replacementValues.put("nom_grup", "");
        
        
    	loadTemplate(FILE_NOTIFY_NEW_GROUP_TEMPLATE, NOTIFY_NEW_GROUP);    	

    	
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

	public void setEmailService(EmailService instanciaEmailService) {
	    this.instanciaEmailService = instanciaEmailService;
	}
	
	public void setSessionManager(SessionManager instanciaSessionManager) {
		this.instanciaSessionManager = instanciaSessionManager;
	}

    public void setEmailTemplateService(EmailTemplateService emailTemplateService) {
    	this.emailTemplateService = emailTemplateService;
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca de notificar grups nous planificats");		
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;

		String codi_ass= "";
		String codi_grup = "";
		String email = "";
		String nom_ass = "";
		String nom_grup = "";		
		String strUpdate = "";
		String login = "";
		
        try {
        		// recuperem la informació dels grups nous que s'han planificat i els seus responsables
        		
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		sakaiConnection.setAutoCommit(false);
        	    
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectGrupsNous);        		
        		
        		ResultSet rst = sakaiStatement.executeQuery();
        		while (rst.next()) {
        				PreparedStatement sakaiStatement2 = null;
        				boolean mailEnviat = false;
	    	  			
        				codi_ass = rst.getString("CODI_ASS");
        				nom_ass = rst.getString("NOM_ASS");
        				codi_grup = rst.getString("CODI_GRUP");
        				nom_grup = rst.getString("NOM_GRUP");
	    	  			email = rst.getString("EMAIL");
	    	  			login = rst.getString("LOGIN");
	  					
	    	  			//Agafem la data actual i la convertim a string	            				
        				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        				Date data = new Date();
        				String dataStr = sdf.format(data);
        				if (login != null) {
        					if (!login.isEmpty()) {
        						if (email != null) {
        							if (!email.isEmpty()) {
        								mailEnviat = enviaNotificacio(codi_ass, codi_grup, nom_ass, nom_grup, email, login);
        							}
        							else {
        								log.warn("Correu no enviat, ass. "+codi_ass+" amb resp. "+login+" sense correu (cadena buida)");
        							}
        						}
        						else {
        							log.warn("Correu no enviat, ass. "+codi_ass+" amb resp. "+login+" sense correu (cadena null)");
        						}
        					}
        					else {
        						log.warn("Correu no enviat, ass. "+codi_ass+" sense professor responsable (cadena buida)");
        					}
        				}
        				else {
        					log.warn("Correu no enviat, ass. "+codi_ass+" sense professor responsable (cadena null)");
        				}
        				try {
        						sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);   						
        						// update a estat 2 si s'ha enviat la notificació 
        						if (mailEnviat) {	 
        									sakaiStatement2.setInt(1,2);
        									sakaiStatement2.setString(2, dataStr);
        									sakaiStatement2.setString(3, codi_ass);
        									sakaiStatement2.setString(4, codi_grup);				
        							}
        						else {
        								// update a estat 3 si no hi ha responsable o no s'ha enviat el correu
        								sakaiStatement2.setInt(1,3);
        								sakaiStatement2.setString(2, dataStr);
        								sakaiStatement2.setString(3, codi_ass);
        								sakaiStatement2.setString(4, codi_grup);				
        						}
        					  sakaiStatement2.executeUpdate();		    	  				
							  // després de cada actualització fem commit
							  sakaiConnection.commit();
							  sakaiStatement2.close();
        				}catch (SQLException e) {
   	    	            	log.error("EXCEPCIO SQL executar update notificacio grup nou ");
   	    	                log.error("SQLException: " +e);
   	    	            }	
   	  			}
	            				    	  		
        }
        catch (SQLException e) {
        	log.error("EXCEPCIO SQL(notificacioGrupsNous) ");
        	log.error("EXCEPCIO SQL "+e);
        }
        catch (Exception ex) {
            log.error("EXCEPCIO (notificacioGrupsNous) ");
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
	
	private void loadTemplate(String templateFileName, String templateRegistrationString) 
	{
		log.info(this + " loading template " + templateFileName);
		//we need a user session to avoid potential NPE's
		Session sakaiSession = instanciaSessionManager.getCurrentSession();
		try {
			sakaiSession.setUserId(ADMIN);
		    sakaiSession.setUserEid(ADMIN);
			InputStream in = notificaGrupsNous.class.getClassLoader().getResourceAsStream(templateFileName);
			Document document = new SAXBuilder(  ).build(in);
			List<Element> it = document.getRootElement().getChildren("emailTemplate");
			
			for (int i =0; i < it.size(); i++) {
				Element xmlTemplate = (Element)it.get(i);				
				xmlToTemplate(xmlTemplate, templateRegistrationString);				
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally
		{
			sakaiSession.setUserId(null);
		    sakaiSession.setUserEid(null);
		}
	}

	private void xmlToTemplate(Element xmlTemplate, String key) {
		String subject = xmlTemplate.getChildText("subject");
		String body = xmlTemplate.getChildText("message");
		String locale = xmlTemplate.getChildText("locale");
		String versionString = xmlTemplate.getChildText("version");
		Locale loc = null;
		
		log.info("subject = " +subject);
		log.info("body = " +body);
		log.info("locale = " +locale);
		log.info("versionString = " +versionString);
		
		if (locale != null && !"".equals(locale)) {
			loc = LocaleUtils.toLocale(locale);
		}
		
		
		if (!emailTemplateService.templateExists(key, loc))
		{
			EmailTemplate template = new EmailTemplate();
			template.setSubject(subject);
			template.setMessage(body);
			template.setLocale(locale);
			template.setKey(key);
			template.setVersion(Integer.valueOf(1));//setVersion(versionString != null ? Integer.valueOf(versionString) : Integer.valueOf(0));	// set version
			template.setOwner("admin");
			template.setLastModified(new Date());
			this.emailTemplateService.saveTemplate(template);
			log.info(this + " user notification template " + key + " added");
		}
		else
		{
			EmailTemplate existingTemplate = this.emailTemplateService.getEmailTemplate(key, new Locale(locale));
			String oVersionString = existingTemplate.getVersion() != null ? existingTemplate.getVersion().toString():null;
			if ((oVersionString == null && versionString != null) || (oVersionString != null && versionString != null && !oVersionString.equals(versionString)))
			{
				existingTemplate.setSubject(subject);
				existingTemplate.setMessage(body);
				existingTemplate.setLocale(locale);
				existingTemplate.setKey(key);
				existingTemplate.setVersion(versionString != null ? Integer.valueOf(versionString) : Integer.valueOf(0));	// set version
				existingTemplate.setOwner("admin");
				existingTemplate.setLastModified(new Date());
				this.emailTemplateService.updateTemplate(existingTemplate);
			log.info(this + " user notification template " + key + " updated to newer version");
			}
		}
			
	}
		
	private boolean enviaNotificacio(String codi_ass, String codi_grup, String nom_ass, String nom_grup, String to, String login)
					throws JobExecutionException {
		log.debug("Dins envia notificacio " +codi_ass+" "+nom_ass+" "+codi_grup+" "+nom_grup+" "+to+" " );
		
		try{
			
			User currentUser = instanciaUserDirectoryService.getUserByEid(login);
			
			Map<String, String> replacementValues = new HashMap<String, String>();
			
			// put placeholders for replacement values 
			replacementValues.put("codi_ass", codi_ass);
	        replacementValues.put("nom_ass", nom_ass);
	        replacementValues.put("codi_grup", codi_grup);
	        replacementValues.put("nom_grup", nom_grup);

			return emailTemplateServiceSend(NOTIFY_NEW_GROUP, null, currentUser, "no-reply@cv.udl.cat", to, null, null, replacementValues) != null? true:false;		
			
		}
		catch (Exception e)
			{
				log.warn("Correu no enviat, no existeix usuari " + login);
				return false;
		}			
		
	}
	
	
	private String emailTemplateServiceSend(String templateName, Locale locale, User user, String from, String to, String headerTo, String replyTo, Map<String, String> replacementValues) {
		log.debug("getting template: " + templateName);
		RenderedTemplate template = null;
		try { 
			if (locale == null)
			{
				// use user's locale
				template = emailTemplateService.getRenderedTemplateForUser(templateName, user!=null?user.getReference():"", replacementValues);
			}
			else
			{
				// use local
				template = emailTemplateService.getRenderedTemplate(templateName, locale, replacementValues);
			}
			if (template != null)
			{
				List<String> headers = new ArrayList<String>();
				headers.add("Precedence: bulk");
				
				String content = template.getRenderedMessage();	
				instanciaEmailService.send(from, to, template.getRenderedSubject(), content, headerTo, replyTo, headers);
				return content;
			}
       }
       catch (Exception e) {
    	   log.warn(this + e.getMessage());
    	   return null;
       }
       return null;
	}

	
}
