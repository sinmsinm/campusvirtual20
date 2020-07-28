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
import org.sakaiproject.user.api.UserNotDefinedException;


//classes que necessitem
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.io.InputStream;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import java.net.MalformedURLException;
import java.io.IOException;
import org.sakaiproject.tool.api.Session;
import java.util.Date;
import java.util.Random;


//codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.emailtemplateservice.model.EmailTemplate;
import org.sakaiproject.emailtemplateservice.model.RenderedTemplate;
import org.sakaiproject.emailtemplateservice.service.EmailTemplateService;

/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */


public class altaUsuarisGenerics implements Job {

	static Logger log = Logger.getLogger(
			altaUsuarisGenerics.class.getName());

	// consulta d'usuaris genèrics a crear
	static String  sqlSelectUsersAlta = "SELECT usuari_generic usuari, pwd, login, codi_pla, nom_pla, email_coo FROM udl_cm_estats_generics WHERE estat = 0 OR mail_sent = 0 ";
	
	// consulta dels passwd d'un usuari genèric ja creat
	static String  sqlSelectPasswdUsuari = "SELECT usuari_generic usuari, pwd FROM udl_cm_estats_generics WHERE usuari_generic = ? AND pwd is not null";
	
	// update de l'estat a 1 (usuari tractat) 	
	static String  sqlUpdateEstat = "UPDATE udl_cm_estats_generics SET estat = 1, pwd = ? WHERE usuari_generic  = ? AND login = ?";

	// update de l'estat mail_sent a 1 (usuari i pwd enviat al coordinador)
	static String  sqlUpdateEstatMailSent = "UPDATE udl_cm_estats_generics SET mail_sent = 1 WHERE usuari_generic = ? AND email_coo = ?";

	
	private static String NOTIFY_NEW_USER ="notificacio.nouUsuariGeneric";
	private static String FILE_NOTIFY_NEW_USER_TEMPLATE = "cat/udl/asic/jobs/templates/notifyNewUserGeneric.xml";
	
	private static final String ADMIN = "admin";

	private SecurityService instanciaSecurityService;
	private UserDirectoryService instanciaUserDirectoryService;
	private SiteService instanciaSiteService;
	private SqlService instanciaSqlService;
	private EmailService instanciaEmailService;
	private SessionManager instanciaSessionManager;
    private EmailTemplateService emailTemplateService;

	//private CourseManagementService instanciaCourseManagementService;
	
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
		
        log.debug("Executem init() de altaUsuarisGenerics");
        
		// Fem que la classe actual no hereti les propietats de rootLogger
		log.setAdditivity(false);
		
		//do we need to load data?
		Map<String, String> replacementValues = new HashMap<String, String>();
				
		// put placeholders for replacement values 
		//CANVIAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		replacementValues.put("codi_ass", "");
		replacementValues.put("nom_ass", "");
		replacementValues.put("codi_grup", "");
		replacementValues.put("nom_grup", "");
		        
		loadTemplate(FILE_NOTIFY_NEW_USER_TEMPLATE, NOTIFY_NEW_USER);    	
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
	
	public void setEmailService(EmailService instanciaEmailService) {
	    this.instanciaEmailService = instanciaEmailService;
	}
	public void setSessionManager(SessionManager instanciaSessionManager) {
		this.instanciaSessionManager = instanciaSessionManager;
	}

    public void setEmailTemplateService(EmailTemplateService emailTemplateService) {
    	this.emailTemplateService = emailTemplateService;
    }

	

	//public void setCourseManagementService(CourseManagementService instanciaCourseManagementService) {
	    //this.instanciaCourseManagementService = instanciaCourseManagementService;
	//}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca: Alta dels usuaris generics");		
		
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;

		String usuari= "";		
		String password = null;
		String codi_pla = "";
		String nom_pla = "";
		String email_coo = "";
		String login_coo = "";
		

        try {        		
  
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		sakaiConnection.setAutoCommit(false);
        				
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectUsersAlta);
        				        		       		
        		log.debug("Executem la consulta per recuperar els usuaris generics a crear");   
        		log.debug("SQL " +sqlSelectUsersAlta);
        		ResultSet rst = sakaiStatement.executeQuery();        		

