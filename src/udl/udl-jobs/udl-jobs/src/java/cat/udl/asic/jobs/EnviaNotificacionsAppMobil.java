package cat.udl.asic.jobs;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
	
// serveis que necessitem
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;

import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.announcement.api.AnnouncementChannel;
import org.sakaiproject.announcement.api.AnnouncementMessage;
import org.sakaiproject.announcement.api.AnnouncementMessageHeader;

import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.Member;

import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;

import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.util.FormattedText;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;

// codi per a les consultes sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringEscapeUtils;

//Codi per les connexions al servei de missatgeria
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.AuthCache;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.HttpHost;
import org.apache.http.HttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.util.EntityUtils;


import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;


import java.io.IOException;

import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeService;

public class EnviaNotificacionsAppMobil implements Job {

	//Inicialitzem el log
	static Logger M_log = Logger.getLogger(EnviaNotificacionsAppMobil.class);

	//Serveis requerits per executar el JOB
	private SecurityService securityService;
	private SqlService sqlService;
	private SecurityAdvisor securityAdvisor;
	private AuthzGroupService instanciaAuthzGroupService;
	protected EntityManager entityManager;
	private AnnouncementService instanciaAnnouncementService;
	private SiteService instanciaSiteService;
	private ServerConfigurationService instanciaServerConfigurationService;
	private TimeService instanciaTimeService;
	private HttpClientContext httpclientContextAppUdL;
	 
	
	
	
	//GETTERS i SETTERS dels serveis de Sakai
	public void setSecurityService(SecurityService securityService) {
	    this.securityService = securityService;
	}

	//Carreguem el sqlService. Cuidado pq aquests esta configurat diferent al components i configurat al sakai.properties
	public void setSqlService(SqlService sqlService) {
	    this.sqlService = sqlService;
	}
	
	public void setAuthzGroupService(AuthzGroupService instanciaAuthzGroupService) {
		this.instanciaAuthzGroupService = instanciaAuthzGroupService;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public void setAnnouncementService(AnnouncementService instanciaAnnouncementService) {
	    this.instanciaAnnouncementService = instanciaAnnouncementService;
	}
	
	public void setSiteService(SiteService instanciaSiteService) {
	    this.instanciaSiteService = instanciaSiteService;
	}
	
	public void setServerConfigurationService(ServerConfigurationService instanciaServerConfigurationService) {
	    this.instanciaServerConfigurationService = instanciaServerConfigurationService;
	}
	
	public void setTimeService(TimeService instanciaTimeService) {
	    this.instanciaTimeService = instanciaTimeService;
	}
	

	//Habilita i deshabilita el supervisor de seguretat per permetre desar dades del model
	private void enableSecurityAdvisor() {
		securityService.pushAdvisor(securityAdvisor);
	}

	private void disableSecurityAdvisor() {
		securityService.popAdvisor();
	}

	//SQLs que s'hauran d'executar 
	static String MESSAGES_TO_PUSH = "SELECT ENTITYREFERENCE FROM ENTITY_TO_PUSH";
	static String MESSAGES_TO_DELETE = "DELETE FROM ENTITY_TO_PUSH WHERE ENTITYREFERENCE = ?";
	
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
        M_log.debug("Inicia JOB Envia Notificacions App Mobil");
    	
	}
	
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		M_log.debug("EnviaNotificacionsAppMobil: Execucio JOB - Enviament de notificacions al servei App Mobil");

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rst = null;
		String entityReference = "";
			
		//Activem l'opció per permetre canviar dades
		enableSecurityAdvisor();
		
