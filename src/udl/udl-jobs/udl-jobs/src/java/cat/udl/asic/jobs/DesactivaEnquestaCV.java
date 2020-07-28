package cat.udl.asic.jobs;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
	
// serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;

// codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DesactivaEnquestaCV implements Job {

	//Inicialitzem el log
	static Logger M_log = Logger.getLogger(DesactivaEnquestaCV.class);

	//Serveis requerits per executar el JOB
	private SecurityService securityService;
	private UserDirectoryService userDirectoryService;
	private SqlService sqlService;
	private SecurityAdvisor securityAdvisor;
	private PreferencesService preferencesService;
	private AuthzGroupService authzGroupService;
	private SessionManager sessionManager;
	
	
	//GETTERS i SETTERS dels serveis de Sakai
	public void setSecurityService(SecurityService securityService) {
	    this.securityService = securityService;
	}
	
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
	    this.userDirectoryService = userDirectoryService;
	}

	//Carreguem el sqlService. Cuidado pq aquests esta configurat diferent al components i configurat al sakai.properties
	public void setSqlService(SqlService sqlService) {
	    this.sqlService = sqlService;
	}
	
	public void setPreferencesService(PreferencesService preferencesService) {
	    this.preferencesService = preferencesService;
	}
	
	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}
	
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	

	//Habilita i deshabilita el supervisor de seguretat per permetre desar dades del model
	private void enableSecurityAdvisor() {
		securityService.pushAdvisor(securityAdvisor);
	}

	private void disableSecurityAdvisor() {
		securityService.popAdvisor();
	}

	//SQLs que s'hauran d'executar (bd enquesta primer amb dblink a uxii)
	static String	SQL_TOTS_VISTA = "SELECT LOGIN FROM PENDENTS_DE_FER_ENQUESTA_LOC";
	
	//Inicialització del JOB 
	public void init() {

		//Create our security advisor.
		securityAdvisor = new SecurityAdvisor() {
			public SecurityAdvice isAllowed(String userId, String function,
					String reference) {
				return SecurityAdvice.ALLOWED;
			}
		};
		/* Preparem entorn per que log4j trobi el fitxer de configuració al directori /conf del tomcat */
		PropertyConfigurator.configure(System.getProperty("catalina.home") + "/conf/log4j.properties");
		
		M_log.setAdditivity(false);
        M_log.debug("Inicia JOB Desactiva Enquesta CV");
    	
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
	
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		M_log.info("EnquestaLleng: Execucio JOB - Desactivacio de enquesta a alumnat que la te activada a CV");

		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmtins = null;
		ResultSet rst = null;
		
		//Activem l'opció per permetre canviar dades
		enableSecurityAdvisor();
		//For avoiding Problems with some checks in BasePreferencesServicess
		actAsAdmin();
		
		
		try {

			// Connectem a la BD de l'enquesta de primer

			connection = sqlService.borrowConnection();
			stmt = connection.prepareStatement(SQL_TOTS_VISTA);

			// Obtenim tots els usuaris a tractar
			rst = stmt.executeQuery();

			while (rst.next()) {

				// Agafem l'usuari de Sakai i afegim la propietat a les
				// preferencies

				try {
					String login = rst.getString("LOGIN");
					if (login != null) {
						// Obtenim l'usuari amb el login passat per la vista
						M_log.info ("Prova Nivell Lleng: Processant usuari " + login);
						User user = userDirectoryService.getUserByEid(login);

						
						if (user != null) {
							
							PreferencesEdit preferences = null;
							try {
								// Obtenim les preferencies
								preferences = preferencesService.edit(user.getId());
							} catch (Exception prefException) {
								
								M_log.info ("EnqLleng: "  + login + " Usuari sense preferencies - crea noves");

								try {
									// Si encara no te preferenices en creem unes de noves
									preferences = preferencesService.add(user.getId());
								} catch (IdUsedException prefException2) {
									M_log.error(prefException2);
								} catch (PermissionException prefException2) {
									M_log.error(prefException2);
								}catch (Exception ex){
									M_log.error ("EnqLleng: Error al processar el registre ¿Usuari Bloquejat? ");
									ex.printStackTrace ();
									
								}

							}
							// Ja tenim les preferenies?
							if (preferences != null) {
								// Afegim la propietat
								ResourcePropertiesEdit props = preferences.getPropertiesEdit();
								props.removeProperty("udlFirstYearTutorial");	
								preferencesService.commit(preferences);
								M_log.info ("EnqLleng: propietat udlFirstYearTutorial eliminada a " + login);
							}

						} else {
							M_log.error("EnqLleng: Usuari amb login <<" + login + ">> no existeix per Sakai");
						}
					}

				} catch (Exception ex) {
					M_log.error("EnqLleng: Error processant linia de registre");
					ex.printStackTrace ();
				}

			}
			
			//Tanquem el cursor
			rst.close();
			
		} catch (SQLException e) {
			M_log.error("EnqLleng: EXCEPCIO SQL(Enquesta Llengues) ");
			M_log.error("EnqLleng: EXCEPCIO SQL " + e);
		} catch (Exception ex) {
			M_log.error("EnqLleng: EXCEPCIO general ");
			M_log.error(ex.getClass().getName() + " :: " + ex.getMessage());
		} finally {

			disableSecurityAdvisor();
			
			try {
					//Tanquem el statement principal
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				M_log.error("EnqLleng: EXCEPCIO SQL al tancar statement");
				M_log.error("EnqLleng: SQLException: " + e);
			}

			//Tanquem la connexió
			if (connection != null){
				sqlService.returnConnection(connection);
			}
				
		}
	}
	
}
