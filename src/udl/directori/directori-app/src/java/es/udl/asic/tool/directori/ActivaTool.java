package es.udl.asic.tool.directori;

import es.udl.asic.api.app.directori.DirectoryService;
import es.udl.asic.api.app.directori.DadesPersonals;
import es.udl.asic.api.app.directori.AccountService;
import es.udl.asic.api.app.directori.MatriculaService;
import es.udl.asic.api.app.directori.RegistreActiva;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.event.api.UsageSession;


import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;
import javax.faces.model.SelectItem;
import javax.faces.component.UIComponent;

import javax.faces.model.SelectItem;
import org.sakaiproject.email.cover.EmailService;
import javax.servlet.http.HttpServletRequest;

public class ActivaTool{
	private String errorMsg="";
	private String emailAvis="";
	private ResourceBundle messageBundle;
	private AccountService accountService;

	
	private String login="";
	private String identificador="";
	private String codi="";
	private String password="";
	private String passwordVerificacio="";
	private String email="";
	private String domini="@<subdomini>.udl.cat";
	private String adreces="";
	private UIComponent commandLink;
	
	private DadesPersonals dadesPersonals=null;
	private RegistreActiva registreActiva = null;
	
	
	private DirectoryService directoryService;
	private MatriculaService matriculaService;
	
	
	/*Getters i Setters del atributs*/
	
	public void init(){
		String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
     	messageBundle = ResourceBundle.getBundle(bundleName);
		
     	//Configurem els paràmetres del servei de ldap
		Placement pla = ToolManager.getCurrentPlacement();
		Properties lst = new Properties();

		lst.setProperty("ldapserver_ro", pla.getConfig().getProperty(
				"ldapserver_ro"));
		lst.setProperty("ldapserver_rw", pla.getConfig().getProperty(
				"ldapserver_rw"));
		lst.setProperty("securityPrincipal", pla.getConfig().getProperty(
				"securityPrincipal"));
		lst.setProperty("securityCredentials", pla.getConfig().getProperty(
				"securityCredentials"));
		lst.setProperty("base", pla.getConfig().getProperty("base"));
		lst.setProperty("baseAlu",pla.getConfig().getProperty("baseAlu"));
		lst.setProperty("baseRoot",pla.getConfig().getProperty("baseRoot"));
		lst.setProperty("aluDiscUrl",pla.getConfig().getProperty("aluDiscUrl"));
		lst.setProperty("aluDiscUsername",pla.getConfig().getProperty("aluDiscUsername"));
		lst.setProperty("aluDiscPassword",pla.getConfig().getProperty("aluDiscPassword"));

		//Obtenim les adreces de correu per enviar els missatges
		adreces = pla.getConfig().getProperty("correus");
		
		String db = pla.getConfig().getProperty("db");
		String driver = pla.getConfig().getProperty("driver");
		
		// Passem els paràmetres de configuracio al servei de ldap
		try {
			directoryService.configParameters(lst);
			matriculaService.setDb(db);
			matriculaService.setDriver(driver);
		}catch (Exception ex) {
			System.out.println("Excepció a l'inicialitzar el servei de ldap");
		}
	}
	
	public void setDirectoryService(DirectoryService ds){
		directoryService = ds;
		if (matriculaService!=null)
			init();
	}
	
	public void setMatriculaService (MatriculaService ms){
		matriculaService = ms;
		if (directoryService!=null)
			init();
	}
	
	public void setAccountService (AccountService accountService){
		this.accountService = accountService;
	}
	
	public AccountService getAccountService (){
		return this.accountService;
	}
	
	
	public void canviaIdioma (String idioma) {
		messageBundle = ResourceBundle.getBundle("es.udl.asic.tool.directori.bundle.Messages",new Locale(idioma));
		
		FacesContext context = FacesContext.getCurrentInstance();
		//context.getViewRoot().setLocale (new Locale(idioma));
		UIViewRoot view = context.getViewRoot();
		view.setLocale (new Locale(idioma));
	}
	