		try {

			//Treballarem amb dos servidors de notificacions, per tant mirarem si estan activats per inicialitzar-los
			if ("true".equals (instanciaServerConfigurationService.getString("appMobil.AppUdL.enabled"))) {
				httpclientContextAppUdL = authenticateUdL();
				if (httpclientContextAppUdL == null) {
					M_log.warn("EnviaNotifAppMobil: Authentication to server AppUdL failed");
					//return;
				}
			}
			
			connection = sqlService.borrowConnection();
			M_log.debug ("EnviaNotifAppMobil: Preparem statement "+MESSAGES_TO_PUSH);
			stmt = connection.prepareStatement(MESSAGES_TO_PUSH);

			// Recuperem els identificadors dels missatges que cal enviar
			rst = stmt.executeQuery();

			while (rst.next()) {
				
				PreparedStatement deleteStmt = null;
				boolean published = false;
				try {
					entityReference = rst.getString("ENTITYREFERENCE");
					if (entityReference != null) {
						M_log.debug ("EnviaNotifAppMobil: Processant " + entityReference);
						Reference ref = entityManager.newReference(entityReference);
						String msgId = ref.getId();
						String notificationUrl = "";
						String channelRefTemp = entityReference.replace("/"+msgId,"");
						String channelRef = channelRefTemp.replace("msg","channel");
						String msgContext = ref.getContext();
						AnnouncementChannel channel = instanciaAnnouncementService.getAnnouncementChannel(channelRef);
						AnnouncementMessage msg = channel.getAnnouncementMessage(msgId);
						AnnouncementMessageHeader msgHeader = msg.getAnnouncementHeader();
						boolean isDraft = msgHeader.getDraft();
						// comprovem que no sigui un esborrany
						if (!isDraft) {
							// comprovem la release date per saber si cal enviar aquest missatge al servei de missatgeria
							// primer comprovem si hi ha release date definida
							if (msg.getProperties().get(instanciaAnnouncementService.RELEASE_DATE) != null) {
								Time releaseDate = msg.getProperties().getTimeProperty(instanciaAnnouncementService.RELEASE_DATE);
								M_log.debug ("EnviaNotifAppMobil: releaseDate " +releaseDate.toStringLocal());
								Time now = instanciaTimeService.newTime();
								if (now.after(releaseDate)) {
									M_log.debug ("EnviaNotifAppMobil: Hem superat la releaseDate ");
									published = true;
								}
							}
							else {
								M_log.debug ("EnviaNotifAppMobil: releaseDate not defined");
								published = true;
							}
						}
						if (published) {
							// recuperem el cos del missatge
							String body = msg.getBody();
							// recuperem l'assumpte del missatge, no pot tenir més de 80 caràcters (CVON-132)
							int subjectMaxLength = 80;
							String subject = msgHeader.getSubject();
							if (subject.length() > subjectMaxLength) {
							      subject = subject.substring(0, 70);
							      subject = subject.concat("...");
							   }
							M_log.debug ("EnviaNotifAppMobil: Subject " + subject);
							// informació de l'autor del missatge
							User author = msgHeader.getFrom();
							String authorDisplayId = author.getDisplayId();
							M_log.debug ("EnviaNotifAppMobil: authorDisplayId " + authorDisplayId);
							String messageAuthor = author.getFirstName()+" "+author.getLastName();
							M_log.debug ("EnviaNotifAppMobil: author "+messageAuthor);
							// recuperem a qui s'ha d'enviar el missatge
							Site site = instanciaSiteService.getSite(msgContext);
							M_log.debug ("EnviaNotifAppMobil: context "+msgContext);
							ToolConfiguration toolConfig = site.getToolForCommonId("sakai.announcements");
							String pageId = toolConfig.getPageId();
							//notificationUrl = site.getUrl()+"/page/"+pageId;
							notificationUrl = "https://cv.udl.cat/portal/site/"+ site.getId()+ "/page/"+pageId;
							M_log.debug ("EnviaNotifAppMobil: NotificationUrl " + notificationUrl);
							String access = msgHeader.getAccess().toString();
							M_log.debug ("EnviaNotifAppMobil: access " +access);
							ResourceProperties siteProperties = site.getProperties();
							String categoryId = siteProperties.getProperty("categoryId");
							// comprovem si l'espai té definida una categoryId
							if (categoryId != null){
								// si està definit el categoryId s'envia com a recipientIds
								if (send(subject,messageAuthor,body,msgContext,site.getTitle(),notificationUrl,categoryId,true)) {
				                	M_log.debug("EnviaNotifAppMobil: Missatge enviat correctament a la categoria "+categoryId);
				                	M_log.debug("EnviaNotifAppMobil: Procedim a eliminar el registre "+entityReference);
				                	deleteStmt = connection.prepareStatement(MESSAGES_TO_DELETE);
				                	deleteStmt.setString(1, entityReference);
				                	deleteStmt.executeUpdate();
				                	connection.commit();
				                	deleteStmt.close();
				                	M_log.debug("EnviaNotifAppMobil: Hem eliminat el registre "+entityReference);
				                }
				                else {
				                	M_log.warn("EnviaNotifAppMobil: Server returned error "+entityReference);
				                }
							}
							else {
								// si no està definit el categoryId cal determinar la llista d'usuaris als que s'ha d'enviar el missatge
								if (access.equals("channel")) {
									// s'ha d'enviar a tots els membres del site
									Set members = site.getMembers();
									String siteMembers = "[";
					                for(Iterator memberIter = members.iterator(); memberIter.hasNext();) {
					                    Member member = (Member)memberIter.next();
					                    String userEid = (String) member.getUserEid();
					                	M_log.debug ("EnviaNotifAppMobil: "+userEid+" is site member");
					                	siteMembers = siteMembers + "\""+userEid+"\"";
					                	if (memberIter.hasNext()){
					                		siteMembers = siteMembers + ",";
					                	}else{
					                		siteMembers = siteMembers + "]";
					                	}
					                }
					                if (send(subject,messageAuthor,body,msgContext,site.getTitle(),notificationUrl,siteMembers,false)) {
					                	M_log.debug("EnviaNotifAppMobil: Missatge enviat correctament als membres del site");
					                	M_log.debug("EnviaNotifAppMobil: Procedim a eliminar el registre "+entityReference);
					                	deleteStmt = connection.prepareStatement(MESSAGES_TO_DELETE);
					                	deleteStmt.setString(1, entityReference);
					                	deleteStmt.executeUpdate();
					                	connection.commit();
					                	deleteStmt.close();
					                	M_log.debug("EnviaNotifAppMobil: Hem eliminat el registre "+entityReference);
					                }
					                else {
					                	M_log.warn("EnviaNotifAppMobil: Server returned error "+entityReference);
					                }
								}
								else if (access.equals("grouped")) {
									// només als membres dels grups autoritzats
									Collection <String> groupIds = msgHeader.getGroups();
									String groupMembers = "[";
									boolean listEmpty = true;
									boolean firstGroupNotEmpty = true;
									for(Iterator groupsIter = groupIds.iterator(); groupsIter.hasNext();) {
											String groupId = (String) groupsIter.next();
											M_log.debug ("EnviaNotifAppMobil: GroupId " +groupId);
											AuthzGroup authzGroup = instanciaAuthzGroupService.getAuthzGroup(groupId);
											Set membersGroup = authzGroup.getMembers();
											if (!membersGroup.isEmpty()) {
												listEmpty = false;
												if (!firstGroupNotEmpty) {
													// si no és el primer grup no buit cal afegir la coma
													groupMembers = groupMembers + ",";
												}
								                for(Iterator membersGroupIter = membersGroup.iterator(); membersGroupIter.hasNext();) {
								                	 Member memberGroup = (Member) membersGroupIter.next();
								                	 String userEidGroup = (String) memberGroup.getUserEid();
								                	 M_log.debug ("EnviaNotifAppMobil: "+userEidGroup+" is member of this group");
								                	 groupMembers = groupMembers + "\""+userEidGroup+"\"";
									                	if (membersGroupIter.hasNext()){
									                		groupMembers = groupMembers + ",";
									                	}
								                }
								                if (groupsIter.hasNext()){
								                	firstGroupNotEmpty = false;	
									            } 
											}
									}
									groupMembers = groupMembers + "]";
									if (listEmpty) {
										// si tots els grups són buits la llista queda buida
										M_log.warn("EnviaNotifAppMobil: Lista de membres buida");
										M_log.debug("EnviaNotifAppMobil: Procedim a eliminar el registre "+entityReference);
					                	deleteStmt = connection.prepareStatement(MESSAGES_TO_DELETE);
					                	deleteStmt.setString(1, entityReference);
					                	deleteStmt.executeUpdate();
					                	connection.commit();
					                	deleteStmt.close();
					                	M_log.debug("EnviaNotifAppMobil: Hem eliminat el registre "+entityReference);
										
									}
									else {
										if (send(subject,messageAuthor,body,msgContext,site.getTitle(),notificationUrl,groupMembers,false)) {
						                	M_log.debug("EnviaNotifAppMobil: Missatge enviat correctament als membres dels grups autoritzats");
						                	M_log.debug("EnviaNotifAppMobil: Procedim a eliminar el registre "+entityReference);
						                	deleteStmt = connection.prepareStatement(MESSAGES_TO_DELETE);
						                	deleteStmt.setString(1, entityReference);
						                	deleteStmt.executeUpdate();
						                	connection.commit();
						                	deleteStmt.close();
						                	M_log.debug("EnviaNotifAppMobil: Hem eliminat el registre "+entityReference);
						                }
						                else {
						                	M_log.warn("EnviaNotifAppMobil: Server returned error "+entityReference);
						                }
									}
								}
								else {
									M_log.warn ("EnviaNotifAppMobil: access no és ni channel ni grouped "+entityReference);
								}
							}		
						}
						else {
							M_log.debug ("EnviaNotifAppMobil: És un esborrany o encara no hem arribat a la releaseDate");
						}
					}
				} catch (Exception ex) {
					M_log.error("EnviaNotifAppMobil: Error processing");
					ex.printStackTrace ();
				}

			}
			
			//Tanquem el cursor
			rst.close();
			
		} catch (SQLException e) {
			M_log.error("EnviaNotifAppMobil: EXCEPCIO SQL(EnviaNotifAppMobil) ");
			M_log.error("EnviaNotifAppMobil: EXCEPCIO SQL " + e);
		} catch (Exception ex) {
			M_log.error("EnviaNotifAppMobil: EXCEPCIO general ");
			M_log.error(ex.getClass().getName() + " :: " + ex.getMessage());
		} finally {

			disableSecurityAdvisor();
			
			try {
					//Tanquem el statement principal
				if (stmt != null){
					stmt.close();
				}
			} catch (SQLException e) {
				M_log.error("EnviaNotifAppMobil: EXCEPCIO SQL al tancar statement");
				M_log.error("EnviaNotifAppMobil: SQLException: " + e);
			}

			//Tanquem la connexió
			if (connection != null){
				sqlService.returnConnection(connection);
			}
				
		}
	}

	private HttpClientContext authenticateUdL() throws Exception {
		
		String urlAuthAppMobilServer = instanciaServerConfigurationService.getString("appMobil.AppUdL.urlServerAuth"); 
		String AuthAppMobilHost = instanciaServerConfigurationService.getString("appMobil.AppUdL.hostAuth");
		String AuthAppMobilPort = instanciaServerConfigurationService.getString("appMobil.AppUdL.portAuth");
		String userAppMobilServer = instanciaServerConfigurationService.getString("appMobil.AppUdL.user"); 
		String pwdAppMobilServer = instanciaServerConfigurationService.getString("appMobil.AppUdL.password"); 

		M_log.debug ("EnviaNotifAppMobil: Url a la que autentiquem ");
        
		HttpHost targetHost = new HttpHost(AuthAppMobilHost, Integer.parseInt(AuthAppMobilPort));
		
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthAppMobilHost, Integer.parseInt(AuthAppMobilPort)),
                new UsernamePasswordCredentials(userAppMobilServer, pwdAppMobilServer));
        
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());
         
        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        
		CloseableHttpClient httpclient = HttpClients.createDefault();

       	HttpPost post = new HttpPost (urlAuthAppMobilServer);
		   
         try {
           CloseableHttpResponse response = httpclient.execute(post,context);
           M_log.debug("EnviaNotifAppMobil: Server Auth response is " + response.getStatusLine().getStatusCode());
           response.close ();
	    }catch (Exception ex){
	      	ex.printStackTrace();
	      	context = null;
	    }
        finally {
          httpclient.close();
        }
        
        return context;
	}

	
	private HttpClient authenticateCRUE() throws Exception {
		CloseableHttpClient httpclient = HttpClients.custom()
				.build();
		
           return httpclient;
	}

	private boolean send(String subject, String author, String content, String siteId, String siteTitle, String notificationUrl, String receptientsIds,boolean isCategory) throws Exception {
		boolean sentUdL = false;
		boolean sentCRUE = false;
		
		if ("true".equals (instanciaServerConfigurationService.getString("appMobil.AppUdL.enabled"))) {
			String categoriesIds;
			
			if (isCategory){
				categoriesIds = "[\""+receptientsIds+"\"]";
				sentUdL = sendUdL (subject,author,content,siteId,siteTitle,notificationUrl,categoriesIds);
			}else {
				sentUdL = sendUdL (subject,author,content,siteId,siteTitle,notificationUrl,receptientsIds);	
			}
		}
		
		if ("true".equals (instanciaServerConfigurationService.getString("appMobil.AppCRUE.enabled"))) {
			if (isCategory) {
				sentCRUE = sendCRUECategory(subject,author,content,siteId,siteTitle,notificationUrl,receptientsIds);
			}else {
				sentCRUE = sendCRUE(subject,author,content,siteId,siteTitle,notificationUrl,receptientsIds);	
			}
			
		}

		return sentCRUE || sentUdL;
	}
	
	private boolean sendUdL(String subject, String author, String content, String siteId, String siteTitle, String notificationUrl, String receptientsIds) throws Exception {
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			
			
			boolean retorn = false;
			String subjectEscapat = StringEscapeUtils.escapeJson(subject);
			String authorEscapat = StringEscapeUtils.escapeJson(author);
			String bodyEscapat = StringEscapeUtils.escapeJson(content);
			String urlMessagesAppMobilServer = instanciaServerConfigurationService.getString("appMobil.AppUdL.urlMessages"); 
			
			String JSON_STRING = "{"
						+ "\"subject\":\""+ subjectEscapat+ "\"," 
						+ "\"author\":\""+ authorEscapat+ "\"," 
						+ "\"content\":\""+ bodyEscapat+ "\","
						+ "\"siteId\":\""+ siteId+ "\","
						+ "\"siteTitle\":\""+ siteTitle+ "\","
						+ "\"notiURL\":\""+notificationUrl+ "\","
						+ "\"receptientsIds\":"+ receptientsIds+ "}";
			
			StringEntity requestEntity = new StringEntity(
				    JSON_STRING);
			
			HttpPost postMessage = new HttpPost(urlMessagesAppMobilServer);
			postMessage.setEntity(requestEntity);
			postMessage.setHeader("Accept", "application/json");
		    postMessage.setHeader("Content-type", "application/json");
		    
			try {
				if (httpclientContextAppUdL != null) {
					
					HttpResponse response = httpclient.execute(postMessage,httpclientContextAppUdL); 
					int postResponseCode = response.getStatusLine().getStatusCode();
					
					M_log.debug("EnviaNotifAppMobil: ServerUdL response is "+postResponseCode);
					//M_log.debug("EnviaNotifAppMobil Server response post: "+postMessage.getResponseBodyAsString());	
					 if (postResponseCode != 200) {
						   M_log.debug("EnviaNotifAppMobil: ServerUdL response is not OK");
					   }
					   else {
						   retorn = true;
					   }
				}
				else {
					M_log.debug("EnviaNotifAppMobil: httpclientContextAppUdL is null");
				}
				
	        }catch (Exception ex){
	        	ex.printStackTrace();
	        }
           finally {
        	   httpclient.close();
	        } 
		   return retorn;
	    }
	
	private boolean sendCRUE  (String subject, String author, String content, String siteId, String siteTitle, String notificationUrl, String receptientsIds) throws Exception {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		boolean retorn = false;
		String subjectEscapat = StringEscapeUtils.escapeJson(subject);
		String authorEscapat = StringEscapeUtils.escapeJson(author);
		String bodyEscapat = StringEscapeUtils.escapeJson(convertFormattedHtmlTextToPlaintext(content));
		String urlMessagesAppMobilServer = instanciaServerConfigurationService.getString("appMobil.AppCRUE.urlMessages"); 
		String importCode = instanciaServerConfigurationService.getString("appMobil.AppCRUE.importCode");
		String token = instanciaServerConfigurationService.getString("appMobil.AppCRUE.token");
		
		String JSON_STRING= "{\n"
				+ "\"import_code\" : \""+ importCode +"\",\n"
				+ "\"token\": \""+ token +"\",\n"
				+ "\"recipient_role_name\": \"TODOS\",\n"
				+ "\"recipients\": "
				+ "		{\n "
				+ "		\"app_usernames\":"+ receptientsIds + "\n"
				+ "},\n"
				+ "\"message\": {\n "
				+ "		\"title\": \""+ subjectEscapat + "\",\n "
				+ "		\"body\": \"" + bodyEscapat +"\\n\\n Missatge enviat des de l'espai del CV: " + StringEscapeUtils.escapeJson(siteTitle) + "\"\n"
				+ "}\n"
				+ "}";
		
		M_log.debug ("JSON:" + JSON_STRING);
		
		StringEntity requestEntity = new StringEntity(
			    JSON_STRING);
		
		HttpPost postMessage = new HttpPost(urlMessagesAppMobilServer);
		postMessage.setEntity(requestEntity);
		postMessage.setHeader("Accept", "application/json");
	    postMessage.setHeader("Content-type", "application/json");
		
		
		try {
			if (httpclient != null) {
				//Fem la primera petició, si la llista d'usuaris està bé farà el missatge i tot anirà perfecte
				HttpResponse response = httpclient.execute(postMessage); 
				int postResponseCode = response.getStatusLine().getStatusCode();
				
				M_log.debug("EnviaNotifAppMobil: Server CRUE response is " + postResponseCode + " Reason " + response.getStatusLine().getReasonPhrase());
				//M_log.debug("EnviaNotifAppMobil Server response post: "+postMessage.getResponseBodyAsString());	
				 if (postResponseCode != 200) {
					   M_log.debug("EnviaNotifAppMobil: Server CRUE response is not OK, prove");
					   //Tractarem la llista d'usuaris de nou
					   if (postResponseCode == 406){
						   HttpEntity entity = response.getEntity();
	                       
						   String message = entity != null ? EntityUtils.toString(entity) : null;

						   //Extraurem la llista de noms del string
						   if (message.contains ("app_usernames")) {
							   String [] split1 = message.split ("app_usernames\\Q\\\":[\\E");
							   if (split1.length > 1) {
								   String [] split2 = split1[1].split ("\\Q]\\E");
								    
								   if (split2[0] != null && split2[0].startsWith("\\\"")) {

									   String nameArrayString = split2[0].replace("\\","");
									   //Un cop ja tenim el string amb la llista de noms l'hem de partir i eliminar-los del receptientsIds
									   String [] nameArray = nameArrayString.split (",");
									   
									   String newReceptientsIds = receptientsIds;
									   for (String a : nameArray) {
										   //Primer busquem si existeix amb "loquesigui",
										   newReceptientsIds = newReceptientsIds.replace (a + ",","");
										   //Si no existeix potser es que es l'ultim i ho busquem sense ,
										   newReceptientsIds = newReceptientsIds.replace (a,"");
									   }
									   
									   //En cas de borra l'ultim també la treurem
									   newReceptientsIds = newReceptientsIds.replace (",]","]");
									   
									   //Provarem de nou a enviar una petició d'enviament
									   JSON_STRING= "{\n"
												+ "\"import_code\" : \""+ importCode +"\",\n"
												+ "\"token\": \""+ token +"\",\n"
												+ "\"recipient_role_name\": \"TODOS\",\n"
												+ "\"recipients\": "
												+ "		{\n "
												+ "		\"app_usernames\":"+ newReceptientsIds + "\n"
												+ "},\n"
												+ "\"message\": {\n "
												+ "		\"title\": \""+ subjectEscapat + "\",\n "
												+ "		\"body\": \"" + bodyEscapat +"\\n\\n Missatge enviat des de l'espai del CV: " + StringEscapeUtils.escapeJson(siteTitle) + "\"\n"
												+ "}\n"
												+ "}";
									   
									   StringEntity requestEntity2 = new StringEntity(
											    JSON_STRING);
										
										HttpPost postMessage2 = new HttpPost(urlMessagesAppMobilServer);
										postMessage2.setEntity(requestEntity2);
										postMessage2.setHeader("Accept", "application/json");
									    postMessage2.setHeader("Content-type", "application/json");
									    
									    HttpResponse response2 = httpclient.execute(postMessage2); 
										int postResponseCode2 = response2.getStatusLine().getStatusCode();
										if (postResponseCode2 != 200) {
											M_log.debug("EnviaNotifAppMobil: ServerUdL response is not OK");
											M_log.debug("EnviaNotifAppMobil: Server CRUE response is " + postResponseCode2 + "Reason " + response2.getStatusLine().getReasonPhrase());
										}
										else {
											M_log.debug ("EnviaNotifAppMobil: Enviat ok");
											retorn = true;
										}
								   } else { // No hi ha usuaris descartats per tant no hem de fer res més. Donem-lo com enviat. 
									   retorn = true;
								   }
							   }
							   else  { // No hi ha usuaris descartats per tant no hem de fer res més. Donem-lo com enviat. 
								   retorn = true;   
							   }   
						   } else { // No hi ha usuaris descartats per tant no hem de fer res més. Donem-lo com enviat. 
							   retorn = true;
						   }
					   }
					   else {
						   M_log.debug ("EnviaNotifAppMobil: Codi resposta "+postResponseCode);
						   HttpEntity entity2 = response.getEntity();
	                       String message2 = entity2 != null ? EntityUtils.toString(entity2) : null;
	                       M_log.debug ("EnviaNotifAppMobil: Resposta servidor "+message2);
	                       // no fem retorn = true; perquè quedi a la taula entity_push i poder veure quin error dona el servidor
					   }
				 }
				 else {
				   retorn = true;
				 }
			}
			else {
				M_log.debug("EnviaNotifAppMobil: httpclient is null");
			}
			
        }catch (Exception ex){
        	ex.printStackTrace();
        }
       finally {
    	   httpclient.close();
        } 
	   return retorn;
    }
	
	