        		while (rst.next()) {
    				
        			PreparedStatement sakaiStatement2 = null;
        			PreparedStatement sakaiStatement3 = null;
        			
    				usuari = rst.getString("usuari");
    	  			codi_pla = rst.getString("codi_pla");
    	  			nom_pla = rst.getString("nom_pla");
    	  			email_coo = rst.getString("email_coo");
    	  			login_coo = rst.getString("login");    	  			
    	  			
    	  			// comprovem si l'usuari ja està donat d'alta
    	  			boolean creat = getUser(usuari);
    	  			// si ja està creat, ho registrem al log
    	  			if (creat){
    	  				log.info("Usuari "+usuari+" ja pertany al campus virtual");
    	  				// hem de cercar quin passwd li correspon
    	  				try {
    	  					PreparedStatement sakaiStatement4 = null;
        	  				sakaiStatement4 = sakaiConnection.prepareStatement(sqlSelectPasswdUsuari); 
        	  				sakaiStatement4.setString(1, usuari);
        	  				ResultSet rst2 = sakaiStatement4.executeQuery();
        	  				// només pot trobar un passwd not null
        	  				rst2.next();
        	  				password = rst2.getString("pwd");
        	  				sakaiStatement4.close();
    	  				}
    	  				catch (SQLException e) {
    	  					log.error("EXCEPCIO SQL execució cerca password");
    	                	log.error("SQLException: " +e);
    	  				}	
    	  				
    	  				
	  					try {// update de l'estat a 1 (usuari tractat) 	
	  						log.debug("Posem a 1 "+usuari+" login "+login_coo+" amb password "+password);
	  						sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);   
	  						sakaiStatement2.setString(1, password);
	  						sakaiStatement2.setString(2, usuari); 
	  						sakaiStatement2.setString(3, login_coo);
	  						sakaiStatement2.executeUpdate();
	    	  				// després de cada actualització fem commit
	    	  				sakaiConnection.commit();
	    	  				sakaiStatement2.close(); 				
	    	  			}catch (SQLException e) {
	    	            	log.error("EXCEPCIO SQL execució update en el cas de usuari ja creat");
	    	                log.error("SQLException: " +e);
	    	            }	
	  					// li enviem la clau al coordinador
	  					if(enviaPassword(password, usuari, email_coo, codi_pla, nom_pla, login_coo)){
	  						log.info("Enviem usuari i pwd al coordinador "+ email_coo );
		    	  			try {//update mail_sent a 1
    	  						sakaiStatement3 = sakaiConnection.prepareStatement(sqlUpdateEstatMailSent);   
    	  						sakaiStatement3.setString(1, usuari);	    	  					    	  						
    	  						sakaiStatement3.setString(2, email_coo);
    	  						sakaiStatement3.executeUpdate();
    	    	  				// després de cada actualització fem commit
    	    	  				sakaiConnection.commit();
    	    	  				sakaiStatement3.close();
	   	    	  			}catch (SQLException e) {
	   	    	            	log.error("EXCEPCIO SQL execució update mail sent al coordinador ");
	   	    	                log.error("SQLException: " +e);
	   	    	            }	
	    	  			}

    	  			}
    	  			else {
    	  				// creem la clau , cridem el servei per donar d'alta l'usuari i enviem mail al coordinador
    	  				password = createPasswd();  	
    	  				addUser(usuari,"Usuari Genèric",codi_pla,password);
    	  				boolean exit = getUser(usuari);
    	  				if (exit){
    	  					log.info("PROCÉS ALTA OK: Usuari "+usuari+" afegit al campus virtual");

    	  					try {// update de l'estat a 1 (usuari tractat) 	
    	  						log.debug("Posem a 1 "+usuari+" login "+login_coo+" amb password "+password);
    	  						sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);   
    	  						sakaiStatement2.setString(1, password);
    	  						sakaiStatement2.setString(2, usuari); 
    	  						sakaiStatement2.setString(3, login_coo);
    	  						sakaiStatement2.executeUpdate();
    	    	  				// després de cada actualització fem commit
    	    	  				sakaiConnection.commit();
    	    	  				sakaiStatement2.close();

    	    	  			}catch (SQLException e) {
    	    	            	log.error("EXCEPCIO SQL execució update en el cas de usuari nou ");
    	    	                log.error("SQLException: " +e);
    	    	            }
    	  					// li enviem la clau al coordinador
    	  					if(enviaPassword(password, usuari, email_coo, codi_pla, nom_pla, login_coo)){
    	  						log.info("Enviem usuari i pwd al coordinador "+ email_coo );
    		    	  			try {//update mail_sent a 1
        	  						sakaiStatement3 = sakaiConnection.prepareStatement(sqlUpdateEstatMailSent);   
        	  						sakaiStatement3.setString(1, usuari);	    	  					    	  						
        	  						sakaiStatement3.setString(2, email_coo);
        	  						sakaiStatement3.executeUpdate();
        	    	  				// després de cada actualització fem commit
        	    	  				sakaiConnection.commit();
        	    	  				sakaiStatement3.close();
    	   	    	  			}catch (SQLException e) {
    	   	    	            	log.error("EXCEPCIO SQL execució update mail sent al coordinador ");
    	   	    	                log.error("SQLException: " +e);
    	   	    	            }	
    	    	  			}
    	  						
    	  				}		
    	  					
