package es.udl.asic.component.app.directori;

import java.util.Properties;
import es.udl.asic.api.app.directori.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.sakaiproject.db.api.SqlReaderFinishedException;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.id.cover.IdManager;



import es.udl.utilities.ldap.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.List;
import java.util.ArrayList;



public class AccountServiceImpl implements AccountService {
	
	private SqlService sqlService;
	private String TESTSQL = "SELECT * from DEMOTABLE";
	private String INSERT_EVIDENCIA = "INSERT INTO EVIDENCIES (EVENT_DATA,ACCIO,DNI,LOGIN,MAIL_ALTERNATIU,IP,AGENT) VALUES (SYSDATE,?,?,?,?,?,?)";
	private String CERCA_VALIDACIO = "SELECT * FROM VALIDA WHERE ID=? AND VALIDATED IS NULL AND CREATED > SYSDATE - INTERVAL '1' HOUR";
	private String INSEREIX_TOKEN = "INSERT INTO VALIDA (ID,USERID,CREATED) VALUES (?,?,SYSDATE)";
	private String UPDATE_TOKEN = "UPDATE VALIDA SET VALIDATED=SYSDATE WHERE USERID=? AND VALIDATED IS NULL";
	private String DELETE_OLD_NOTVALIDATED_TOKENS = "DELETE FROM VALIDA WHERE USERID=? AND VALIDATED IS NULL";
	private LDAPUdLUtils ldapUdlUtils = null;
	
	private String ldapserverrwstr;
	private String ldapserverrostr;
	private String securityprincipalstr;
	private String securitycredentialsstr;
	
	private String base;
	private String baseRoot;
	private String baseAlu;
	
	private LDAPUdLUtils getLdapUtils () {
		if (ldapUdlUtils == null){
			ldapUdlUtils = new LDAPUdLUtils ();
			ldapUdlUtils.setLdapserver_rw(ldapserverrwstr);
			ldapUdlUtils.setLdapserver_ro(ldapserverrostr);
			ldapUdlUtils.setSecurityPrincipal(securityprincipalstr);
			ldapUdlUtils.setSecurityCredentials(securitycredentialsstr);	
		}
		
		return ldapUdlUtils;
		
	}
	
	public void setSqlService(SqlService sqlService){
		this.sqlService = sqlService;
	}
	
	public SqlService getSqlService(){
		return sqlService;
	}

	public void setLdapserverrwstr (String ldapserverrwstr){
		this.ldapserverrwstr = ldapserverrwstr;
	}
	
	public String getLdapserverrwstr() {
		return ldapserverrwstr;
	}
	
	public void setLdapserverrostr (String ldapserverrostr) {
		this.ldapserverrostr = ldapserverrostr;
	}
	
	public String getLdapserverrostr() {
		return ldapserverrostr;
	}
	
	public void setSecurityprincipalstr(String securityprincipalstr){
		this.securityprincipalstr = securityprincipalstr;
	}
	
	public String getSecurityprincipalstr () {
		return securityprincipalstr;
	}
	
	public void setSecuritycredentialsstr (String securitycredentialsstr) {
		this.securitycredentialsstr = securitycredentialsstr;
	}
	
	public String getSecuritycredentialsstr (){ 
		return securitycredentialsstr;
	}
	
	public void setBase (String base){
		this.base = base;
	}
	
	public String getBase () {
		return base;
	}
	
	public void setBaseAlu (String baseAlu){
		this.baseAlu = baseAlu;
	}
	
	public String getBaseAlu () {
		return baseAlu;
	}
	
	public void setBaseRoot (String baseRoot){
		this.baseRoot = baseRoot;
	}
	
	public String getBaseRoot () {
		return baseRoot;
	}
	
