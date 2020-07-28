//********************************************************************************
// Super classe HDesk.java
//********************************************************************************

/**
 * TODO:Aillar tots els metodes que nomes siguin d'ordenacio, cerca i paginacio
**/

package es.udl.asic.sakaiproject.tool.einahelpdesk;
import javax.faces.context.*;
import javax.faces.model.SelectItem;
import java.util.*;

import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Assistencia;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Campus;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Tecnics;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Categoria;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Edifici;

import es.udl.asic.sakaiproject.tool.einahelpdesk.hibernate.HibernateBD;
import es.udl.asic.sakaiproject.service.einahelpdesk.HDeskService;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.event.ActionEvent;

import java.util.List;

//per autenticar
//import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
//sakai22
import org.sakaiproject.user.cover.UserDirectoryService;
//import org.sakaiproject.service.framework.session.cover.UsageSessionService;
//sakai22
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryProvider;
import org.sakaiproject.user.api.UserEdit;

//configuracio eina
//import org.sakaiproject.api.kernel.tool.Tool;
//import org.sakaiproject.api.kernel.tool.Placement;
//import org.sakaiproject.api.kernel.tool.cover.ToolManager;
//sakai22
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;

import java.util.Comparator;

import java.text.SimpleDateFormat;

//per enviar el email
//import org.sakaiproject.service.framework.email.cover.EmailService;
//sakai22
//import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.email.cover.EmailService;

//Necessari per OpenLdap al metode doAutentificar()
import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

//Necessari per passar a .pdf (metode ImprimeixAssistencies())
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.api.Site;
import org.w3c.dom.*;
import org.apache.xerces.dom.DocumentImpl;

public class HDesk{
private Properties prop;
		String dialect;
		String url;
		String driver;
		String username;
		String password;
		String supportAgent;
		String perfil;
		String anuncisServeiInformatica;
		String urlfaq;
		
		ArrayList responsables=new ArrayList();
		ArrayList operadors=new ArrayList();
		ArrayList categories=new ArrayList();
		ArrayList assistencies=new ArrayList();
		ArrayList columna_cerca=new ArrayList();

		public HDeskService hdeskservice;
		public HibernateBD hibernatebd;

		//els ids de seleccio de desplegables
		String  campus_sel;

		//per fer la cerca d'informació
		String columna_sel=""; 
		String paraula_clau;


		//per ordenar per columnes a la consulta d'assistencies
		String ordenacio="";
		String amuntavallres="";//quina imatge fiquem
		String asc_desc="";//en quin ordre
		int sentit = 1; //amunt o avall
		int columna_ordre = 1; //Columna d'ordenacio

		//per fer la paginacio
		String reg_per_pag="20";
		int pagina=1;
		int totalPag=1;
		boolean noTePrevia=true;
		boolean noTeSeg=true;

		//ticket 
		int ticket_sel=0;
		Assistencia assistencia=new Assistencia();

		//comprova si consultem historic o actives/resoltes
		int isHistoric; //0 no ho es, 2 sí.

		//per canviar prioritat i estat
		String prioritat_sel;
		String estat_sel;
		String seguiment;
		String seguiment_intern;
		String tecnic_sel;

		//Per fer les noves assistencies des de qualsevol perfil
		ArrayList llistaCampus=new ArrayList();

		ArrayList llistaEdificis=new ArrayList();
		String edifici_selec;

		ArrayList llistaCategoria=new ArrayList();
		String categoria_selec; //=1;

		ArrayList llistaCategoria_tipus=new ArrayList();
		String categoria_selec_tipus; //=1;

		String telefon;
		String despatx;
		String consulta;
		String solucio;
		String codi_udl="";
		String categoria;

		String nom;

		boolean envia_correu=false; //si és 0 no envia un correu d'avís, si és 1 sí
		boolean envia_correu_usuari=false; //si és 0 no envia un correu d'avís a l'usuari de l'assistència, si és 1 si

		//Sakai22

		String userid;
		User usr=null;

		/*protected void finalize() throws Throwable{
		  hdeskservice.shutdown();
		  super.finalize();
		  }*/

		int estatConsulta;
		
		int activat;
		//**********************************************************************		
		public void setTecnic_sel(String tecnic_sel){
			this.tecnic_sel=tecnic_sel;
		}

		public String getTecnic_sel(){
			return tecnic_sel;
		}

		//**********************************************************************	
		public void setHdeskservice(HDeskService h) {
			//System.out.println("Iniciant el component a hibernateBD..."+h);
			this.hdeskservice = h; 

			hibernatebd=new HibernateBD(hdeskservice);
			perfil = doAutentificar();
			//hibernatebd.sendToHistoric(new Date()); //passem les resoltes a historic passats 5 dies de la resolucio
		}


		public HDeskService getHdeskservice(){
			return hdeskservice;
		}

		//**********************************************************************
		public void setEnvia_correu(boolean envia_correu){
			//per saber si es vol enviar un correu a l'operador o responsable quan se li reassigna
			this.envia_correu=envia_correu;
		}

		public boolean getEnvia_correu(){
			return envia_correu;
		}

		//**********************************************************************
		public void setEnvia_correu_usuari(boolean envia_correu_usuari){
			//per saber si es vol enviar un correu a l'usuari que ha generat l'assistencia
			this.envia_correu_usuari=envia_correu_usuari;
		}

		public boolean getEnvia_correu_usuari(){
			return envia_correu_usuari;
		}

		//**********************************************************************
		//Canviar a dins assistencia		
		public void setSeguiment(String seguiment){
			//super.setSeguiment(seguiment);
			this.seguiment=seguiment;
		}

		public String getSeguiment(){
			return seguiment;
		}

		public void setSeguiment_intern(String seguiment_intern){
                        this.seguiment_intern=seguiment_intern;
                }

                public String getSeguiment_intern(){
                        return seguiment_intern;
                }

		public void setPrioritat_sel(String prior){
			this.prioritat_sel=prior;
		}

		public String getPrioritat_sel(){

			return prioritat_sel;
		}

		public void setEstat_sel(String estat){
			//super.setEstat_sel(estat);
			this.estat_sel=estat;
		}

		public String getEstat_sel(){
			return estat_sel;
		}
		//fi canviar a dins assistencia 


		//Simplement comprova que estem consultant una llista d'historics o d'actius/resolts 		
		public int getIsHistoric(){
			return isHistoric;
		}

		public void setIsHistoric(int historic){
			this.isHistoric=historic;
		}
		//fi comprovacio

		//Captura el ticket a la llista
		public void processActionViewTicket(ActionEvent evt){
			FacesContext ctx = FacesContext.getCurrentInstance();
			String ticket_selStr = (String) ctx.getExternalContext().getRequestParameterMap().get("ticket");
			ticket_sel = Integer.parseInt(ticket_selStr);
		}

		//Per capturar el ticket
		public String getTicket_sel() {
			String ticket= ""+ this.ticket_sel;
			return ticket;
		}

		//Per capturar una assistencia concreta
		public Assistencia getAssistencia(){
			return assistencia;
		}