    	  				else {
    	  					// registrem el problema al crear l'usuari
    	  					log.info("Problema amb usuari "+usuari+" PROCÉS ALTA ha fallat  ");
    	  				}
    	  			}
            			
    	  	}			
	    }
	    catch (SQLException e) {
	    	log.error("EXCEPCIO SQL(usuarisExternsSincro) ");
	    	log.error("EXCEPCIO SQL "+e);
	    }
	    catch (Exception ex) {
	        log.error("EXCEPCIO (usuarisExternsSincro) ");
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
	
	/*
	 * Create random password
	 */
	private String createPasswd(){
		
		String pass="";
		String con = "sdfghjklqwrtypzxcvbnm";
		String voc = "aeiou";
		
		char consonants [] = con.toCharArray();
		char vocals [] = voc.toCharArray();
		
		
		Random rand = new Random();

		for (int i=0;i<3;i++){
			pass = pass + consonants[rand.nextInt(21)]; 
			pass = pass + vocals[rand.nextInt(5)];
		}
		pass = pass + rand.nextInt(100);
		
		return pass;
	}
	
	
	private boolean getUser(String userLogin) throws JobExecutionException {
		
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
			return false;
		}catch (Exception e) {  
			log.error("EXCEPCIO (getUser)");
			log.error(e.getClass().getName() + " :: " + e.getMessage());
		}finally {
			log.debug("Deshabilitem el security advisor");
			disableSecurityAdvisor();
		}
		return true;
	}
	
	
	private void addUser(String userLogin, String nom, String cognom, String password) throws JobExecutionException {
		
		String email = "";
		log.debug("++ addUser ++");
		try {
				log.debug("Habilitem el security advisor");
				enableSecurityAdvisor();
				instanciaUserDirectoryService.addUser(null, userLogin, nom, cognom, email, password, "convidat", null);            	
		}catch (Exception e){
				log.error("EXCEPCIO (addUser)");
				log.error(e.getClass().getName() + " :: " + e.getMessage());
		}finally {
			log.debug("Deshabilitem el security advisor");
			disableSecurityAdvisor();	
		}
	}
	

	private boolean enviaPassword(String password, String usuari_generic, String to, String codi_pla, String nom_pla, String login)
			throws JobExecutionException {
		
		log.debug("Dins envia notificacio  " +codi_pla+" "+nom_pla+" "+usuari_generic+" "+password+" "+to+" " );
		
		try{
			
			User currentUser = instanciaUserDirectoryService.getUserByEid(login);
			
			Map<String, String> replacementValues = new HashMap<String, String>();
			
			// put placeholders for replacement values 
			replacementValues.put("codi_pla", codi_pla);
		    replacementValues.put("nom_pla", nom_pla);
		    replacementValues.put("usuari_generic", usuari_generic);
		    replacementValues.put("password", password);
		
			return emailTemplateServiceSend(NOTIFY_NEW_USER, null, currentUser, "no-reply@cv.udl.cat", to, null, null, replacementValues) != null? true:false;		
			
		}
		catch (Exception e)
			{
				log.warn(this + " cannot find user " + login);
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