	public void test() {
			
		Connection conn = getSimpleConnection();
		System.out.println ("Tenim connexio");
		Object [] objectList = null;
		
		try {
			Object retValue =  sqlService.dbRead(conn,TESTSQL,objectList,new org.sakaiproject.db.api.SqlReader() {
				public Object readSqlResultRecord(ResultSet result)
					throws SqlReaderFinishedException {
					try {
						if (result!=null){
							System.out.println (result.getString ("ID"));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				return null;
				}
			});
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			returnConnection (conn);
		}
	}
	
	private Connection getSimpleConnection() {
		Connection c = null;
		try {
			c = getSqlService().borrowConnection();
			c.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}

	private void returnConnection(Connection conn) {
		getSqlService().returnConnection(conn);
	}
	

	
	
	//Hem de mirar si son PAS/PDI, alumne o pas.
	
	public List<String> cercaPerEmailAlternatiu (String email) {
		
		List <String> usernames = new ArrayList<String> ();
		
		if (email != null && !"".equals(email)) {
		
			String filtre = "(&(objectclass=posixAccount)(mailMessageStore="+email+"))";
			NamingEnumeration enumer = getLdapUtils().searchLDAP(filtre,baseRoot);
			
			try{
				Attributes attrs = null;
				SearchResult result = null;
				
				while (enumer.hasMore()){
					result = (SearchResult) enumer.next();
					attrs = result.getAttributes();
					String uid=attrs.get("uid").get().toString();
					usernames.add (uid);
				}
			}catch(Exception ex){
				ex.printStackTrace ();
				System.out.println("Error al provar si existeix el correu");
				return usernames;
			}
		}
		
		return usernames;
	}
	
	
	
	public List<DadesPersonals> cercaPerNomUsuari (String username){
		List <DadesPersonals> usuaris = new ArrayList<DadesPersonals> ();
		
		if (username != null && !"".equals(username)) {
		
			String filtre = "(&(objectclass=posixAccount)(uid="+username+"))";
			NamingEnumeration enumer = getLdapUtils().searchLDAP(filtre,baseRoot);
			
			try{
				Attributes attrs = null;
				SearchResult result = null;
				
				if (!enumer.hasMore()){ // No coincidencies
					System.out.println ("No hi ha coincidències");
					return null;
				}
				
				while (enumer.hasMore()){
					result = (SearchResult) enumer.next();
					attrs = result.getAttributes();
					String accountStatus = "";
					String emailAlternatiu = "";
					String nom = "";
					String cognoms = "";
					String estat ="";
					
					//Mirem l'estat del compte. 
					try{
						accountStatus = attrs.get("accountStatus").get().toString();
					}catch (Exception ex){
						accountStatus="";
					}
					
					try{
						nom = attrs.get("givenName").get().toString();
					}catch (Exception ex){
						nom = "";
					}
					
					try{
						cognoms = attrs.get("sn").get().toString();
					}catch (Exception ex){
						cognoms= "";
					}
					
		
					try{
						emailAlternatiu = attrs.get("mailMessageStore").get().toString();
					}catch (Exception ex){
						emailAlternatiu="";
					}	
 
					if (!"".equals(emailAlternatiu)){
						System.out.println ("Creem l'usuari" + username);
						DadesPersonals d = new DadesPersonals();
						d.setUid(username);
						d.setNom(nom);
						d.setCognoms(cognoms);
						d.setEstat (accountStatus);
						d.setCorreuAlternatiu (emailAlternatiu);
						usuaris.add (d);
					}
					
				}
			}catch(Exception ex){
				ex.printStackTrace ();
				System.out.println("Error al buscar si l'usuari existeix el correu");
				return usuaris;
			}
		}
		
		return usuaris;
		
	}
	public String creaToken (String username) {
		//Eliminem tokens antics no validats
		Connection conn = getSimpleConnection();
		Object [] objectList = new Object [1];
		objectList[0] = username;
		String token = null;
		
		
		try {
			getSqlService().dbWrite(conn, DELETE_OLD_NOTVALIDATED_TOKENS, objectList);
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			returnConnection (conn);
		}
		
		
		conn = getSimpleConnection();
		token = IdManager.createUuid();
		
		//Creem un token nou
		objectList = new Object [2];
		objectList[0] = token;
		objectList[1] = username;
		
		try {
			 getSqlService().dbWrite(conn, INSEREIX_TOKEN, objectList);
		} catch (Exception ex){
			ex.printStackTrace();
			token = null;
		} finally {
			returnConnection (conn);
		}
		
		return token; 
	}

	public DadesPersonals checkForToken (String token) {
		Connection conn = getSimpleConnection();
		Object [] objectList = new Object [1];
		objectList[0] = token;
		
		
		try {
			List validTokens = (List) sqlService.dbRead(conn,CERCA_VALIDACIO,objectList,new org.sakaiproject.db.api.SqlReader() {
			
				public Object readSqlResultRecord(ResultSet result)
					throws SqlReaderFinishedException {
					try {
						if (result!=null){
							System.out.println ("El userid" + result.getString ("USERID"));
							return result.getString ("USERID");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return null;
				}
			});
			
			if (validTokens.size() > 0) {
				//Busquem el primer usuari al LDAP amb aquest id per treure'n el nom
				List <DadesPersonals> usuaris = cercaPerNomUsuari ((String) validTokens.get(0));
				
				
				if (usuaris.size() > 0) {
					System.out.println ("Retorneu un usuari");
					return usuaris.get(0);
				}
				else {
					System.out.println ("Retorneu un buit");
					return null;
				}
			} else {
				System.out.println ("No hi ha validTokens");
			}

			
			
			
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			returnConnection (conn);
		}
		
		return null;
	}
	
	public boolean canviaClau (String username, String password) {
		
		//Primer intenta canviar el password al PAS
		try{
			boolean fallaPAS = false;
			
			if (ldapUdlUtils.canviar_passwd (username,password,base) != 0){
				fallaPAS = true;
			} 
			
			if (ldapUdlUtils.canviar_passwd (username,password,baseAlu)!= 0){
				if (fallaPAS) { // En aquest cas han fallat tots dos canvis.
					return false;
				}
			}
			
		} catch (Exception ex) {
			return false;
		}
		
		Connection conn = getSimpleConnection();
		Object [] objectList = new Object [1];
		objectList[0] = username;
		
		try {
			getSqlService().dbWrite(conn, UPDATE_TOKEN, objectList);
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			returnConnection (conn);
		}
		
		return true;
	}

	
	public boolean desaEvidencia (String accio,String dni, String login, String mailAlternatiu, String ip, String agent){
		Connection conn = getSimpleConnection();
		System.out.println ("Tenim connexio");
		Object [] objectList = new Object [6];
		objectList[0] = accio;
		objectList[1] = dni;
		objectList[2] = login;
		objectList[3] = mailAlternatiu;
		objectList[4] = ip;
		objectList[5] = agent;
		
		try {
			return getSqlService().dbWrite(conn, INSERT_EVIDENCIA, objectList);
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			returnConnection (conn);
		}
		return false; 
	}
	
}