		public void setAssistencia(Assistencia assistencia){
			this.assistencia=assistencia;
		}

		//Metodes de paginacio de la consulta d'assistencies		
		public int getPagina(){
			return pagina;	
		}

		public int getTotalPag(){
			return totalPag;	
		}

		public boolean getNoTePrevia(){
			return noTePrevia;
		}

		public boolean getNoTeSeg(){
			return noTeSeg;
		}

		public void setNoTePrevia(boolean note){
			this.noTePrevia=note;
		}

		public void setNoTeSeg(boolean note){
			this.noTeSeg=note;
		}

		public void setReg_per_pag(String reg){
			this.reg_per_pag=reg;
		}

		public String getReg_per_pag(){
			return reg_per_pag;	
		}

		//**********************************************************************
		public void setReg_per_pag(ValueChangeEvent event) {
			//Per capturar el moviment del combobox que calcula els registres per pagina
			FacesContext ctx = FacesContext.getCurrentInstance();
			Map map = ctx.getExternalContext().getRequestMap();
			FacesMessage msg = new FacesMessage(event.getNewValue().toString());
			ctx.addMessage(null,msg);

			setReg_per_pag((String) event.getNewValue());//Integer.parseInt((String) event.getNewValue()));
			pagina=1;

			//Per guanyar rapidesa en l'historic
			int hist=getIsHistoric();
			if (hist==2){   // estem a l'historic
				CercaConsultaAnterior();	
			}
			getAssistencies();
		}

		//**********************************************************************
		public String processActionPrimeraPag(){
			pagina=1;
			setNoTePrevia(true);
			setNoTeSeg(false);

			//Per guanyar rapidesa en l'historic
			int hist=getIsHistoric();
			if (hist==2){   // estem a l'historic
				CercaConsultaAnterior();
			}
			return "cons_assist_admin";
		}

		//**********************************************************************
		public String processActionPagAnt(){
			pagina--;
			if (pagina==1) setNoTePrevia(true);
			setNoTeSeg(false);

			//Per guanyar rapidesa en l'historic
			int hist=getIsHistoric();
			if (hist==2){   // estem a l'historic
				CercaConsultaAnterior();	
			}
			return "cons_assist_admin";
		}

		//**********************************************************************
		public String processActionPagSeg(){
			pagina++;
			if (pagina==totalPag) setNoTeSeg(true);
			setNoTePrevia(false);

			//Per guanyar rapidesa en l'historic
			int hist=getIsHistoric();
			if (hist==2){   // estem a l'historic
				CercaConsultaAnterior();
			}
			return "cons_assist_admin";
		}

		//**********************************************************************
		public String processActionUltimaPag(){
			pagina=totalPag;
			setNoTeSeg(true);
			setNoTePrevia(false);

			//Per guanyar rapidesa en l'historic
			int hist=getIsHistoric();
			if (hist==2){   // estem a l'historic
				CercaConsultaAnterior();
			}
			return "cons_assist_admin";
		}