	public String canviaIdiomaActivacioAluCa () {
		canviaIdioma("ca");
		return "activaAlu";
	}
	
	public String canviaIdiomaActivacioAluEs () {
		canviaIdioma("es");
		return "activaAlu";
	}
	
	public String canviaIdiomaActivacioAluEng () {
		canviaIdioma("en");
		return "activaAlu";
	}
	
	public UIComponent getCommandLink() {
	    return commandLink;
	}

	public void setCommandLink(UIComponent commandLink) {
	    this.commandLink = commandLink;
	}
	
	public DirectoryService getDirectoryService(){
		return directoryService;
	}
	public String getNom(){
		return dadesPersonals.getNom();
	}
	public String getCognoms(){
		return dadesPersonals.getCognoms();
	}
	public String getDni(){
		return dadesPersonals.getDni();
	}
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getEmail() {
		return email;
	}
	public String getEmailComplet (){
		return dadesPersonals.getCorreuprincipal();
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDomini(){
		return domini;
	}
	public void setDomini(String domini){
		this.domini = domini;
	}
	
	public String getIdentificador(){
		return identificador;
	}
	public void setIdentificador(String identificador){
		this.identificador = identificador;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPasswordVerificacio() {
		return passwordVerificacio;
	}
	
	public void setPasswordVerificacio(String passwordVerificacio) {
		this.passwordVerificacio = passwordVerificacio;
	}
	
	public String getErrorMsg(){
		return errorMsg;
	}
	public String getEmailAvis(){
		return emailAvis;
	}
	
	/*Accions*/

	public String onOkConfirmar(){
		return "main";
	}

	public String onOkError(){
		return "activa";
	}
	public String onOkErrorMail (){
		return "activaMail";
	}

	public String activa(){
		String emailtotal = null;
			
		if (!test())
			return "erroractiva";
		
		String correuprov = dadesPersonals.getCorreuprincipal();
		//Només agafem (per assegurar) a partir del @
		
		String [] captions = correuprov.split ("@");
		
		if (correuprov.startsWith("@")){
			emailtotal = email + correuprov;
			domini = correuprov; 
		}else if (captions.length == 2){
			emailtotal = correuprov;
			domini = captions[1];
			email= captions[0];
			return saveActivacio(emailtotal);
		}
		
		return "activaMail";
	}
	
	public String activaAlu(){
		HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
		String ip = getRemoteAddress(httpServletRequest);
		String agent = httpServletRequest.getHeader("User-agent");
		
		errorMsg = null;
		
		//valida tot
		if (!validateRegistreExistent()){
			//El missatge ficat al test
			return "activaAlu";
		}
		
		if (!validatePassword()){
			accountService.desaEvidencia ("ACTIVAALU_ERROR_CLAUFORMATINCORRECTE",dadesPersonals.getDni(), dadesPersonals.getUid(), null , ip, agent);
			errorMsg = messageBundle.getString("activaerror3");
			return "activaAlu";

		}
		if (!validatePasswords()){
			accountService.desaEvidencia ("ACTIVAALU_ERROR_NOCOINCIDEIXEN",dadesPersonals.getDni(), dadesPersonals.getUid(), null , ip, agent);
			errorMsg = messageBundle.getString("activaerror4");
			return "activaAlu";

		}
		
		return saveActivacioAlu ();
	}
	

	public String activaCorreu(){
		String emailtotal = null;
		String correuprov = dadesPersonals.getCorreuprincipal();
		
		if (correuprov.startsWith("@")){
			emailtotal = email + correuprov;
			domini = correuprov; 
		}

		
		if (!validaEmail(email) || directoryService.mailExisteix(emailtotal)){
			errorMsg = messageBundle.getString("activaerror5");
			return "erroractivamail";
		}
				
		return saveActivacio (emailtotal);
	}
	
	
	private String saveActivacioAlu(){
		HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
		String ip = getRemoteAddress(httpServletRequest);
		String agent = httpServletRequest.getHeader("User-agent");
		
		if (!directoryService.activaCompteAlu(registreActiva.getLogin(),password)) {
			errorMsg = "Error a l'activar el compte";
			return "activaAlu";
		}
		
		//Obtenim de nou les dades per assegurar-nos què s'ha desat
		dadesPersonals = directoryService.obtenirDadesAlumne(registreActiva.getLogin());
		login = registreActiva.getLogin();
		
		//Enviem un missatge al compte alternatiu de l'alumne amb aquestes dades.
		accountService.desaEvidencia ("ACTIVAALU_OK",dadesPersonals.getDni(), login, dadesPersonals.getCorreuAlternatiu() , ip, agent);
		
		if (!"".equals(dadesPersonals.getCorreuAlternatiu())){
			String miss =  messageBundle.getString ("activaciomailok") + "\n" + login +" \n\n" ; 
				   miss = miss + messageBundle.getString ("missatgeactivaokfinal"); 
			
			enviaMissatge (messageBundle.getString("activasubject"),miss,dadesPersonals.getCorreuAlternatiu() ); 
		}
		
		
		return "correcteactivaalu";
	}
	
	private String saveActivacio (String emailtotal){
	
		if (!directoryService.activaCompte(login,password,emailtotal)){
			errorMsg = "Error a l'activar el compte";
			return "erroractiva";
		}
		
		//Obtenim de nou les dades per assegurar-nos què s'ha desat
		dadesPersonals = directoryService.obtenirDades(login,true);
	
	
		
		String miss= messageBundle.getString("activamessage") + "\n" +
					 messageBundle.getString("activalogin") + " " + login + "\n" +
					 messageBundle.getString("dni") + " " + getDni() + "\n" +
					 messageBundle.getString("info_cognoms") + " " +dadesPersonals.getCognoms() +"\n" +
					 messageBundle.getString("info_nom") + " " + dadesPersonals.getNom() + "\n" +
					 messageBundle.getString("correuprincipal") + " " + dadesPersonals.getCorreuprincipal() + "\n";
		
		enviaMissatge(miss);
		return "correcteactiva";
	}
	
	
	public String cancela(){
		return "main";
	}
	
	public String comprova(){
		return "activa";
	}
	
	private boolean test(){
		if (!validateLogin()){
			errorMsg = messageBundle.getString("activaerror1");
			return false;
		}
		if (!validateCodi()){
			errorMsg = messageBundle.getString("activaerror2");
			return false;
		}
		if (!validatePassword()){
			errorMsg = messageBundle.getString("activaerror3");
			return false;
		}
		if (!validatePasswords()){
			errorMsg = messageBundle.getString("activaerror4");
			return false;
		}
		/*if (!validaEmail(email)){
			errorMsg = messageBundle.getString("activaerror5");
			return false;
		}*/
		return true;
	}
	
	
	private boolean validateLogin(){
		if (contains(".*(\\.|\\_|\\-|\\(|\\)|\\[\\]\\:\\;\\,\\ç\\{\\}\\*\\^\\!\\\"\\#\\$\\&\\/\\=\\<\\>).*",login)==1){
			return false;
		}
		//El cridem d'aquesta forma per a poder obtenir les dades com a Manager (És perillós, augmentem l'acoblament entre el controlador i el model
		dadesPersonals = directoryService.obtenirDades(login,true);
		
		if	(dadesPersonals!=null){ 
			String codiTemp = dadesPersonals.getCodi();

			if (codiTemp.trim().equals("activat") || codiTemp==null ){
				dadesPersonals=null;
			}
		}
		
		return dadesPersonals!=null;
	}
	
	private boolean validateCodi(){
		return codi.equals(dadesPersonals.getCodi());
	}
	
	private boolean validatePassword(){
		int number = contains(".*([a-z])+.*",password) +  contains(".*([A-Z])+.*",password) + contains(".*([0-9])+.*",password)  + 
					 contains(".*(\\.|\\_|\\-|\\(|\\)|\\[\\]\\:\\;\\,\\ç\\{\\}\\*\\^\\!\\\"\\#\\$\\&\\/\\=\\<\\>).*",password);
		return  (number >=3 && password.length() >= 6);
	}
	
	private boolean validatePasswords(){
		return password.equals(passwordVerificacio);
	}
	
	//Validation methods for alumnes
	private boolean validateRegistreExistent () {
		HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();  
		String ip = getRemoteAddress(httpServletRequest);
		String agent = httpServletRequest.getHeader("User-agent");
		
		registreActiva = null;
		
		if ("".equals (identificador)){
			errorMsg = messageBundle.getString("activaaluerror7");
			return false;
		}
		
		if ("".equals (codi)){
			errorMsg = messageBundle.getString("activaaluerror8");
			return false;
		}
		
		if (!isInteger(codi)) {
			errorMsg = messageBundle.getString("activaaluerror2");
			return false;
		}
		
		if (identificador != null) {
			registreActiva = matriculaService.getRegistreActiva (identificador);
		
			if (registreActiva != null && registreActiva.getLogin()!=null) {
		
				dadesPersonals = directoryService.obtenirDadesAlumneAsAdmin(registreActiva.getLogin());
				
				//Tot quadra
				if	(dadesPersonals!=null) { 
					//Comprovem el número de matrícula
					if (registreActiva.getCodiMatricula() != null && registreActiva.getCodiMatricula().contains(codi)){
						String codiTemp = dadesPersonals.getCodi();

						if (codiTemp.trim().equals("activat") || codiTemp==null ){
							accountService.desaEvidencia ("ACTIVAALU_ERROR_JAACTIVAT",identificador, dadesPersonals.getUid(), null , ip, agent);
							errorMsg = messageBundle.getString("activaaluerror3");
							dadesPersonals=null;
							return false;
						} 
						else if (codiTemp.trim().equals("pendentAlu")) {
							return true;
						}
						
					} else {
						accountService.desaEvidencia ("ACTIVAALU_ERROR_NUMEXPEDIENTINCORRECTE",identificador, dadesPersonals.getUid(), null , ip, agent);
						errorMsg = messageBundle.getString("activaaluerror4");
						return false;
					}
				} else {
					accountService.desaEvidencia ("ACTIVAALU_ERROR_ENCARANOEXISTEIXLDAP",identificador, null, null , ip, agent);
					errorMsg = messageBundle.getString("activaaluerror5");
					return false;
				}
			} else {
				accountService.desaEvidencia ("ACTIVAALU_ERROR_DNINOVALID",identificador, null, null , ip, agent);
				errorMsg = messageBundle.getString("activaaluerror6");
				return false;
			}
		}
		
		return false;
	}
	
	
	private int contains(String regexpstring,String key){
		Pattern regexp = Pattern.compile(regexpstring);
		
		if (regexp.matcher(key).matches())
			return 1;
		else return 0;
	}
	
	private boolean validaEmail (String vemail){
		if (contains(".*(à|á|è|é|ì|í|ò|ó|ù|ú|À|Á|È|É|Ì|Í|Ò|Ó|Ù|Ú|@).*",vemail)==1){
			return false;
		}
		
		return true;
	}
	
	private void enviaMissatge(String missatge){
		String from= "noreply@cv.udl.cat";
		String subject = messageBundle.getString("activasubject");
		String [] corre = adreces.split(" ");
		
		for (int i=0; i< corre.length;i++)
			EmailService.send(from,corre[i],subject,missatge,null,null,null);
	
	}
	
	
	private void enviaMissatge(String subject, String missatge, String email){
		String from= "noreply@cv.udl.cat";
		EmailService.send(from,email,subject,missatge,null,null,null);
	}
	
	private static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	private static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static String getRemoteAddress(HttpServletRequest req) {
	    String ipAddress = req.getHeader("X-FORWARDED-FOR");
	    if (ipAddress != null) {
	        ipAddress = ipAddress.replaceFirst(",.*", "");  // cares only about the first IP if there is a list
	    } else {
	        ipAddress = req.getRemoteAddr();
	    }
	    return ipAddress;
	}

	
}