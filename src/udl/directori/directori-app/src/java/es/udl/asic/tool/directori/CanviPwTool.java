package es.udl.asic.tool.directori;


import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;

import es.udl.asic.api.app.directori.DirectoryService;
import es.udl.asic.api.app.directori.DadesPersonals;
import org.sakaiproject.tool.api.Session; 
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.event.api.UsageSession;
import java.util.Properties;
import java.util.ResourceBundle;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CanviPwTool{
	
	private String pwOld="";
	private String pwNew="";
	private String pwNew2="";
	private String errorMsg="";
	private String userId="";
	private boolean pdipas=false;
	
	private ResourceBundle messageBundle = null;
	
	private DirectoryService directoryService=null;
	
	public String getUserId(){
		return userId;
	}
	
	public void setDirectoryService(DirectoryService ds){
		directoryService = ds;
		init();
	}
	
	public DirectoryService getDirectoryService(){
		return directoryService;
	}
	
	public void setPwOld(String pwOld){
		this.pwOld = pwOld;
	}
	
	public String getPwOld(){
		return pwOld;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public void setPwNew(String pwNew){
		this.pwNew = pwNew;
	}
	
	public String getPwNew (){
		return pwNew;
	}
	
	public void setPwNew2(String pwNew2){
		this.pwNew2 = pwNew2;
	}
	
	public String getPwNew2 (){
		return pwNew2;
	}
	
	public void init(){
		Placement pla = ToolManager.getCurrentPlacement();

		String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
     	messageBundle = ResourceBundle.getBundle(bundleName);

		Properties lst = new Properties();
		
		lst.setProperty("ldapserver_ro",pla.getConfig().getProperty("ldapserver_ro"));
		lst.setProperty("ldapserver_rw",pla.getConfig().getProperty("ldapserver_rw"));
		lst.setProperty("securityPrincipal",pla.getConfig().getProperty("securityPrincipal"));
		lst.setProperty("securityCredentials",pla.getConfig().getProperty("securityCredentials"));
		lst.setProperty("base",pla.getConfig().getProperty("base"));
		lst.setProperty("baseRoot",pla.getConfig().getProperty("baseRoot"));
		lst.setProperty("baseAlu",pla.getConfig().getProperty("baseAlu"));
		
		UsageSession uses = UsageSessionService.getSession();
		userId = uses.getUserEid();
		
		//Passem els paràmetres de configuracio al servei de ldap
		try{			
			directoryService.configParameters(lst);
		}catch (Exception ex){
			System.out.println("Excepció a l'inicialitzar el servei de ldap");
		}
		
		if (!obtenirDades()){
			errorMsg = messageBundle.getString("pwerr1");
		}
		
	}
	
	public String onDesar(){
		if (validaOldPasswd()){
			if (validaNewPasswd()){
				boolean changed= pdipas ? directoryService.canviPasswd(userId,pwNew) :  directoryService.canviPasswdAlu(userId,pwNew);
				
				if (changed){
					return "correctepw";
				}
				else{
					errorMsg = messageBundle.getString("pwerr2");
					//errorMsg = "S'ha produït un error al fer el canvi de clau. La clau nova no és vàlida";
					return "errorpw";
				}
				
			}
			else{
				return "errorpw";
			}
		}
		else {
			errorMsg = messageBundle.getString("pwerr3");
			return "errorpw";
		}
	}
	
	public String onReiniciar(){
		pwNew = "";
		pwNew2 = "";
		pwOld ="";
		return "canvipw";
		
	}
	
	public String onOkError(){
		return "canvipw";
	}
	
	public String onOkConfirmar(){
		return "canvipw";
	}

	private boolean validaNewPasswd(){
		
		if (!pwNew.equals(pwNew2)){
			errorMsg = messageBundle.getString("pwerr4");
			return false;
		}

		/*if (pwNew.equals("")){
			errorMsg = messageBundle.getString("pwerr5");
			return false;
		}
		if (pwNew.length() <6 ){
			errorMsg = messageBundle.getString("pwerr7");
			return false;
		}*/

		if (!validatePassword(pwNew)){
			errorMsg = messageBundle.getString ("activaerror3");
			return false;
		}
		
	return true;
	}
	
	
	// Nou passwd---
	
	private boolean validatePassword(String password){
		int number = contains(".*([a-z])+.*",password) +  contains(".*([A-Z])+.*",password) + contains(".*([0-9])+.*",password)  + 
					 contains(".*(\\.|\\_|\\-|\\(|\\)|\\[\\]\\:\\;\\,\\ç\\{\\}\\*\\^\\!\\\"\\#\\$\\&\\/\\=\\<\\>)+.*",password);
		return  (number >=3 && password.length() >= 8);
	}
	
	private int contains(String regexpstring,String key){
		Pattern regexp = Pattern.compile(regexpstring);
		
		if (regexp.matcher(key).matches())
			return 1;
		else return 0;
	}

	// Validacio
	
	private boolean validaOldPasswd(){
		return pdipas ? directoryService.hasThisPassword(userId,pwOld) : directoryService.hasThisPasswordAlu(userId,pwOld);
	}
	
	private boolean obtenirDades(){
		DadesPersonals dadesActuals =  directoryService.obtenirDadesAlumne(userId);

		if (dadesActuals==null){
			pdipas=true;
			dadesActuals =  directoryService.obtenirDades(userId);
		}

		if (dadesActuals==null){
			
			String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
			ResourceBundle messageBundle = ResourceBundle.getBundle(bundleName);

			errorMsg = messageBundle.getString("pwerr6");
			return false; 
		}
		
		return true;
	}
	
	
}

