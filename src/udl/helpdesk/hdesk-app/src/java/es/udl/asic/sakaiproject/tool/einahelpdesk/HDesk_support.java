// classe que exten HDesk
/**
 * 
 **/

package es.udl.asic.sakaiproject.tool.einahelpdesk;
import javax.faces.context.*;
import javax.faces.model.SelectItem;
import java.util.*;

import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Assistencia;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Campus;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Tecnics;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Categoria;

import es.udl.asic.sakaiproject.tool.einahelpdesk.hibernate.HibernateBD;
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
//sakai22
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

public class HDesk_support extends HDesk{

	String userId=getUserid();
	
private	
	String descripcio;
	String categ_sel;//="1";
	String nou_tipus="";
	String nova_descripcio="";
	String consulta;
	String id_operador;
	String campus_sel;
	
/*			
//Captura el ticket a la llista
public void processActionViewTicket(ActionEvent evt)
{
		FacesContext ctx = FacesContext.getCurrentInstance();
        String ticket_selStr = (String) ctx.getExternalContext().getRequestParameterMap().get("ticket");
        ticket_sel = Integer.parseInt(ticket_selStr);
}*/

public String processActionAssisAdmin(){
	setAssistencia(hibernatebd.getAssistencia(ticket_sel, userId));
	if (isHistoric==2) return "assist_historic"; //si ho es, no ens cal cridar a la funcio per emplenar el array d'operadors
	else{
		setOperadorsActius(hibernatebd.getOperadorsActius());
		setLlistaCategoria_tipus(hibernatebd.getCategories());
		return "assist_admin";
	}
}

//Per capturar una assistencia concreta
public Assistencia getAssistencia(){
	return assistencia;
}

public void setAssistencia(Assistencia assistencia){
	this.assistencia=assistencia;
}

//inici metodes gestio categories
public void setNou_tipus(String nou_tipus){
	this.nou_tipus=nou_tipus; 
}

public void setNova_descripcio(String nova_descripcio){
	this.nova_descripcio=nova_descripcio;
}

public String getNou_tipus(){
	return "";
}

public String getNova_descripcio(){
	return "";
}

public void setCateg_sel(String categ_sel){
	this.categ_sel=categ_sel;
}

//Detecta quan movem el desplegable a la gestio de categories
public void setCateg_sel(ValueChangeEvent ev){	
	FacesContext ctx = FacesContext.getCurrentInstance();
	Map map = ctx.getExternalContext().getRequestMap();
	FacesMessage msg = new FacesMessage(ev.getNewValue().toString());
	ctx.addMessage(null,msg);
	String categ_sel_str=(String)ev.getNewValue();
	//setCateg_sel(Integer.parseInt(categ_sel_str));
	setCateg_sel(categ_sel_str);
	getDescripcio();	
}

public String getCateg_sel(){
	return categ_sel;
}

public String getDescripcio(){
	descripcio=hibernatebd.getDescripcio(categ_sel);
	return descripcio;	
}

public void setDescripcio(String descripcio){
	this.descripcio=descripcio;
}

public ArrayList getCategories(){
	return categories;
}

public void setCategories(ArrayList categories_bd){
	int i=0;
	ArrayList resp_array=new ArrayList();
	Categoria obj_c=new Categoria();
	String ident="";
	//SelectItem si = new SelectItem();
	while(i < categories_bd.size())
	{   
	obj_c= (Categoria) categories_bd.get(i);
	ident=""+ obj_c.getId_categoria();
	resp_array.add(new SelectItem(ident,obj_c.getTipus()));
	setCateg_sel(ident);
	i++;
	}
	this.categories=resp_array;
}

//fi metodes gestio categories

//inici metodes gestio responsables/tecnics

public void setCampus_sel(String  campus_sel){
	this.campus_sel=campus_sel;
}

public String  getCampus_sel(){
	return campus_sel;
}

public void setResponsables(Vector responsables){
	int i=0;
	String ident="";
	ArrayList resp_array=new ArrayList();
	Campus obj_c=new Campus();
	String nom_responsable="";
	
	while(i < responsables.size())
	{ 
		obj_c= (Campus) responsables.get(i);
		nom_responsable=hibernatebd.getOperador(obj_c.getResponsable());
		ident=""+ obj_c.getId_campus();
		//resp_array.add(new SelectItem(obj_c.getId_campus(),obj_c.getNom()+", "+nom_responsable));
		//sakai22 (s'ha de fer així, amb el ident o sino no ho troba)******************
		resp_array.add(new SelectItem(ident,obj_c.getNom()+", "+nom_responsable));
		i++;
	}
	this.responsables=resp_array;	
}

public ArrayList getResponsables(){
	return responsables; 
}

/*
public void setOperadors(Vector operadors)
{

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

public ArrayList getOperadors()
{
	return operadors; 
}
*/
//fi metodes gestio responsables/operadors

public void setConsulta(String consulta){
	this.consulta=consulta;
}

public String getConsulta(){
	return consulta;
}

public HDesk_support(){
	
	/*Tool tool = ToolManager.getCurrentTool();
    	Placement pla = ToolManager.getCurrentPlacement();
    	prop=new Properties();
    	prop=pla.getConfig();
	dialect = pla.getConfig().getProperty("hibernate.dialect");
	url = pla.getConfig().getProperty("hibernate.connection.url");
	driver = pla.getConfig().getProperty("hibernate.connection.driver_class");
	username = pla.getConfig().getProperty("hibernate.connection.username");
	password = pla.getConfig().getProperty("hibernate.connection.password");
	supportAgent=pla.getConfig().getProperty("support.agent");
	perfil = doAutentificar();*/
}
//Process actions dels jsp's

public String processActionGoConsulta(){
	try{
		return "cons_assist_admin";
	}
	catch (Exception ex){
		ex.printStackTrace();
		return "err";
	}
}

public String processNovaAssistencia(){
	return "";
}

//Support Agent
public String processGestioResponsables(){
	setResponsables(hibernatebd.getResponsables());
	setOperadorsActius(hibernatebd.getOperadorsActius());
	return "gestio_respon";
}

public String processActionSubstituir(){
	hibernatebd.Bescanviar(campus_sel, tecnic_sel);
	setResponsables(hibernatebd.getResponsables());
	setOperadorsActius(hibernatebd.getOperadorsActius());
	return "gestio_respon";
}

public String processGestioTipus(){
	setCategories(hibernatebd.getCategories());
	return "gestio_tipus";
}

public String processActionEditarCat(){
	hibernatebd.setTipus(categ_sel,descripcio);
	return "gestio_tipus";
}

public String processActionEsborrarCat(){
	if (hibernatebd.comprovaInactiva(categ_sel)) hibernatebd.delTipus(categ_sel);
	else System.out.println("La categoria esta sent utilitzada");
	setCategories(hibernatebd.getCategories());
	return "gestio_tipus";
}

public String processActionCrearCat(){
	hibernatebd.crearTipus(nou_tipus, nova_descripcio);
	setCategories(hibernatebd.getCategories());
	return "gestio_tipus";
}

public String processActionCercar(){
	super.processActionCercar();
	return "cons_assist_admin";
}

//**********************************************************************
public String processActionGoCreaTipus(){
        //
        try{
                return "gest_tipus_assist";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//**********************************************************************
public String processActionGoCreaOperador(){
        //Metode que activa la pantalla (gest_operadors.jsp)
        try{
		//connexió amb la base de dades
		//hibernatebd=new HibernateBD();
		setOperadors(hibernatebd.getOperadors()); 
		return "crea_operador";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
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

//*********************************************************************
public String processActionDesarAssistencia(){
	super.processActionDesarAssistencia();
    	//super.setAssistencies(hibernatebd.getAssistencies(isHistoric));//per refrescar
    	//super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel, super.paraula_clau, isHistoric,super.ordenacio, asc_desc));
	
	//sakai22, els camps buits a la consulta sql de getAssistencies no els suporta.
	super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel, super.paraula_clau, isHistoric,"assis.ticket", "DESC"));
	processActionConsultaAssis();
	return "cons_assist_admin";
}

//**********************************************************************
public String processActionConsultaAssis(){
        try{
		//consultem les assistencies actives i/o resoltes (0 i/o 1)
                super.sentit=1;
		super.columna_ordre=1;
		super.pagina=1;
		super.setIsHistoric(0);
		super.setAssistencies(hibernatebd.getAssistencies(0));
                super.setColumna_cerca("totes");//deixem totes les columnes
                super.paraula_clau="";
              	
		return "cons_assist_admin";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//**********************************************************************
public String processActionConsultaHistoric(){
        //consultem la llista d'assistencies historiques
        try{
		super.sentit=1;
                super.columna_ordre=1;
		super.pagina=1;
		
		//Per guanyar rapidesa en l'historic
		//super.setAssistencies(hibernatebd.getAssistencies(2));
                super.setIsHistoric(2);
                super.setAssistencies(hibernatebd.getAssistencies(2,Integer.parseInt(super.reg_per_pag),1));

		super.setColumna_cerca("sense_estat");//treiem la columna d'estat, pq totes son historiques.
               	super.paraula_clau="";
		super.setEstatConsulta(1);  //Per guanyar rapidesa en l'historic, aixi es sap la consulta anterior per a tornar-la a fer
		//super.setIsHistoric(2); 

		return "cons_assist_admin";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//*********************************************************************
public String EsborraOperador(){
        //Metode que esborra un operador de (gest_operadors.jsp)
        try{
		System.out.println("Vull esborrar l'operador: "+tecnic_sel);	
                if (hibernatebd.checkUser(tecnic_sel).equals("responsable")) System.out.println("No puc que es responsable!");
                else hibernatebd.BDEsborraOperador(tecnic_sel);
		setOperadors(hibernatebd.getOperadors());
				
		return "crea_operador";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//*********************************************************************
public String CreaOperador(){
        //Metode que crea un operador nou a (gest_operadors.jsp)
        try{
				//sakai 2.2
				//String nom_operador = UserDirectoryService.getUser(id_operador).getDisplayName();
				String nom_operador="";
				try {
        	                        //sakai22: el getUserByEid retorna de un login (id_operador) un usr i d'aquest podem obtenir el seu nom complet
	                                User usr = UserDirectoryService.getUserByEid(id_operador);
                	                nom_operador = usr.getDisplayName();
                        	}catch (Exception ex){
                        	        System.out.println("No s'ha pogut obtenir l'usuari");
                       		}

				hibernatebd.BDCreaOperador(id_operador.toLowerCase(), nom_operador);
				setOperadors(hibernatebd.getOperadors());
				return "crea_operador";
		}
        catch (Exception ex){
				ex.printStackTrace();
				return "err";
        }
}

// desactivació d'un operador

public String DesactivaOperador(){
	 
	try{
		//System.out.println("Desactivem l'operador: "+tecnic_sel);	
        if (hibernatebd.checkUser(tecnic_sel).equals("responsable")) System.out.println("No puc, "+tecnic_sel+" és responsable de campus!");
        else hibernatebd.BDDesactivaOperador(tecnic_sel);
		setOperadors(hibernatebd.getOperadors());
				
		return "crea_operador";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
	
}

// activació d'un operador

public String ActivaOperador(){
	
	try{
		//System.out.println("Activem l'operador: "+tecnic_sel);	
        hibernatebd.BDActivaOperador(tecnic_sel);
		setOperadors(hibernatebd.getOperadors());
				
		return "crea_operador";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
	
}


//**********************************************************************
public void setId_operador(String id_operador){
	this.id_operador=id_operador;
}

//**********************************************************************
public String getId_operador(){
	return id_operador;
}

//**********************************************************************
public String RetornaCons(){
        //Metode cridat per tornar enrere ordenadament, mirem si estem a l'historic o q
        try{
        	return "cons_assist_admin";
        }
        catch (Exception ex){
                ex.printStackTrace();
		return "err";
        }
}

//**********************************************************************
public String RetornaMain(){
        //Metode cridat des de (err.jsp) per a tornar enrere
        try{
                return "main";
        }
        catch (Exception ex){
                ex.printStackTrace();
		return "err";
        }
}

//**********************************************************************
public void processActionNeteja(){
	//Metode que neteja la llista d'assistències i l'historic, depenent d'on estem
	if(super.isHistoric==0) 		//no estem a l'historic
		processActionConsultaAssis();
	else					//estem a l'historic
		processActionConsultaHistoric();	
}

//**********************************************************************
public String mouSentit1(){	
	//ordenem pel ticket
	super.mouSentit1();
	
	//Per guanyar rapidesa en l'historic
        if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.ticket",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
		super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.ticket",super.asc_desc));
	super.setEstatConsulta(2);

	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit2(){	
	//ordenem per data d'inici introduïda
	super.mouSentit2();

	//Per guanyar rapidesa en l'historic
        if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.data_inici",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
		super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.data_inici",super.asc_desc));
	super.setEstatConsulta(3);

	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit3(){	
	//aqui ordenarem per l'id de l'usuari
	super.mouSentit3();
	
	//cas especial d'ordenacio, pq el nom, cognoms de l'usuari no es troba en cap taula
	//Per guanyar rapidesa en l'historic
        super.setEstatConsulta(4);

	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit4(){	
	//ordenem pel nom de campus
	super.mouSentit4();

	//Per guanyar rapidesa en l'historic
        if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "campus.nom",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
	else
                super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel,super.paraula_clau,super.isHistoric, "campus.nom",super.asc_desc));
        super.setEstatConsulta(5);

	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit5(){	
	//ordenem pel nom de l'edifici
	super.mouSentit5();

	//Per guanyar rapidesa en l'historic
        if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "edifici.nom_edifici",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
	else
                super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel,super.paraula_clau,super.isHistoric, "edifici.nom_edifici",super.asc_desc));
        super.setEstatConsulta(6);

	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit6(){
	//ordenem pel nom del tecnic	
	super.mouSentit6();
	
	//Per guanyar rapidesa en l'historic
        if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "tecnic.nom",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
                super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel,super.paraula_clau,super.isHistoric, "tecnic.nom",super.asc_desc));
	super.setEstatConsulta(7);
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit7(){	
	//ordenem per tipus d'assistencia
	super.mouSentit7();

	//Per guanyar rapidesa en l'historic
        if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "categoria.tipus",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
                super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel,super.paraula_clau,super.isHistoric, "categoria.tipus",super.asc_desc));
	super.setEstatConsulta(8);

	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit8(){	
	//ordenem per prioritat	
	super.mouSentit8();
	
	//Per guanyar rapidesa en l'historic
        if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.prioritat",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
                super.setAssistencies(hibernatebd.getAssistencies(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.prioritat",super.asc_desc));
	super.setEstatConsulta(9);
	
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit9(){	
	//ordenem per estat
	super.mouSentit9();
	return "con_assist_admin";
}

//**********************************************************************
//Metode que genera un .pdf amb la llista d'assistencies, amb possibilitat d'imprimir-lo
public String imprimeixAssistencies(){
        //Genero un informe
        super.imprimeixAssistencies();
        return "con_assist_admin";
}
//**********************************************************************
//Metode que genera un .pdf amb l'assistencia que hi ha en pantalla
public String imprimeixUnaAssistencia(){
        //Genero un informe
        super.imprimeixUnaAssistencia();
        return "con_assist_admin";
}


}
