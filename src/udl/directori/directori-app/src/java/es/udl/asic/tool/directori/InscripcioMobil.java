package es.udl.asic.tool.directori;

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
import java.util.List;
import java.util.ArrayList;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;
import javax.faces.model.SelectItem;


public class InscripcioMobil{
	
	
	private String errorMsg;
	private DadesPersonals dadesActuals;
	private DadesPersonals novesDades;
	//private ResourceBundle messageBundle;
	private Pattern rmobile;

	private DirectoryService directoryService;
	private String userId;
	private boolean pdipas = false;
	
	public void setDirectoryService(DirectoryService ds){
		directoryService = ds;
		init();
	}
	
	public DirectoryService getDirectoryService(){
		return directoryService;
	}

	public DadesPersonals getDadesActuals(){
		return dadesActuals;
	}

	public void setDadesActuals(DadesPersonals dadesActuals) {
		this.dadesActuals = dadesActuals;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public DadesPersonals getNovesDades() {
		if (novesDades==null)
			init();
		return novesDades;
	}
	
	public void setNovesDades(DadesPersonals novesDades) {
		this.novesDades = novesDades;
	}
	
	public void init(){
		String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
		ResourceBundle messageBundle = ResourceBundle.getBundle(bundleName);
		
		Placement pla = ToolManager.getCurrentPlacement();
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
		
		//Obtenim les dades de l'usuari

		if (obtenirDades()){
			onReiniciarMobile();
			
		}else{
			
			errorMsg = messageBundle.getString("dadeserr1");
			//Hauriem de fer la redireccio
		}
		
		//Iniciem les expresió regular per a fer la validació
		
		String expressioMobil = "6[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]";
		rmobile = Pattern.compile (expressioMobil);
	}
	
	public String onDesarMobil(){
		if (validaDadesMobil()){
			return "revisadadesmobil";
		}
		else{
			return "errordadesmobil";
		}
	}
	
	public String onReiniciarMobile(){
		if (novesDades==null){
			novesDades = new DadesPersonals();
		}
		//Afegim totes les dades velles a les noves
		novesDades.setNom(dadesActuals.getNom());
		novesDades.setCognoms(dadesActuals.getCognoms());
		novesDades.setTlf(dadesActuals.getTlf());
		novesDades.setMobile(dadesActuals.getMobile());

		if (pdipas) {
			novesDades.setReenviament(dadesActuals.getReenviament());
			novesDades.setFax(dadesActuals.getFax());
			novesDades.setGuardacorreu(dadesActuals.getGuardacorreu());
			novesDades.setMissatgeria(dadesActuals.getMissatgeria());
		}
				
		novesDades.setWebpersonal(dadesActuals.getWebpersonal());
		novesDades.setUbicacio(dadesActuals.getUbicacio());
		novesDades.setCorreuprincipal (dadesActuals.getCorreuprincipal());
		
		

		return "canvimobil";
	}
	
	public String onCancelarMobile(){
		return "canvimobil";
	}	
	
	public String onFinalitzarMobil(){
		
		boolean canvidades =false;
		
		if (pdipas){//Això ha de ser un mecanisme provisional per esbrinar com desar-ho 
			canvidades = directoryService.canviDades(userId,novesDades);
		}else{
			canvidades = directoryService.canviDadesAlumne(userId,novesDades);
		}
		
		if (canvidades){
			return "correctedadesmobil";
		}
		else{
			String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
			ResourceBundle messageBundle = ResourceBundle.getBundle(bundleName);
			
			errorMsg = messageBundle.getString("dadeserr11");
			return "errordadesmobil";
		}
	}
	
	
	public String onOkConfirmarMobil(){
		return "canvimobil";
	}	
	
	public String onOkErrorMobil(){
		return "canvimobil";
	}	
	
	private boolean validaDadesMobil(){
		//String errorMsg="";
		boolean valid=true;
	
		
	if (!rmobile.matcher(novesDades.getMobile()).matches() && !novesDades.getMobile().equals("")){
		String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
		ResourceBundle messageBundle = ResourceBundle.getBundle(bundleName);
		errorMsg = messageBundle.getString ("dadeserr11");
		valid = false;
		}
				

	//S'ha de poder deixar buit per si de cas.
		/*if (novesDades.getMobile().equals("")){ //
			errorMsg = messageBundle.getString ("dadeserr11");
			valid = false;
	}		*/
		return valid;
	}
	
	
	private boolean obtenirDades(){
		dadesActuals =  directoryService.obtenirDadesAlumne(userId);
		
		if (dadesActuals==null){
			pdipas=true;
			dadesActuals =  directoryService.obtenirDades(userId);
		}

		if (dadesActuals==null){
			
			String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
			ResourceBundle messageBundle = ResourceBundle.getBundle(bundleName);

			errorMsg = messageBundle.getString("dadeserr5");
			return false; 
		}
		
		return true;
	}
	
	
}