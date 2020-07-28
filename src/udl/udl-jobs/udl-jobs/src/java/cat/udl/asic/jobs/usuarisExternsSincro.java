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


import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.Random;
/* this is a test Quartz job to show that we can inject jobs into the jobscheduler from an external location */


public class usuarisExternsSincro implements Job {

	static Logger log = Logger.getLogger(
            usuarisExternsSincro.class.getName());

	// consulta d'usuaris amb estat 0 (pendents d'alta)
	static String  sqlSelectUsersAlta = "SELECT LOGIN, NOM, COGNOM FROM UDL_CM_EXTERNS WHERE ESTAT = 0 ";
	
	// update de l'estat a 1 (usuari tractat) 	
	static String  sqlUpdateEstat = "UPDATE UDL_CM_EXTERNS SET ESTAT = 1 , DATA_CREACIO_SAKAI = ?  WHERE LOGIN = ? ";

	public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm";

	private SecurityService instanciaSecurityService;
	private UserDirectoryService instanciaUserDirectoryService;
	private SqlService instanciaSqlService;
	private EmailService instanciaEmailService;
	
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
		
        log.debug("Executem init() de usuarisExternsSincro");
        
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

	public void setEmailService(EmailService instanciaEmailService) {
	    this.instanciaEmailService = instanciaEmailService;
	}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		log.info("Executant la tasca de sincronització dels usuaris externs");
		Connection sakaiConnection = null;
		PreparedStatement sakaiStatement = null;

		String loginUsuari= "";
		String nom = "";
		String cognom = "";
		String password = null;
		String strUpdate = "";
		
        try {
        		// recuperem la informació dels usuaris externs que cal afegir al campus virtual
        		
        		sakaiConnection = instanciaSqlService.borrowConnection(); 
        		sakaiConnection.setAutoCommit(false);
        	
        		sakaiStatement = sakaiConnection.prepareStatement(sqlSelectUsersAlta);        		
        		
        		log.debug("Executem la consulta per recuperar els usuaris externs planificats");        		
        		ResultSet rst = sakaiStatement.executeQuery();
        		while (rst.next()) {
        				PreparedStatement sakaiStatement2 = null;
	    	  			loginUsuari = rst.getString("LOGIN");
	    	  			nom = rst.getString("NOM");
	    	  			cognom = rst.getString("COGNOM");
	  					
	    	  			//Agafem la data actual i la convertim a string	            				
        				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        				Date data = new Date();
        				String dataStr = sdf.format(data);
        				
	    	  			// comprovem si l'usuari ja està donat d'alta
	    	  			boolean creat = getUser(loginUsuari);
	    	  			// si ja està creat, ho registrem al log
	    	  			if (creat){
	    	  				log.info("Usuari "+loginUsuari+" ja pertany al campus virtual");
    	  					try {
    	    	  				// update de l'estat a 1 (usuari tractat) 	
    	  						sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);   
    	  						sakaiStatement2.setString(1, dataStr);
    	  						sakaiStatement2.setString(2, loginUsuari);	    	  					    	  						
    	  						sakaiStatement2.executeUpdate();
    	    	  				// després de cada actualització fem commit
    	    	  				sakaiConnection.commit();
    	    	  				sakaiStatement2.close();
    	    	  				
    	    	  			}catch (SQLException e) {
    	    	            	log.error("EXCEPCIO SQL execució update en el cas de usuari ja creat");
    	    	                log.error("SQLException: " +e);
    	    	            }	

	    	  			}
	    	  			else {
	    	  				// creem la clau i cridem el servei per donar d'alta l'usuari
	    	  				password = createPasswd();  				
	    	  				addUser(loginUsuari,nom,cognom,password);
	    	  				boolean exit = getUser(loginUsuari);
	    	  				if (exit){
	    	  					log.info("PROCÉS ALTA OK: Usuari "+loginUsuari+" afegit al campus virtual");
	    	  					// li enviem la clau 
	    	  					enviaPassword(password, loginUsuari);	    	  					

	    	  					try {
	    	    	  				// update de l'estat a 1 (usuari tractat) 	
	    	  						sakaiStatement2 = sakaiConnection.prepareStatement(sqlUpdateEstat);   
	    	  						sakaiStatement2.setString(1, dataStr);
	    	  						sakaiStatement2.setString(2, loginUsuari);	    	  					    	  						
	    	  						sakaiStatement2.executeUpdate();
	    	    	  				// després de cada actualització fem commit
	    	    	  				sakaiConnection.commit();
	    	    	  				sakaiStatement2.close();

	    	    	  			}catch (SQLException e) {
	    	    	            	log.error("EXCEPCIO SQL execució update en el cas de usuari nou ");
	    	    	                log.error("SQLException: " +e);
	    	    	            }	
	    	  				}
	    	  				else {
	    	  					// registrem el problema al crear l'usuari
	    	  					log.info("Problema amb usuari "+loginUsuari+" PROCÉS ALTA ha fallat  ");
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
		
		String email = userLogin;
		log.debug("++ addUser ++");
		try {
				log.debug("Habilitem el security advisor");
				enableSecurityAdvisor();
				instanciaUserDirectoryService.addUser(null, userLogin, nom, cognom, email, password, "profExtern", null);
		}catch (Exception e){
				log.error("EXCEPCIO (addUser)");
				log.error(e.getClass().getName() + " :: " + e.getMessage());
		}finally {
			log.debug("Deshabilitem el security advisor");
			disableSecurityAdvisor();	
		}
	}
	
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
	
	private void enviaPassword(String password, String to) throws JobExecutionException {

		try{
				String from= "no-reply@cv.udl.cat";	//qui envia el mail 
				String message_subject = "Avís: Dades per a l'accés al Campus Virtual UdL";
	        	String content = "Benvolgut/da: \n\n" +
	        	"Les seves dades per accedir al Campus Virtual de la UdL són:\n"+
	        	"Usuari : '" + to + "' \n"+
	        	"Password: '"+password+"'.\n\n"+
	        	"Un cop hagi accedit al Campus Virtual pot canviar la seva contrasenya a través de l'eina Compte. \n\n"+
	        	"Aquest missatge ha estat enviat automàticament.";
	
				log.info("-*-*-*- Vaig a enviar el mail de "+from+" a "+to);
	        	instanciaEmailService.send(from, to, message_subject, content, null, null, null);
	        	log.info("-*-*-*- Ja està enviat!");
	        }
		catch(Exception e){
			log.error("ERROR (usuarisExternsSincro) (enviaPassword) "+to);
			log.error("Excepció "+e);
		}
	}
	
	
}