		//**********************************************************************
		public void ajustarPagines(){	
			int quantsPerPag=Integer.parseInt(reg_per_pag);
			//Per guanyar rapidesa en l'historic
			int hist=getIsHistoric();	

			if (hist==0 || hibernatebd.getNum_assistencies()==-1){	//no estem a l'historic o be, estem a l'historic i cerquem pel listbox
				//calculem el nombre de pagines 
				totalPag = assistencies.size() / quantsPerPag;

				//Afegin una ultima pagina si conve
				if ((assistencies.size() % quantsPerPag) > 0) totalPag++;
			}
			else{							//si estem a l'historic
				//calculem el nombre de pagines
				int num= hibernatebd.getNum_assistencies();
				totalPag = num / quantsPerPag;

				//Afegin una ultima pagina si conve
				if ((num % quantsPerPag) > 0) totalPag++;
			}	

			if(totalPag==0)totalPag=1; //per evitar que la pagina no existeixi si la cerca no retorna resultats 
	
			if (pagina==totalPag) setNoTeSeg(true);
			else setNoTeSeg(false);

			if (pagina==1) setNoTePrevia(true);
			else setNoTePrevia(false);
    
		}
//fi metodes paginacio

//**********************************************************************
public int getEstatConsulta(){
        //Per guanyar rapidesa en l'historic
        return estatConsulta;
}

//**********************************************************************
public void setEstatConsulta(int estatConsulta){
        //Per guanyar rapidesa en l'historic
        this.estatConsulta=estatConsulta;
}

public int getActivat(){
    return activat;
}

//**********************************************************************
public void setActivat(int activat){
    //Per guanyar rapidesa en l'historic
    this.activat=activat;
}

public String getUrlFaq(){
	return urlfaq;
}

public void setUrlFaq(String urlfaq){
	this.urlfaq = urlfaq;
}


//**********************************************************************
public void CercaConsultaAnterior(){
        //Metode per saber quina consulta havia fet abans i poder-la repetir, ES PER A L'HISTORIC, PER GUANYAR RAPIDESA
        int quantsPerPag=Integer.parseInt(reg_per_pag);

        if(getEstatConsulta()==1){              //ordenacio inicial
                setAssistencies(hibernatebd.getAssistencies(2, quantsPerPag, pagina));
        }
        else if(getEstatConsulta()==2){         //ordenacio per la columna Ticket
                setAssistencies(hibernatebd.getAssistenciesHistoric(columna_sel,paraula_clau,isHistoric, "assis.ticket",asc_desc,quantsPerPag,pagina));
        }
        else if(getEstatConsulta()==3){         //ordenacio per la columna Data
                setAssistencies(hibernatebd.getAssistenciesHistoric(columna_sel,paraula_clau,isHistoric,"assis.data_inici",asc_desc,quantsPerPag,pagina));
        }
        else if(getEstatConsulta()==4){         //ordenacio per nom d'usuari, no cal fer res, ja que, el nom no esta a la BD i no puc paginar
        }
        else if(getEstatConsulta()==5){         //ordenacio per la columna Campus
                setAssistencies(hibernatebd.getAssistenciesHistoric(columna_sel,paraula_clau,isHistoric, "campus.nom",asc_desc,quantsPerPag,pagina));
        }
        else if(getEstatConsulta()==6){         //ordenacio per la columna Edifici
                setAssistencies(hibernatebd.getAssistenciesHistoric(columna_sel,paraula_clau,isHistoric, "edifici.nom_edifici",asc_desc,quantsPerPag,pagina));
        }
        else if(getEstatConsulta()==7){         //ordenacio per 'assignada a'
                setAssistencies(hibernatebd.getAssistenciesHistoric(columna_sel,paraula_clau,isHistoric, "tecnic.nom",asc_desc,quantsPerPag,pagina));
        }
        else if(getEstatConsulta()==8){         //ordenacio per tipus d'assistencia
                setAssistencies(hibernatebd.getAssistenciesHistoric(columna_sel,paraula_clau,isHistoric, "categoria.tipus",asc_desc,quantsPerPag,pagina));
        }
        else if(getEstatConsulta()==9){         //ordenacio per prioritat
                setAssistencies(hibernatebd.getAssistenciesHistoric(columna_sel,paraula_clau,isHistoric, "assis.prioritat",asc_desc,quantsPerPag,pagina));
        }
        else if(getEstatConsulta()==10){        //ordenacio per listbox perfil OPERADOR
                setAssistencies(hibernatebd.getAssistenciesOperador("",columna_sel, paraula_clau, isHistoric));
        }
        else if(getEstatConsulta()==11){        //ordenacio per listbox perfil RESPONSABLE
                setAssistencies(hibernatebd.getAssistenciesRespo("", 0, columna_sel, paraula_clau, isHistoric));
        }
        else if(getEstatConsulta()==12){        //ordenacio per listbox perfil ADMINISTRADOR
                setAssistencies(hibernatebd.getAssistencies(columna_sel, paraula_clau, isHistoric));
        }
}

//Metodes per fer la cerca a la consulta d'assistencies
//**********************************************************************
public void setColumna_sel(String columna){
	this.columna_sel=columna;
}

//**********************************************************************
public String getColumna_sel(){
	return columna_sel;
}

//**********************************************************************
//emplena el combobox per mirar per quins camps podem fer la cerca
public void setColumna_cerca(String opcio)
{
	ArrayList columna_cerca=new ArrayList();
	columna_cerca.add(new SelectItem((String) "ticket",(String) "Identificador"));
	if (opcio.equals("sense_usuari")); //estem mirant les d'un usuari concret
	else columna_cerca.add(new SelectItem((String) "usuari",(String) "Introduïda per"));
	columna_cerca.add(new SelectItem((String) "data_inici",(String) "Data"));
	if (opcio.equals("sense_campus"));//discriminem per campus
	else columna_cerca.add(new SelectItem((String) "id_campus",(String) "Campus"));
	columna_cerca.add(new SelectItem((String) "id_edifici",(String) "Edifici"));
	if (opcio.equals("sense_operador"));//discriminem per operador
	else columna_cerca.add(new SelectItem((String) "id_tecnic",(String) "Assignada a"));
	columna_cerca.add(new SelectItem((String) "id_categoria",(String) "Tipus"));
	columna_cerca.add(new SelectItem((String) "prioritat",(String) "Prioritat"));
	if (opcio.equals("sense_estat")); //estem mirant nomes historic
	else columna_cerca.add(new SelectItem((String) "estat",(String) "Estat"));

	//per a fer la busqueda per descripcio i seguiment
	if (opcio.equals("sense_usuari"))	
		columna_cerca.add(new SelectItem((String) "descripcio_seguiment", (String) "Descripció / Seguiment"));	
	else
		columna_cerca.add(new SelectItem((String) "descripcio_seguiment_intern", (String) "Descripció / Seguiment & intern"));
	
	this.columna_cerca=columna_cerca;
	}

//**********************************************************************
public ArrayList getColumna_cerca(){
	return columna_cerca;
}

//**********************************************************************
public void setParaula_clau(String paraula_clau){
	this.paraula_clau=paraula_clau;	
}

//**********************************************************************
public String getParaula_clau(){
	return paraula_clau;
}
//fi metodes cerca


//metodes per fer la autenticacio
//**********************************************************************
public void setPerfil(String perfil){
	this.perfil=perfil;
}

public String getPerfil(){
	return perfil;
}

//sakai22****************************************
public void setUserid() {
	//per la 2.2
	//Session s = SessionManager.getCurrentSession();
	//User usr=null;
	UsageSession uses = UsageSessionService.getSession();
	String ident = uses.getUserId();
	try {
		usr = UserDirectoryService.getUser(ident);
    }
	catch (Exception ex){System.out.println("No s'ha pogut obtenir l'usuari");}

	this.userid=usr.getEid();
	//this.userid =s.getUserId();// UsageSessionService.getSessionUserId();
}

public String getUserid() {
	return userid;
}
//fi sakai22*************************************
//***********************************************

//**********************************************************************
public String doAutentificar()
{
	String perfil="";
	int i=0;
	String correu = UserDirectoryService.getCurrentUser().getEmail();
	//String userid = UsageSessionService.getSessionUserId();
	nom = UserDirectoryService.getCurrentUser().getDisplayName();
	
	//sakai22
	String userid=getUserid();
		
	hibernatebd.setPropietats(url, driver, username, password, dialect);
	
	String identificador="";
        String base="ou=People, dc=udl, dc=es"; //busco a l'arbre es->udl->People PAS/PDI
        boolean returnVal = true;
        Hashtable env = new Hashtable();
        try{
                env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL,"ldap://dir3.udl.net:389");
		
                DirContext ctx = new InitialDirContext(env);

                //Per descendir en l'estructura dels subarbres durant les cerques
		SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                //retorna el uid
                searchControls.setReturningAttributes(new String[] {"uid"});
		
                //executa la cerca dins de l'arbre base
                String searchFilter = "(&(objectclass=posixAccount)(uid="+userid+"))";
                NamingEnumeration results = ctx.search(base, searchFilter, searchControls);
		 	
		if (results.hasMore()==false)		 identificador="alumne"; //no hi ha cap element a la branca buscada, es un alumne
			
		else {								 //hi ha elements, es del personal
			while(results.hasMore() && i<1){
				SearchResult result = (SearchResult) results.next();
				if (userid.equals(supportAgent)) identificador="support";  //es l'administrador
				else{
					//comprovar si operador o responsable
                        		perfil=hibernatebd.checkUser(userid);
                        		if (perfil!="") 	identificador=perfil; 
                    			else 			identificador="pas_pdi"; 
				}
				i++;
			}
			if (results.hasMore()==true)  System.out.println("ERROR EN LDAP! DUES ENTRADES IGUALS, i= "+i);			
		}
		results.close();
                ctx.close();
	}
	catch(Exception ex){		//en cas de que falli la cerca
                ex.printStackTrace();
        }
	return identificador;
}
//**********************************************************************
//fi metodes per autenticar

//**********************************************************************
//Metode de cerca


public String processActionCercar()
{
	//Metode cridat des del perfil Administrador per a la cerca del listbox
	pagina=1; //per a que retorni a la primera si ens trovessim a una altra
	setAssistencies(hibernatebd.getAssistencies(columna_sel,paraula_clau,isHistoric));  
	setEstatConsulta(12); //Per guanyar rapidesa en l'historic, aixi es sap la consulta anterior per a tornar-la a fer 

	return "";// que retorni dins de cada servlet de perfil cons_assist_admin";
}

//**********************************************************************
public void setAssistencies(ArrayList assistencies){
	this.assistencies=assistencies;
	ajustarPagines();
}

