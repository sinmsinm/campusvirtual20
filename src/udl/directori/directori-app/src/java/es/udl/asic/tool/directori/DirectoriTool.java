package es.udl.asic.tool.directori;

import es.udl.asic.api.app.directori.DadesPersonals;
import org.sakaiproject.email.cover.EmailService;
import es.udl.asic.api.app.directori.DirectoryService;
//import cat.udl.asic.sms.cover.SMSService; 
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import javax.faces.context.FacesContext;


import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;

import java.util.*;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import javax.naming.*;
import javax.naming.directory.*;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import org.sakaiproject.component.cover.ServerConfigurationService;
/**
 * 
 * @author Alex - ASIC
 * 
 * Aquesta classe s'encarrega de capturar els events, i definir el comportament
 * de la vista.
 */

public class DirectoriTool {

	// private Logger logger; //Servei de Log

	/* Atributs que representen els usuaris del curs */

	private String ident = ""; // Identificador del usuari seleccionat

	//private Directori directori; // Fitxa d'un usuari en concret

	private DirectoryService directoryService; // Servei afegit

	private List llistaAssist; // Llista de assistents al curs

	private Assistent assistent = null; // Assisten comodin que ens ajuda a

	// a establir valors a les linies del
	// llistat

	/* Atributs per poder paginar la llista */

	private int numPag = 1; // Numero de la pagina acutal

	private int numPerPag = 30; // Numero de registres per pagina

	private int numPagTotal = 0; // Numero de pagines total

	private int numReg = 0; // Numero de primer registre de la pagina

	// actual

	/* Cadenes que mostren informacio per pantalla */

	private String selite = "30"; // Selecció actual del nombre de usuaris per

	// pagina

	private String anterior = "<-";

	private String seguent = "->";

	/* Propietats per poder ordenar la llista per diferents camps */

	private int ordena = 0; // Especifica si és ascendent o descendent

	private int camp = 1; // Columna d'ordenacio

	private String strOrd = "";

	private String cerca = ""; // Patro de cerca
	
	private String mobil = ""; // Patro de mobil

	private boolean incloureEst = false; // Inclore els alumnes a a la cerca

	private String exists = "unknown";
	
	private int page = 1;
	
	private Assignatura assignatura = null;
	
	private String smsText="";

	private String acabats[] = null;
	/* Mètode per inicialitzar el servei de directori */
	
	public static String PERMIS_ENVIA = "sms.envia";
	
	private AuthzGroupService authzGroupService = null;

	public void setDirectoryService(DirectoryService ds) {
		directoryService = ds;
		init();
	}

	public void init() {
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
		lst.setProperty("baseRoot",pla.getConfig().getProperty("baseRoot"));
		lst.setProperty("baseAlu",pla.getConfig().getProperty("baseAlu"));
		
		String pacabats = pla.getConfig().getProperty("acabats");
		
		if (pacabats!=null){
			acabats = pacabats.split (",");
		}
		
		
		// Passem els paràmetres de configuracio al servei de ldap
		try {
			directoryService.configParameters(lst);
		}catch (Exception ex) {
			System.out.println("Excepció a l'inicialitzar el servei de ldap");
		}

		authzGroupService =  (AuthzGroupService) ComponentManager
				.get("org.sakaiproject.authz.api.AuthzGroupService");
		
	}

	/*-------------------------------------------------------------------------
	 * Conjunt de getter i setters de propietats
	 * 
	 * 
	 * ------------------------------------------------------------------------
	 */

	/* Getter i Setter de selite */
	

	public String getLoginUrl(){
		Placement pla = ToolManager.getCurrentPlacement();
		String idsite=pla.getContext();
		String reference = SiteService.siteReference(idsite);
		String portalUrl = ServerConfigurationService.getPortalUrl();
		String serverUrl = ServerConfigurationService.getServerUrl();

		return serverUrl + "/authn/login?url="  + portalUrl + reference;
	}
	
	public String getUserIsInDirectory() {
		// init the variable
		String userId ="";
		
		if (exists.equals("unknown")) {

			UsageSession uses = UsageSessionService.getSession();
			try{
				userId = uses.getUserEid();
			}
			catch(Exception ex){
				return exists;
			}
			
				if (directoryService.obtenirDades(userId) != null) {
					exists = "pdi/pas";
				}
				else if (directoryService.obtenirDadesAlumne(userId)!= null){
					exists  = "student";
				} else {
					exists = "false";
				}
				if (!exists.equals ("false")){
					//REinicia l'arbre de components per a que es pugui veure correctament
					FacesContext context = FacesContext.getCurrentInstance();
					UIViewRoot view = context.getViewRoot();
					List lst =view.getChildren();
				}

			
		}
		return exists;
	}