private boolean sendCRUECategory  (String subject, String author, String content, String siteId, String siteTitle, String notificationUrl, String categoryId) throws Exception {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		boolean retorn = false;
		String subjectEscapat = StringEscapeUtils.escapeJson(subject);
		String authorEscapat = StringEscapeUtils.escapeJson(author);
		String bodyEscapat = StringEscapeUtils.escapeJson(convertFormattedHtmlTextToPlaintext(content));
		String urlMessagesAppMobilServer = instanciaServerConfigurationService.getString("appMobil.AppCRUE.urlMessages"); 
		String importCode = instanciaServerConfigurationService.getString("appMobil.AppCRUE.importCode");
		String token = instanciaServerConfigurationService.getString("appMobil.AppCRUE.token");
	
		if ("udlinfo".equals(categoryId)) {
		
			String JSON_STRING= "{\n"
					+ "\"import_code\" : \""+ importCode +"\",\n"
					+ "\"token\": \""+ token +"\",\n"
					+ "\"recipient_role_name\": \"TODOS\",\n"
					+ "\"recipients\": "
					+ "		{\n "
					+ "		\"university_codes\":[\"udl\"]\n"
					+ "},\n"
					+ "\"message\": {\n "
					+ "		\"title\": \""+ subjectEscapat + "\",\n "
					+ "		\"body\": \"" + bodyEscapat +"\\n\\n Missatge enviat des de l'espai del CV: " + StringEscapeUtils.escapeJson(siteTitle) + "\"\n"
					+ "}\n"
					+ "}";
			
			StringEntity requestEntity = new StringEntity(
				    JSON_STRING);
			
			HttpPost postMessage = new HttpPost(urlMessagesAppMobilServer);
			postMessage.setEntity(requestEntity);
			postMessage.setHeader("Accept", "application/json");
		    postMessage.setHeader("Content-type", "application/json");
			
			
			try {
				if (httpclient != null) {
					//Fem la primera petició, si la llista d'usuaris està bé farà el missatge i tot anirà perfecte
					HttpResponse response = httpclient.execute(postMessage); 
					int postResponseCode = response.getStatusLine().getStatusCode();
					
					M_log.debug("EnviaNotifAppMobil: Server CRUE response is " + postResponseCode + "Reason " + response.getStatusLine().getReasonPhrase());
						
					 if (postResponseCode != 200) {
						   M_log.debug("EnviaNotifAppMobil: Server CRUE response is not OK, prove");
					 }
					 else {
					   retorn = true;
					 }
				}
				else {
					M_log.debug("EnviaNotifAppMobil: httpclient is null");
				}
				
	        }catch (Exception ex){
	        	ex.printStackTrace();
	        }
	       finally {
	    	   httpclient.close();
	        } 
		}else {
			//No es un codi vàlid i per tant el donem com enviat pero descartem
			M_log.debug ("EnviaNotifAppMobil: Descartem missatge, s'envia des d'un categoryid no autoritzat");
			retorn = true;
		}
	   return retorn;
    }

	private String convertFormattedHtmlTextToPlaintext(String htmlText) {
		/*
		 * replace "<p>" with nothing. Replace "</p>" and "<p />" HTML
		 * tags with "<br />"
		 */
		if (htmlText == null)
			return "";

		htmlText = htmlText.replaceAll("<p>", "");
		htmlText = htmlText.replaceAll("</p>", "###invent###");
		htmlText = htmlText.replaceAll("<p />", "###invent###");
		htmlText = htmlText.replaceAll("<br />", "###invent###");
		htmlText = htmlText.replaceAll("<br >", "###invent###");
		htmlText = htmlText.replaceAll("<br>", "###invent###");
		htmlText = FormattedText.convertFormattedTextToPlaintext(htmlText);
		htmlText = htmlText.replaceAll ("###invent###","\n");
		return htmlText;
	}
	
}