//**********************************************************************
public ArrayList getAssistencies(){
    ArrayList mostrables= new ArrayList();
    //el primer index ens conte el numero de registres, el segon el primer index dins del array que utilitzarem
    int index=0, index_bucle; 
    int quantsPerPag=Integer.parseInt(reg_per_pag);
	
    ajustarPagines();
   
    if (pagina > 1) index_bucle=((pagina-1)*quantsPerPag);
    else index_bucle=0;

    //Per guanyar rapidesa en l'historic
    int hist=getIsHistoric();

    if (hist==0  || hibernatebd.getNum_assistencies()==-1);  	//no estem a l'historic, o be, estem a l'historic i cerquem pel listbox
    else	index_bucle=0;					//estem a l'historic, hem de posar l'index a zero per agafar be les assistencies	

    //setAssistencies(hibernatebd.Assistencies(2, quantsPerPag, pagina));	
    while (index < quantsPerPag && index_bucle < assistencies.size()){
		mostrables.add((Assistencia) assistencies.get(index_bucle));
		index ++;
		index_bucle++;
    }
    
    //retorna les assistencies que volem mostrar;
    return mostrables;
}

//**********************************************************************
//Per agafar el link de la pagina d'anuncis del servei d'informatica (al perfil PAS-PDI)
public void setAnuncisServeiInformatica(String anuncisServeiInformatica){
        this.anuncisServeiInformatica=anuncisServeiInformatica;
}

public String getAnuncisServeiInformatica() {
        return anuncisServeiInformatica;
}

//**********************************************************************
public HDesk(){
	Tool tool = ToolManager.getCurrentTool();
    	Placement pla = ToolManager.getCurrentPlacement();
    	prop=new Properties();
    	prop=pla.getConfig();
    
    
	dialect = pla.getConfig().getProperty("hibernate.dialect");
	url = pla.getConfig().getProperty("hibernate.connection.url");
	driver = pla.getConfig().getProperty("hibernate.connection.driver_class");
	username = pla.getConfig().getProperty("hibernate.connection.username");
	password = pla.getConfig().getProperty("hibernate.connection.password");
	supportAgent=pla.getConfig().getProperty("support.agent");
	anuncisServeiInformatica=pla.getConfig().getProperty("anuncis.servei.informatica");	
	urlfaq= pla.getConfig().getProperty("url.faq");
	//hibernatebd=new HibernateBD(hdeskservice);
	setUserid();
	//perfil = doAutentificar();
		
	//hibernatebd.sendToHistoric(new Date()); //passem les resoltes a historic passats 10 dies de la resolucio
}

//**********************************************************************
public void setOperadorsActius(Vector operadors){
	int i=0;
	ArrayList resp_array=new ArrayList();
	Tecnics obj_t=new Tecnics();

	while(i < operadors.size())
	{   
		obj_t= (Tecnics) operadors.get(i);
		if (hibernatebd.checkUser(obj_t.getId_tecnic()).equals("responsable"))
			resp_array.add(new SelectItem(obj_t.getId_tecnic(),obj_t.getNom()+" (*)"));
		else 
		resp_array.add(new SelectItem(obj_t.getId_tecnic(),obj_t.getNom()));
		i++;
	}
	this.operadors=resp_array;	
}

public void setOperadors(Vector operadors){
	int i=0;
	ArrayList resp_array=new ArrayList();
	Tecnics obj_t=new Tecnics();
	int actiu = 1;

	while(i < operadors.size())
	{   
		obj_t= (Tecnics) operadors.get(i);
		if (hibernatebd.checkUser(obj_t.getId_tecnic()).equals("responsable"))
			{
				resp_array.add(new SelectItem(obj_t.getId_tecnic(),obj_t.getNom()+" (*)"));
			}
		else 
			{
				if (obj_t.getActivat()<actiu)
					{
						resp_array.add(new SelectItem(obj_t.getId_tecnic(),obj_t.getNom()+" (inactiu)"));
					}
				else
					{
						resp_array.add(new SelectItem(obj_t.getId_tecnic(),obj_t.getNom()));
					}
			}
		i++;
	}
	this.operadors=resp_array;	
}