	public String getSelite() {
		return selite;
	}

	public void setSelite(String si) {
		selite = si;
	}


	public int getPage(){
		return page;
	}
	
	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}
	
	
	/*
	 * Getter i Setter del servei de Log
	 * 
	 * public Logger getLogger() { return logger; }
	 * 
	 * public void setLogger(Logger logger) { this.logger = logger; }
	 *  /* Getter i Setter del Assitent
	 */

	public void setAssistent(Assistent ass1) {
		assistent = ass1;
	}

	public Assistent getAssistent() {
		return assistent;
	}

	/* Getter i Setter de la llista d'assitents al curs */

	public void setLlistaAssist(List l) {
		llistaAssist = l;
	}

	public List getLlistaAssist() {
		return llistaAssist;
	}

	/*
	 * Getters de anterior (->) Calcula si ha de mostrar-se o no
	 */

	public String getAnterior() {
		if (numPag == 1) {
			anterior = "nogif.gif";
		} else {
			anterior = "anterior.gif";
		}

		if (numPag == numPagTotal) {
			seguent = "nogif.gif";
		} else {
			seguent = "seguent.gif";
		}

		return anterior;
	}

	/*
	 * Getters de seguent (->) No calcula res ja que ja es calcula en anterior
	 */

	public String getSeguent() {
		return seguent;
	}

	/* Getter i Seter de numPerPag */

	public void setNumPerPag(int numperpag) {
		numPerPag = numperpag;
	}

	public int getNumPerPag() {
		return numPerPag;
	}

	/* Getter i Setter de numPag (numero de pagina) */

	public int getNumPag() {
		return numPag;
	}

	public void setNumPag(int numpag) {
		numPag = numpag;
	}

	/* Getter de numReg Obtenir el numero del primer registre de la pagina */

	public int getNumReg() {
		return (numPerPag * numPag) - numPerPag;
	}

	/* Getter de numPagTotal - numero total de pagines */

	public int getNumPagTotal() {

		if (llistaAssist == null) {
			CreaLlistaUsuaris(); // Comprovem el null per només fer-ho el
									// primer cop que es pinta
		}

		numPagTotal = llistaAssist.size() / numPerPag;

		// Afegin una ultima pagina si conve
		if ((llistaAssist.size() % numPerPag) > 0) {
			numPagTotal++; // Li afegim la pagina de restos
		}

		// System.out.println("El numero total" + numPagTotal);
		return numPagTotal;
	}

	/* Getter i setter de cerca */

	public void setCerca(String par) {
		cerca = par;
	}

	public String getCerca() {
		return cerca;
	}

	
	/* Getter i setter de mobil */

	public void setMobil(String par) {
		mobil = par;
	}

	public String getMobil() {
		return mobil;
	}
	
	
	public boolean getIncloureEst() {
		return incloureEst;
	}

	public void setIncloureEst(boolean par) {
		incloureEst = par;
	}


	public Assignatura getAssignatura(){
		return assignatura;
	}
	
	/*-------------------------------------------------------------------------
	 * Conjunt de metodes que intervenen en la ordenacio de la llista
	 * 
	 * 
	 * ------------------------------------------------------------------------
	 */

	/*
	 * Aquest metode controla l'ordre de les llistes. Modifica les variables que
	 * serveix per configurar l'ordre le la llista.
	 */

	private void mouOrdre(int cmp) {
		if (camp != cmp) {
			ordena = 0;
			camp = cmp;
		} else {
			if (ordena == 0) {
				ordena = 1;
			} else {
				ordena = 0;
			}
		}

		// Creem un comparador que ens permetra ordenar la llista
		ComparaNom cp = new ComparaNom();

		// Passem els parametres d'ordenacio al comparador
		cp.setOrdre(ordena);
		cp.setCamp(camp);

		Collections.sort(llistaAssist, cp);
	}

	/*
	 * Retorna el nom del gif a pintar per senyalar l'ordre
	 */

	private String getStrOrd(int num) {
		if (camp == num) {
			if (ordena == 0) {
				return "amunt.gif";
			} else {
				return "avall.gif";
			}
		} else {
			return "nogif.gif";
		}
	}

	public String getStrOrd1() {
		return getStrOrd(1);
	}

	public String getStrOrd2() {
		return getStrOrd(2);
	}

	public String getStrOrd3() {
		return getStrOrd(3);
	}

	public String getStrOrd4() {
		return getStrOrd(4);
	}

	public String ordenaCol1() {
		mouOrdre(1);

		return "main";
	}

	public String ordenaCol2() {
		mouOrdre(2);

		return "main";
	}

	public String ordenaCol3() {
		mouOrdre(3);

		return "main";
	}

	public String ordenaCol4() {
		mouOrdre(4);

		return "main";
	}

	public String torna() {
		return "main";
	}

	/*-------------------------------------------------------------------------
	 * Conjunt de metodes que intervenen en la navegacio per pagines de la 
	 * llista
	 * 
	 * ------------------------------------------------------------------------
	 */

	/*
	 * Metode per recalcular el nombre de numero de linies per pagina lligat al
	 * combo box dels main.jsp
	 */

	public void canvia(ValueChangeEvent event) {
		try {
			numPerPag = Integer.parseInt((String) event.getNewValue());
			numPag = 1;
		} catch (Exception ex) {
			numPerPag = 100;
		}
	}

	/* Anem a la seguent pagina */

	public String procSeguent() {
		numPag++;

		return "main";
	}

	/* Anem a la pagina anterior */

	public String procAnterior() {
		numPag--;

		return "main";
	}

	public String fitxaPrincipal() {
		return "main";
	}
	
	public boolean getCanSend(){
		try{
		Placement pl = ToolManager.getCurrentPlacement();
		User currentUser = UserDirectoryService.getCurrentUser();
		String cursiteId= pl.getContext();
		
		 return authzGroupService.isAllowed(currentUser.getId(), PERMIS_ENVIA, "/site/"+ cursiteId);
		 
		} catch (Exception ex){
			 return false;
		 }
	}
	
	/*public String enviaSMS(){
		System.out.println ("Envio sms als membres de " + assignatura.getId()  + " amb el text: " + smsText);
		
		String [] destinataris  = {"23342342","432452342"};
		
		try {
			//Obtenim la llista d'usuaris de l'assignatura
			Set <String> usuaris = (Set<String>) getAssignatura().getLlistaUsuaris();
			String llista_mobils="";
			String llista_correus="";
			String from= "noreply@cv.udl.cat";
			String mobil = null;
			for (String userid: usuaris){
				
				User cUser=	UserDirectoryService.getUser(userid);
				//Obtenim les dades personals per a enviar el correu
				
				DadesPersonals prov_dades = directoryService.obtenirDades(cUser.getEid());
				if (prov_dades!=null){
					mobil = prov_dades.getMobile();
				}
				
				System.out.println ("El mobil és" + mobil);
				
				EmailService.send(from,cUser.getEmail(),"Notificació UdL",smsText,null,null,null);

				if (mobil!= null && !mobil.equals("")){
					if (llista_mobils.equals("")){
						llista_mobils = mobil;
					}else{
						llista_mobils = llista_mobils + ";"  + mobil;	
					}
				}
			}
			
				destinataris = llista_mobils.split(";");
				
		String resposta = SMSService.sendSMS (destinataris,smsText);
		
		System.out.println ("S'ha enviat tot " + resposta);
		
		}catch (Exception ex){
			ex.printStackTrace();
			return "smserror";
		}
		
		return "smscorrecte";
	}
	
	public String tornaSMS(){
		return "smsCerca";
	}*/

	public String goLlistaAssignatures(){
		return "llistaAssignatures";
	}

	public String goAssignatura (){
		return "sendSms";
	}
	
	public String fesCerca() {
		CreaLlistaUsuaris();

		return "main";
	}
	public String onActiva(){
		return "activa";
	}

	
	
	/*-------------------------------------------------------------------------
	 * Conjunt de metodes que intervenen en la creacio de les llistes d'usuaris
	 * ------------------------------------------------------------------------
	 */

	public void CreaLlistaUsuaris() {
		numPag = 1;

		// Obtenim el llistat d'usuaris
		List users = getUsers();

		// Creem la llista on ficarem els usuaris
		llistaAssist = new ArrayList();

		// Creem un comparador que ens permetra ordenar la llista
		ComparaNom cp = new ComparaNom();

		// Passem els parametres d'ordenacio al comparador
		cp.setOrdre(ordena);
		cp.setCamp(camp);

		// Creem l'iterador de la llista
		Iterator it1 = users.iterator();

		while (it1.hasNext()) {
			// Obtenim un usuari de la llista
			DadesPersonals user = (DadesPersonals) it1.next();

			// Creem l'assitent
			Assistent assistent = new Assistent();


			if (user!=null) {
				// carreguem al assistent l'informacio que volem mostrar
				assistent.setNom(user.getNom());
				assistent.setCognoms(user.getCognoms());
				assistent.setIdentificador(user.getUid());
				assistent.setEmail(user.getCorreuprincipal());
				assistent.setTlf(user.getTlf());
				assistent.setFax(user.getFax());
				assistent.setCentre(user.getEmployeeType());
				assistent.setRoomNumber(user.getUbicacio());
				assistent.setWebpersonal(user.getWebpersonal());
				assistent.setMissatgeria(user.getMissatgeria());
				// afegin l'assistent a la llista
				llistaAssist.add(assistent);
			}

			// Ordenem la llista amb els criteris del comparador
			Collections.sort(llistaAssist, cp);
		}

		// calculem el nombre de pagines
		numPagTotal = llistaAssist.size() / numPerPag;

		// Afegin una ultima pagina si conve
		if ((llistaAssist.size() % numPerPag) > 0) {
			numPagTotal++; // Li afegim la pagina de restos
		}
	}

	
	public List getLlistaAssignatures(){
		List llistaAssignatures = new ArrayList ();
		String userid ="";
		try{
			userid = UserDirectoryService.getUserId(assistent.getIdentificador());
		}catch (Exception ex){
			return llistaAssignatures; 
		}
		
		
		Set authzGroupIds = authzGroupService.getAuthzGroupsIsAllowed(userid, "site.upd", null); // (1)
		Iterator it = authzGroupIds.iterator(); // (2)
		while (it.hasNext()) {
		   String authzGroupId = (String) it.next();
		   Reference ref = EntityManager.newReference(authzGroupId); // (3)
		   if(ref.isKnownType()) {
		      if(ref.getType().equals(SiteService.APPLICATION_ID)) { // (4)
		         // do something since this is a site
		         String siteId = ref.getId();
		         String context = ref.getId();
		         boolean insert = false;
		         
		         for (String acabat: acabats){
		        	 if (siteId.endsWith(acabat.trim())){
		        		 insert=true;
		        	 }
		         }
		         if (insert){
			         try {
			            Site site = SiteService.getSite(siteId);
			            llistaAssignatures.add (new Assignatura (site)); 
			         } catch (Exception e) {
			            // invalid site Id returned
			            throw new RuntimeException("Could not get site from siteId:" + siteId);
			         	}
		         }
			   }
		   }
		}
		return llistaAssignatures;
	}
	
	/*
	 * Aquest metode obte la llista d'usuaris del curs al que estem
	 */

	
	private List getUsers() {
		List users = null;
		String filtre = "";

		try {
			if (cerca.length() < 3) {
				cerca = "";
			}
			users = directoryService.getLlistaUsuaris(filtraString(cerca),incloureEst);
		} catch (Exception e) {
			System.out.println("Fallo en users");
		}

		return users;
	}

	private String filtraString(String in) {

		String ret = new String("");

		ret = in.trim();
		ret = ret.replaceAll("'", "");
		ret = ret.replaceAll("[Ññ]", "n");
		ret = ret.replaceAll("[¥]", "n");
		ret = ret.replaceAll("[áàÁÀäÄâÂã]", "a");
		ret = ret.replaceAll("[éèÉÈëËêÊ]", "e");
		ret = ret.replaceAll("[íìÍÌïÏîÎ]", "i");
		ret = ret.replaceAll("[óòÓÒöÖôÔ]", "o");
		ret = ret.replaceAll("[úùÚÙüÜûÛ]", "u");
		ret = ret.replaceAll("[çÇ]", "c");
		ret = ret.replaceAll("[ý]", "y");
		ret = ret.replaceAll("[ºª]", ".");

		return ret;

	}

	/**
	 * 
	 * @author alex - ASIC
	 * 
	 * Aquesta classe representa l'objecte que conforma la linia de la llista
	 * d'usuaris. Recull mes parametres de lo normal per poder ampliar la classe
	 * facilment
	 */

	public class Assignatura {
			public Site currentSite = null; 
				
			public Assignatura (Site site){
				this.currentSite = site;
			}
			
			public String getId (){
				return currentSite.getId();
			}
			
			public String getTitle(){
				return currentSite.getTitle();
			}
		
			public Set getLlistaUsuaris (){
				Set llistaUsuaris = null;
				try{
					AuthzGroup azg = authzGroupService.getAuthzGroup("/site/"+currentSite.getId());
					llistaUsuaris = azg.getUsers();
				}catch (Exception ex){
					ex.printStackTrace();
				}

				return llistaUsuaris;
			}
			
			public String processaAssignatura (){
				assignatura = this;
				return goAssignatura ();
			}
			
	}
	
	public class Assistent {
		private String identificador = "";

		private String nom = "";

		private String cognoms = "";

		private String email = "";

		private String tlf = "";
		
		private String mobile = "";

		private String fax = "";
		
		private String local = "";

		private String prov = "";

		private String posadd = "";

		private String codpos = "";

		private String roomNumber = "";

		private String centre = "";
		
		private String webpersonal ="";
		
		private String missatgeria ="";
		

		/* Setters */
		public void setNom(String par) {
			nom = par;
		}

		public void setCognoms(String par) {
			cognoms = par;
		}

		public void setIdentificador(String par) {
			identificador = par;
		}

		public void setEmail(String par) {
			email = par;
		}

		public void setTlf(String par) {
			tlf = par;
		}
		
		public void setMobile(String par) {
			mobile = par;
		}

		public void setFax(String par) {
			fax = par;
		}

		public void setLocal(String par) {
			local = par;
		}

		public void setProv(String par) {
			prov = par;
		}

		public void setPosAdd(String par) {
			posadd = par;
		}

		public void setCodpos(String par) {
			codpos = par;
		}

		public void setRoomNumber(String par) {
			roomNumber = par;
		}

		public void setCentre(String par) {
			centre = par;
		}
		public void setWebpersonal(String par){
			webpersonal = par;
		}
		
		public void setMissatgeria(String par){
			missatgeria = par;
		}

		/* Getters */
		public String getNom() {
			return nom;
		}

		public String getCognoms() {
			return cognoms;
		}

		public String getIdentificador() {
			return identificador;
		}

		public String getEmail() {
			return email;
		}

		public String getProv() {
			return prov;
		}

		public String getLocal() {
			return local;
		}

		public String getPosadd() {
			return posadd;
		}

		public String getCodpos() {
			return codpos;
		}

		public String getTlf() {
			return tlf;
		}
		
		public String getMobile() {
			return mobile;
		}
		
		public String getFax() {
			return fax;
		}

		public String getRoomNumber() {
			return roomNumber;
		}

		public String getCentre() {
			return centre;
		}

		public String getWebpersonal() {
			return webpersonal;
		}
		
		public String getMissatgeria() {
			return missatgeria;
		}
		
		/*
		 * Aquest metode s'executa quan polses sobre un dels assistents de la
		 * llista Ens renvia a la fitxa amb la informacio de l'usuari
		 */

		public String processaUsuari() {
			// Passem al assistent de la classe FitxesTool l'estructura propia
			assistent = this;

			// Executem el metode fitxaPrincipal de la classe FitxesTool
			fitxaPrincipal();

			// Obtenim la sessio i li passem l'identificador de l'usuari
			// per a poder obtenir posteriorment (mitjançant un servlet)
			// la foto.
			Session session = SessionManager.getCurrentSession();
			session.setAttribute("ident", identificador);
			if (centre.equals("PDIPAS"))
				return "infopas";
			else
				return "info";
		}
		
		
		public String processaAssignatures() {
				assistent = this;
				return goLlistaAssignatures();
		}
		
		
	}

	public String goCerca() {
		page=1;
		return "main";
	}

	public String goCanviDades() {
		page=2;
		return "canvidades";
	}
	
	public String goCanviDadesAlu() {
		page=2;
		return "canvidadesalu";
	}

	public String goCanviPasswd() {
		page=3;
		return "canvipw";
	}

	public String goMobil() {
		page=4;
		return "canvimobil";
	}

	/**
	 * 
	 * @author alex - ASIC
	 * 
	 * Classe que ens ajudara a ordenar les llistes d'usuaris.
	 * 
	 */

	public class ComparaNom implements Comparator {
		private int ordre;

		private int camp;

		public void setOrdre(int ord) {
			ordre = ord;
		}

		public void setCamp(int cmp) {
			camp = cmp;
		}

		public int compare(Object obj1, Object obj2) {
			String str1 = "";
			String str2 = "";

			switch (camp) {
			case 1:
				str1 = ((Assistent) obj1).getIdentificador();
				str2 = ((Assistent) obj2).getIdentificador();

				break;

			case 2:
				str1 = ((Assistent) obj1).getNom();
				str2 = ((Assistent) obj2).getNom();

				break;

			case 3:
				str1 = ((Assistent) obj1).getCognoms();
				str2 = ((Assistent) obj2).getCognoms();

				break;

			case 4:
				str1 = ((Assistent) obj1).getEmail();
				str2 = ((Assistent) obj2).getEmail();

				break;
			}

			if (ordena == 0) {
				return str1.compareTo(str2);
			} else {
				return str2.compareTo(str1);
			}
		}
	}

}