public ArrayList getOperadors(){
	return operadors; 
}
//**********************************************************************
public String processActionGoPregFrequents(){
        //
        try{
                return "preg_frequents";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//**********************************************************************
public String processActionDesarAssistencia(){
	//metode que desa una assistencia en els perfils OPERADOR/RESPONSABLE/SUPORT
	try{
	Date avui=new Date();
	//	Updatem
	int estat;
	int estat_activa;
	int prioritat;
	String tecnic;
	int tipus_categoria;
	String data_avui;
	String data_fi;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	data_avui=sdf.format(avui);
	
	if (seguiment.equals("")) ;
	else seguiment= "\n"+ data_avui +"\t (" + nom + ") "+ seguiment;
	
	if (seguiment_intern.equals("")) ;
        else seguiment_intern= "\n"+ data_avui +"\t (" + nom + ") "+ seguiment_intern;	
		
	if (estat_sel.equals("")) estat=assistencia.getEstat();	//estat_sel agafa l'estat listbox assistencia en el cas de canviar-lo activa/resolta/historic
	else 			  estat=Integer.parseInt(estat_sel);
	

	//***Si l'assistencia entrada es seva (emesa per = operador/responsable/suport que esta al helpdesk)
	//i escriu alguna cosa S'HA DE PASSAR A ACTIVA
	//***L'unic problema que te aixo, es que si un operador vol canviar l'estat de la seva propia assistencia,
	//no podra fer-ho, ja que, sempre li passara a activa
	estat_activa = assistencia.getEstat_activa();
	String usuari = assistencia.getUsuari();	//agafem el nom de qui ha entrat l'assistencia
	String op_resp_sup = userid;			//agafem el nom del operador/responsable/suport que esta al helpdesk
	
	if(usuari.equals(op_resp_sup)){
		//comprovo si esta resolta l'assistencia, si ho és, s'ha de posar en vermell i negreta
		if(estat==1)	estat_activa=2;		//ho marco per a qui tingui assignada l'assistencia ho vegi en vermell negreta		
		else		estat_activa=1;         //ho marco per a qui tingui assignada l'assistencia ho vegi en vermell
		
		estat=0;		//ho passem a activa
						
	}


	if (prioritat_sel.equals("")) prioritat=assistencia.getPrioritat();
	else prioritat=Integer.parseInt(prioritat_sel);
	
	if (tecnic_sel.equals("")) tecnic=assistencia.getId_tecnic();
	else {tecnic=tecnic_sel; estat_activa=3;} //si hi ha hagut una reassignacio, la marco
	
	if(categoria_selec_tipus==null) categoria_selec_tipus="";
	if (categoria_selec_tipus.equals("")) {tipus_categoria=assistencia.getId_categoria();}
	else tipus_categoria=Integer.parseInt(categoria_selec_tipus);
	
	if (estat_sel.equals("") && prioritat_sel.equals("") && tecnic_sel.equals("") && seguiment.equals("") && seguiment_intern.equals("") && categoria_selec_tipus.equals("")) ;
	else hibernatebd.AlterAssistencia(assistencia.getTicket(), prioritat, estat, estat_activa, tecnic, seguiment, seguiment_intern, tipus_categoria);
	
	if (envia_correu){
		/*S'envia un correu en cas de que es marqui el checkbox*/
		int numero=assistencia.getTicket();
		//sakai 22, el correu es recupera de forma diferent
		//String to=UserDirectoryService.getUser(tecnic).getEmail(); //a qui li envies el mail
		String to="";
		User usr=UserDirectoryService.getUserByEid(tecnic);
                to=usr.getEmail();

        String consulta = assistencia.getConsulta();
        String comentaris_antics = assistencia.getSolucio();
        if (comentaris_antics == null)
        {
        	comentaris_antics = "";
        } 
        String comentaris_interns_antics = assistencia.getSolucio_interna();
        if (comentaris_interns_antics == null)
        {
        	comentaris_interns_antics = "";
        }
        
		String from = "suport@asic.udl.es";   //qui envia el mail 
		String message_subject = "Avís: rebuda una nova assistència";
		if (tecnic.equals("csga")) 
		{
			message_subject = message_subject +
			" ("+numero+")";
		}
		String content = "Benvolgut/da: \n\n" +
		"Tens una nova assistència assignada a l'eina 'Suport Usuari'.\n"+
		"El ticket de l'assistència és "+ numero+".\n\n"+
		"Missatge enviat automàticament.\n\n";
		if (tecnic.equals("csga"))
		{
			content = content +
			consulta+"\n\n"+
			"Comentaris antics: "+comentaris_antics+"\n\n"+
			"Comentaris interns antics: "+comentaris_interns_antics+"\n\n"+
			"Comentaris: "+seguiment+"\n\n"+
			"Comentaris interns: "+seguiment_intern+"\n\n";	
		}
		
		
				
		EmailService.send(from, to, message_subject, content, null, null, null);
		//System.out.println("***** S'HA ENVIAT UN CORREU CORRECTAMENT");	
		envia_correu=false;
	}
	if (envia_correu_usuari){
                /*S'envia un correu a l'usuari en cas de que es marqui el checkbox*/
                int numero=assistencia.getTicket();
		String data=assistencia.getStrData_inici();
                //sakai 22, el correu es recupera de forma diferent
                //String to=UserDirectoryService.getUser(tecnic).getEmail(); //a qui li envies el mail
                String to_usuari="";
		to_usuari=assistencia.getCorreu_usuari();
                
                String from = "suport@asic.udl.es";   //qui envia el mail
                String message_subject = "Avís: canvis en la seva assistència de l'eina 'Suport Usuari'";
		String content = "Benvolgut/da: \n\n" +
                "Hi ha hagut canvis a l'assistència amb número de ticket "+numero+" de data "+data+".\n"+
                "Consulteu l'eina 'Suport Usuari' accessible a través del campus virtual de la UdL per veure aquests canvis:\n"+
		"http://cv.udl.es/portal/site/serveiinformatica/page/bc699d0e-e972-4738-80fb-da2a50bc2987 \n\n"+
                "Missatge enviat automàticament.";
                
                EmailService.send(from, to_usuari, message_subject, content, null, null, null);
                //System.out.println("***** S'HA ENVIAT UN CORREU A L'USUARI CORRECTAMENT");
                envia_correu_usuari=false;
        }
	
	setSeguiment("");//esborrem tot
	setSeguiment_intern("");
	setEstat_sel("");
	setPrioritat_sel("");
	setTecnic_sel("");
	setCategoria_selec_tipus("");

	}catch(Exception e){e.printStackTrace();}
	
	return "";
}

//**********************************************************************
public String RetornaMain(){
        try{
                return "main";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//
//Tots aquests metodes ordenen la cerca per columnes de la consulta d'assistencies	
//
//**********************************************************************
public void setAmuntavallres(String dibuix){
	this.amuntavallres=dibuix;
}

//**********************************************************************
public String getAmuntavallres(int camp){
	if (columna_ordre == 1 || columna_ordre == 2 || columna_ordre==9){
	//per a que surti al reves la icona de ticket i data
		if(columna_ordre==camp){ 
			if (sentit == 1)	return "avall.gif";
                	else                 	return "amunt.gif";
                }
	 	else   				return "res.gif"; 	
	}
	else{
		if (columna_ordre == camp) {
			if (sentit == 0)	return "avall.gif";
			else			return "amunt.gif";
		} 
		else				return "res.gif";
	}
}

//**********************************************************************
public void mouSentit(int sentit_ordre) {
		if (columna_ordre != sentit_ordre) {
			sentit = 0;
			columna_ordre = sentit_ordre;
		} 
		else	sentit=(sentit == 0)?1:0;
}

//**********************************************************************
public String mouSentit1(){	
	//ordenem pel ticket
	mouSentit(1);
	asc_desc=(sentit==1)?"DESC":"ASC";
	ordenacio="assis.ticket";
	//setAssistencies(hibernatebd.Assistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc));//comentat per guanyar rapidesa al suport
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit2(){	
	//ordenem per data d'inici introduïda
	mouSentit(2);
	asc_desc=(sentit==1)?"DESC":"ASC";
	ordenacio="assis.data_inici";
	//setAssistencies(hibernatebd.Assistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc));//comentat per guanyar rapidesa al suport
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit3(){	
	//aqui ordenarem per l'id de l'usuari
	mouSentit(3);
	asc_desc=(sentit==0)?"DESC":"ASC";
	//cas especial d'ordenacio, pq el nom, cognoms de l'usuari no es troba en cap taula
	int index=0;
	ArrayList assistencies= new ArrayList();
	Assistencia assis= new Assistencia();
	ordenacio="assis.usuari";
	assistencies=hibernatebd.getAssistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc);
	
	if (asc_desc.equals("ASC")) Collections.sort(assistencies, new NomComparador());
	else Collections.sort(assistencies, new NomComparadorInv());

	setAssistencies(assistencies);
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit4(){	
	//ordenem pel nom del campus
	mouSentit(4);
	asc_desc=(sentit==0)?"DESC":"ASC";
	ordenacio="campus.nom";
	//setAssistencies(hibernatebd.getAssistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc));//comentat per guanyar rapidesa al suport
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit5(){	
	//ordenem pel nom de l'edifici
	mouSentit(5);
	asc_desc=(sentit==0)?"DESC":"ASC";
	ordenacio="edifici.nom_edifici";
	//setAssistencies(hibernatebd.getAssistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc));//comentat per guanyar rapidesa al suport
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit6(){	
	//ordenem pel nom del tecnic
	mouSentit(6);
	asc_desc=(sentit==0)?"DESC":"ASC";
	ordenacio="tecnic.nom";
	//setAssistencies(hibernatebd.getAssistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc));//comentat per guanyar rapidesa al suport
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit7(){	
	//ordenem per tipus d'assistencia
	mouSentit(7);
	asc_desc=(sentit==0)?"DESC":"ASC";
	ordenacio="categoria.tipus";
	//setAssistencies(hibernatebd.getAssistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc));//comentat per guanyar rapidesa al suport
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit8(){
	//ordenem per prioritat
	mouSentit(8);
	asc_desc=(sentit==0)?"DESC":"ASC";
	ordenacio="assis.prioritat";
	//setAssistencies(hibernatebd.getAssistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc));//comentat per guanyar rapidesa al suport
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit9(){
	//ordenem per estat	
	mouSentit(9);
	asc_desc=(sentit==1)?"DESC":"ASC";
	ordenacio="assis.estat";
	setAssistencies(hibernatebd.getAssistencies(columna_sel,paraula_clau,isHistoric, ordenacio,asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public String getAmuntavallres1(){
	return getAmuntavallres(1);
}

public String getAmuntavallres2(){
	return getAmuntavallres(2);
}

public String getAmuntavallres3(){
	return getAmuntavallres(3);
}

public String getAmuntavallres4(){
	return getAmuntavallres(4);
}

public String getAmuntavallres5(){
	return getAmuntavallres(5);
}

public String getAmuntavallres6(){
	return getAmuntavallres(6);
}

public String getAmuntavallres7(){
	return getAmuntavallres(7);
}

public String getAmuntavallres8(){
	return getAmuntavallres(8);
}

public String getAmuntavallres9(){
	return getAmuntavallres(9);
}

//**********************************************************************
//Classe per fer la ordenacio quan busquem per nom
class NomComparador implements Comparator {
	
	public int compare(Object o1, Object o2) {
	    Assistencia ass1 = (Assistencia) o1;
	    Assistencia ass2 = (Assistencia) o2;
	    return ass1.getNom_usuari().compareTo(ass2.getNom_usuari());
	}

	public boolean equals(Object o) {
	    return this == o;
	}
}

//**********************************************************************
//Classe per fer la ordenacio quan busquem per nom
class NomComparadorInv implements Comparator {
	
	public int compare(Object o1, Object o2) {
	    Assistencia ass1 = (Assistencia) o1;
	    Assistencia ass2 = (Assistencia) o2;
	    return ass2.getNom_usuari().compareTo(ass1.getNom_usuari());
	}
	
	public boolean equals(Object o) {
	    return this == o;
	}
}


//**********************************************************************
//METODES PER FER UNA NOVA ASSISTENCIA DES DE QUALSEVOL PERFIL
//**********************************************************************
public String processActionNovaAssistencia(){
        //Metode que envia a (nova_assist.jsp)
       try{
		
        setLlistaCampus(hibernatebd.getLlistaCampus()); 
		setLlistaEdificis(hibernatebd.getLlistaEdificis(campus_sel)); 
		setLlistaCategoria( hibernatebd.getCategories() );
		setCategoria(hibernatebd.getDescripcio(categoria_selec)); 
				
		return "nova_assist";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//**********************************************************************
public String getNom(){
	return nom;
}

//**********************************************************************
public void setNom(String nom){
	this.nom=nom;
}

//**********************************************************************
public void setLlistaCampus(Vector llistaCampus){
	//Metode que mostra una llista amb tots els campus a (nova_assist.jsp)
	int i=0;
	String ident="";

	ArrayList resp_array=new ArrayList();
	Campus obj_t=new Campus();

	while(i < llistaCampus.size())
	{   
		obj_t= (Campus) llistaCampus.get(i);
		ident=""+ obj_t.getId_campus();
                
		// resp_array.add(new SelectItem(obj_t.getId_campus(),obj_t.getNom()));
                // sakai22 (s'ha de fer així, amb el ident o sino no ho troba)******************
	 	resp_array.add(new SelectItem(ident,obj_t.getNom()));
                i++;
	}
	this.llistaCampus=resp_array;
}

//**********************************************************************
public ArrayList getLlistaCampus(){
	return llistaCampus; 
}

//**********************************************************************
public void setCampus_sel(String campus_sel){
	//System.out.println("Campus al set:"+campus_sel);
	if (campus_sel=="1010") ;
	else this.campus_sel=campus_sel;
}

//**********************************************************************
public void setCampus_sel(ValueChangeEvent ev){
	//Metode polimorfic, que recull el canvi a la listbox de campus i actualitza la listbox d'edifici a (nova_assist.jsp)
	FacesContext ctx = FacesContext.getCurrentInstance();
	FacesMessage msg = new FacesMessage(ev.getNewValue().toString());
	ctx.addMessage(null,msg);
	setCampus_sel(campus_sel);
	getLlistaEdificis();
}

//**********************************************************************
public String getCampus_sel(){
	//Metode que agafa la selecció escollida de la listbox 'campus' de (nova_assist.jsp)
	return campus_sel;
}

//**********************************************************************
public void setLlistaCategoria(ArrayList llistaCategoria){
	//Metode que mostra una llista amb totes les categories d'assistències a (nova_assist.jsp)
	
	int i=0;
	ArrayList resp_array=new ArrayList();
	Categoria obj_t=new Categoria();
	String ident="";
	while(i < llistaCategoria.size())
	{   
		obj_t= (Categoria) llistaCategoria.get(i);
		ident=""+ obj_t.getId_categoria();
		resp_array.add(new SelectItem(ident,obj_t.getTipus()));
		i++;
	}
	//setCategoria_selec(ident); //aixi surten els "..." a tipus d'assistencia i no el item de xarxa
	this.llistaCategoria=resp_array;
}

//**********************************************************************
public void setLlistaCategoria_tipus(ArrayList llistaCategoria_tipus){
	//Metode que mostra una llista amb totes les categories d'assistències per a
	//PODER CANVIAR EL TIPUS D'ASSISTENCIA, SENSE QUE ESTIGUI SELECCIONAT EL ITEM DE XARXA
	// al jsp (assist_admin.jsp)
	int i=0;
	ArrayList resp_array=new ArrayList();
	Categoria obj_t=new Categoria();
	String ident="";
	while(i < llistaCategoria_tipus.size())
	{   
		obj_t= (Categoria) llistaCategoria_tipus.get(i);
		ident=""+ obj_t.getId_categoria();
		resp_array.add(new SelectItem(ident,obj_t.getTipus()));
		i++;
	}
	//setCategoria_selec(ident);
	this.llistaCategoria_tipus=resp_array;
}

//**********************************************************************
public ArrayList getLlistaCategoria_tipus(){
	//Metode que desa una llista amb totes les categories d'assistències per a
	//PODER CANVIAR EL TIPUS D'ASSISTENCIA, SENSE QUE ESTIGUI SELECCIONAT EL ITEM DE XARXA
	// al jsp (assist_admin.jsp)
	return llistaCategoria_tipus; 
}

//**********************************************************************
public String getCategoria_selec_tipus(){
	//Metode que agafa la selecció escollida de la listbox 'categoria' per a
	//PODER DESAR EL TIPUS D'ASSISTENCIA, al jsp (assist_admin.jsp)
	return categoria_selec_tipus;
}

//**********************************************************************
public void setCategoria_selec_tipus(String categoria_selec_tipus){
	//Metode que agafa la selecció escollida de la listbox 'categoria' per a
	//PODER DESAR EL TIPUS D'ASSISTENCIA, al jsp (assist_admin.jsp)
	this.categoria_selec_tipus=categoria_selec_tipus;
}

//**********************************************************************
public ArrayList getLlistaCategoria(){
	return llistaCategoria; 
}

//**********************************************************************
public void setCategoria_selec(String categoria_selec){
	this.categoria_selec=categoria_selec;
}

//**********************************************************************
public void setCategoria_selec(ValueChangeEvent ev){
	//Metode polimorfic, que recull el canvi a la listbox de categoria i actualitza el textbox de descripció (nova_assist.jsp)
	FacesContext ctx = FacesContext.getCurrentInstance();
	FacesMessage msg = new FacesMessage(ev.getNewValue().toString());
	ctx.addMessage(null,msg);
			
	setCategoria_selec(categoria_selec);
	getCategoria();
}

//**********************************************************************
public void setCategoria(String categoria){
	this.categoria=categoria;
}

//**********************************************************************
public String getCategoria(){
	categoria = hibernatebd.getDescripcio(categoria_selec);
	return categoria;
}

//**********************************************************************
public String getCategoria_selec(){
	//Metode que agafa la selecció escollida de la listbox 'categoria' de (nova_assist.jsp)
	return categoria_selec;
}

//**********************************************************************
public void setLlistaEdificis(Vector llistaEdificis){
	//Metode que mostra una llista amb tots els edificis a (nova_assist.jsp)
	int i=0;
	String ident="";
	ArrayList resp_array=new ArrayList();
	Edifici obj_t=new Edifici();

	while(i < llistaEdificis.size())
	{   
		obj_t= (Edifici) llistaEdificis.get(i);
                ident=""+ obj_t.getId_edifici();
                //resp_array.add(new SelectItem(obj_t.getId_edifici(),obj_t.getNom_edifici() ));
                //sakai22 (s'ha de fer així, amb el ident o sino no ho troba)******************
                resp_array.add(new SelectItem(ident,obj_t.getNom_edifici() ));
                i++;
	}
	this.llistaEdificis=resp_array; 
}

//**********************************************************************
public ArrayList getLlistaEdificis(){
	//Metode que agafa el campus escollit per a triar els edificis
	Vector vector_llistaEdificis=hibernatebd.getLlistaEdificis(campus_sel);
	int i=0;
	String ident="";
	ArrayList resp_array=new ArrayList();
	Edifici obj_t=new Edifici();

	while(i < vector_llistaEdificis.size())
	{   
		obj_t= (Edifici) vector_llistaEdificis.get(i);
                ident=""+ obj_t.getId_edifici();
                //resp_array.add(new SelectItem(obj_t.getId_edifici(),obj_t.getNom_edifici() ));
                //sakai22 (s'ha de fer així, amb el ident o sino no ho troba)******************
                resp_array.add(new SelectItem(ident,obj_t.getNom_edifici() ));
                i++;
	}
	this.llistaEdificis=resp_array; 	
	return llistaEdificis; 
}

//**********************************************************************
public void setEdifici_selec(String edifici_selec){
	this.edifici_selec=edifici_selec;
}

//**********************************************************************
public String getEdifici_selec(){
	//Metode que agafa la selecció escollida de la listbox 'edificis' de (nova_assist.jsp)
	return edifici_selec;
}

//**********************************************************************
public String EnviaDades(){
      //Metode que emmagatzema a la base de dades una nova assistencia
      try{
             	int prioritat=1; //prioritat normal
		int estat=0;	 //estat actiu
		int estat_activa=3;//nova assistencia

		if(consulta=="" || despatx=="" || campus_sel=="" || edifici_selec=="" || categoria_selec=="" || telefon==""){
			return "err_nova_assistencia";
		}
		else{
			//agafo el login de l'usuari
			//String usuari = UsageSessionService.getSessionUserId();
			//sakai22
        		String usuari=getUserid();

			//busco el responsable associat a un campus
			String id_tecnic=hibernatebd.BDBuscaResponsable(campus_sel);
												
			//agafo la data del sistema
			Date data_inici=new Date();
			//SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			//System.out.println("La data d'avui es:" + sdf.format(data_inici)); 
				
			
			//escric l'assistencia a la base de dades
			hibernatebd.BDCreaAssistencia(campus_sel, categoria_selec, consulta, prioritat, data_inici, estat, estat_activa, usuari, id_tecnic, telefon, despatx, edifici_selec,codi_udl);

			//netejo tots els camps per a que no tornin a sortir al recarregar la pagina
			telefon="";
			despatx="";
			codi_udl="";
			consulta="";
			campus_sel="";
			edifici_selec="";
			categoria_selec="";
			return "ok";
		}
      }
      catch (Exception ex){
              ex.printStackTrace();
              return "err";
      }
}

//**********************************************************************
public void setTelefon(String telefon){
	this.telefon=telefon;
}

//**********************************************************************
public String getTelefon(){
	return telefon;
}

//**********************************************************************
public void setDespatx(String despatx){
	this.despatx=despatx;
}

//**********************************************************************
public String getDespatx(){
	return despatx;
}

//**********************************************************************
public void setConsulta(String consulta){
	this.consulta=consulta;
}

//**********************************************************************
public String getConsulta(){
	return consulta;
}

//**********************************************************************
public void setSolucio(String solucio){
	this.solucio=solucio;
}

//**********************************************************************
public String getSolucio(){
	return solucio;
}

//**********************************************************************
public void setCodi_udl(String codi_udl){
	this.codi_udl=codi_udl;
}

//**********************************************************************
public String getCodi_udl(){
	return codi_udl;
}

//**********************************************************************
public String RetornaNovaAssist(){
	//Metode cridat des de (ok.jsp) per a tornar enrere
	try{
		return "principi";
	}
	catch (Exception ex){
		ex.printStackTrace();
		return "err";
	}
}

//**********************************************************************
// METODE PER A CREAR UN .PDF DE LES ASSISTENCIES DE LA PANTALLA
//**********************************************************************
public String imprimeixAssistencies(){
	//Obtenim la sessió per passar paràmetres al servlet de impressió
	Session session = SessionManager.getCurrentSession();
 	               
	//Creem un nou document XML
	Document doc = new DocumentImpl();
	Element llista = doc.createElement("llista");
 	       
	try{
		//Afegim cada una de les linies dins del node linia
             	Element linies = doc.createElement("linies");
               
		Iterator it = assistencies.iterator();       
                
		while (it.hasNext()){
			Assistencia lineaassi = (Assistencia) it.next();
 	                               
			Element linia = doc.createElement("linia");
 				                  
     			Element ticket = doc.createElement("ticket");
			Element introduidaper = doc.createElement("introduidaper");
			Element data = doc.createElement("data");
			Element telefon = doc.createElement("telefon");
			Element campus = doc.createElement("campus");
			Element edifici = doc.createElement("edifici");			
			Element despatx = doc.createElement("despatx");
			Element assignadaa = doc.createElement("assignadaa");
			Element tipusassistencia = doc.createElement("tipusassistencia");
                        Element prioritat = doc.createElement("prioritat");
			Element estat = doc.createElement("estat");
			Element descripcio = doc.createElement("descripcio");

			String elticket="" +lineaassi.getTicket();
			
			ticket.appendChild(doc.createTextNode(elticket));
			introduidaper.appendChild(doc.createTextNode(lineaassi.getNom_usuari()));
			data.appendChild(doc.createTextNode(lineaassi.getStrData_inici()));
			telefon.appendChild(doc.createTextNode(lineaassi.getTelefon()));
			campus.appendChild(doc.createTextNode(lineaassi.getNom_campus()));
			edifici.appendChild(doc.createTextNode(lineaassi.getNom_edifici()));
			despatx.appendChild(doc.createTextNode(lineaassi.getLocalitzacio()));
			assignadaa.appendChild(doc.createTextNode(lineaassi.getId_tecnic()));
			tipusassistencia.appendChild(doc.createTextNode(lineaassi.getNom_categoria()));
                        prioritat.appendChild(doc.createTextNode(lineaassi.getNom_prioritat()));
			estat.appendChild(doc.createTextNode(lineaassi.getNom_estat()));
			descripcio.appendChild(doc.createTextNode(lineaassi.getConsulta()));

			linia.appendChild(ticket);
			linia.appendChild(introduidaper);
			linia.appendChild(data);
			linia.appendChild(telefon);
			linia.appendChild(campus);
                        linia.appendChild(edifici);
			linia.appendChild(despatx);
			linia.appendChild(assignadaa);
			linia.appendChild(tipusassistencia);
                        linia.appendChild(prioritat);
			linia.appendChild(estat);
			linia.appendChild(descripcio);
               		
			linies.appendChild(linia);
		}
               
		llista.appendChild(linies);
 	       
		//Afegin l'acta al document
		doc.appendChild(llista);
 	                       
		session.setAttribute("document", doc);
		session.setAttribute("unaSolaAssistencia", "moltes");	        
              
		try {
                        FacesContext fc =FacesContext.getCurrentInstance();
			fc.getExternalContext().redirect("einahelpdesk/assistencies.pdf");
			fc.responseComplete();
		}
		catch (Exception ex){System.out.println("Error a la redireccio");}
 	               
               }
	       catch (Exception ex){
                        System.out.println("Error al obtenir el site o el grup");
               }       
						
               return "con_assist_admin";
 }

//**********************************************************************
// METODE PER A CREAR UN .PDF D'UNA ASSISTENCIA CONCRETA
//**********************************************************************
public String imprimeixUnaAssistencia(){
	//Obtenim la sessió per passar paràmetres al servlet de impressió
        Session session = SessionManager.getCurrentSession();

        //Creem un nou document XML
        Document doc = new DocumentImpl();
        Element llista = doc.createElement("llista");

        try{
                Element linia = doc.createElement("linia");

                Element ticket = doc.createElement("ticket");
                Element introduidaper = doc.createElement("introduidaper");
                Element login = doc.createElement("login");
                Element correu = doc.createElement("correu");
		Element datainici = doc.createElement("datainici");
		Element datafi = doc.createElement("datafi");
		Element despatx = doc.createElement("despatx");
		Element telefon = doc.createElement("telefon");
                Element campus = doc.createElement("campus");
                Element edifici = doc.createElement("edifici");
                Element codimaquina = doc.createElement("codimaquina");
                Element assignadaa = doc.createElement("assignadaa");
                Element tipusassistencia = doc.createElement("tipusassistencia");
                Element prioritat = doc.createElement("prioritat");
                Element estat = doc.createElement("estat");
                Element descripcio = doc.createElement("descripcio");
		Element seguiment = doc.createElement("seguiment");			
		Element seguimentintern = doc.createElement("seguimentintern");

                String elticket="" +assistencia.getTicket();
			
		ticket.appendChild(doc.createTextNode(elticket));
                introduidaper.appendChild(doc.createTextNode(assistencia.getNom_usuari()));
                login.appendChild(doc.createTextNode(assistencia.getUsuari()));
                correu.appendChild(doc.createTextNode(assistencia.getCorreu_usuari()));
		datainici.appendChild(doc.createTextNode(assistencia.getStrData_inici()));
		datafi.appendChild(doc.createTextNode(assistencia.getStrData_fi()));
		despatx.appendChild(doc.createTextNode(assistencia.getLocalitzacio()));
		telefon.appendChild(doc.createTextNode(assistencia.getTelefon()));
                campus.appendChild(doc.createTextNode(assistencia.getNom_campus()));
                edifici.appendChild(doc.createTextNode(assistencia.getNom_edifici()));
                if (assistencia.getCodi_udl()==null)
                {
                	codimaquina.appendChild(doc.createTextNode(""));
                }
                else 
                {
                	codimaquina.appendChild(doc.createTextNode(assistencia.getCodi_udl()));
                }    
                assignadaa.appendChild(doc.createTextNode(assistencia.getNom_tecnic()));
                tipusassistencia.appendChild(doc.createTextNode(assistencia.getNom_categoria()));
                prioritat.appendChild(doc.createTextNode(assistencia.getNom_prioritat()));
                estat.appendChild(doc.createTextNode(assistencia.getNom_estat()));
                descripcio.appendChild(doc.createTextNode(assistencia.getConsulta()));
        if (assistencia.getSolucio()==null)
        {
        	seguiment.appendChild(doc.createTextNode(""));
        }
        else 
        {
        	seguiment.appendChild(doc.createTextNode(assistencia.getSolucio()));
        }
		if (assistencia.getSolucio_interna()==null)
		{
			seguimentintern.appendChild(doc.createTextNode(""));
		}
		else {
			seguimentintern.appendChild(doc.createTextNode(assistencia.getSolucio_interna()));
		}

                linia.appendChild(ticket);
                linia.appendChild(introduidaper);
                linia.appendChild(login);
		linia.appendChild(correu);
		linia.appendChild(datainici);
                linia.appendChild(datafi);
		linia.appendChild(despatx);
		linia.appendChild(telefon);
                linia.appendChild(campus);
                linia.appendChild(edifici);
                linia.appendChild(codimaquina);
                linia.appendChild(assignadaa);
                linia.appendChild(tipusassistencia);
                linia.appendChild(prioritat);
                linia.appendChild(estat);
                linia.appendChild(descripcio);
		linia.appendChild(seguiment);
		linia.appendChild(seguimentintern);
		
		llista.appendChild(linia);	
		
                //Afegin l'acta al document
                doc.appendChild(llista);

                session.setAttribute("document", doc);
		session.setAttribute("unaSolaAssistencia", "una");

                try {
                        FacesContext fc =FacesContext.getCurrentInstance();
                        fc.getExternalContext().redirect("einahelpdesk/assistencia.pdf");
                        fc.responseComplete();
                }
                catch (Exception ex){System.out.println("Error a la redireccio");}
               }
               catch (Exception ex){
                        System.out.println("Error al obtenir el site o el grup");
               }
               return "con_assist_admin";
}

} //fi clase
