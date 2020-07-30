/********************************************************************/
/*        EINA HELPDESK -> CODI PER AL PERFIL RESPONSABLE           */
/********************************************************************/

package es.udl.asic.sakaiproject.tool.einahelpdesk;
import javax.faces.context.*;
import javax.faces.model.SelectItem;
import java.util.*;
import java.util.Date; 
import java.text.SimpleDateFormat; 

import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Assistencia;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Campus;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Tecnics;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Categoria;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Edifici;

import es.udl.asic.sakaiproject.tool.einahelpdesk.hibernate.HibernateBD;
import es.udl.asic.sakaiproject.tool.einahelpdesk.HDesk;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.event.ActionEvent;

import java.util.List;

//per autentificar
//import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
//sakai22
import org.sakaiproject.user.cover.UserDirectoryService;
//import org.sakaiproject.service.framework.session.cover.UsageSessionService;
//sakai22
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.event.api.UsageSession;

//configuracio eina
//import org.sakaiproject.api.kernel.tool.Tool;
//import org.sakaiproject.api.kernel.tool.Placement;
//import org.sakaiproject.api.kernel.tool.cover.ToolManager;
//sakai22
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;

//sakai22
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryProvider;
import org.sakaiproject.user.api.UserEdit;

public class HDesk_responsable extends HDesk{
	

		//String userId= UsageSessionService.getSessionUserId();
		//sakai22
		String userId=getUserid();
		String nom_responsable = UserDirectoryService.getCurrentUser().getDisplayName();		
	
		//cerca
		ArrayList columna_cerca=new ArrayList();
		String paraula_clau;
				
		//per ordenar per columnes a la consulta d'assistencies
		String amuntavallres="";//quina imatge fiquem
		String asc_desc="";//en quin ordre
		int sentit = 1; //amunt o avall
		int columna_ordre = 1; //Columna d'ordenacio
		
		ArrayList llistaCampus=new ArrayList();
		int campus_selec=0;
	
		ArrayList llistaEdificis=new ArrayList();
		int edifici_selec;
		
		ArrayList llistaCategoria=new ArrayList();
		int categoria_selec=1;
		
		ArrayList assistencies=new ArrayList();
		
		String telefon;
		String despatx;
		String consulta;
		String codi_udl="";
		String categoria;

		String consulta_formatada;
		
		String nom_campus; //per ficar a la capçelera dels jsp
		int campusId;
		//ticket 
				
//**********************************************************************
public String getNom_campus(){
	return nom_campus;
}

//**********************************************************************
public void setNom_campus(String nom_campus){
	this.nom_campus=nom_campus;
}

//**********************************************************************
public String getUserId(){
        return userId;
}

//**********************************************************************
public void setUserId(String userId){
        this.userId=userId;
}

//**********************************************************************
public String getNom_responsable(){
        return nom_responsable;
}

//**********************************************************************
public void setNom_responsable(String nom_responsable){
        this.nom_responsable=nom_responsable;
}

//**********************************************************************
public HDesk_responsable(){
	//Constructor, inicialitza la connexió amb la BD hibernate
	//hibernatebd=new HibernateBD();
}

//**********************************************************************
public String processActionGoPregFrequents(){
        //
        try{
                return "preg_frequents_respo";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//**********************************************************************
public String processActionConsAssisCampus(){
    //
    try{
			//consultem les assistencies actives i/o resoltes (0 i/o 1) i el seu historic
			super.setColumna_cerca("sense_campus");
			//String userid = UsageSessionService.getSessionUserId();
			//sakai22
			String userid=getUserid();
			campusId=hibernatebd.getCampusResponsable(userid);
			
			super.sentit=1;
			super.columna_ordre=1;
			super.pagina=1;
			super.paraula_clau="";
			super.setIsHistoric(0);
			super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId, isHistoric));
			setNom_campus(hibernatebd.getNom_campus(campusId));
			return "consultar_responsable";
    }
    catch (Exception ex){
            ex.printStackTrace();
            return "err";
    }
}

//**********************************************************************
public String processActionConsultaHistoric(){
      //Metode que mostra totes les assistencies històriques de tots els campus
      try{
		  	//super.setColumna_cerca("sense_campus");
			//String userid = UsageSessionService.getSessionUserId();
			//sakai22
			String userid=getUserid();
			campusId=hibernatebd.getCampusResponsable(userid);
			
			//volem veure TOTES les asssistencies de la UdL
			super.sentit=1;
                	super.columna_ordre=1;
			super.pagina=1;
			
			//Per guanyar rapidesa en l'historic 
			//super.setAssistencies(hibernatebd.getAssistencies(2));
			super.setIsHistoric(2);			
			super.setAssistencies(hibernatebd.getAssistencies(2,Integer.parseInt(super.reg_per_pag),1));
			
			super.setColumna_cerca("sense_estat");
			super.paraula_clau="";
			//super.setIsHistoric(2);	
			//super.setAssistencies(hibernatebd.getAssistenciesRespo(campusId, isHistoric));
			super.setEstatConsulta(1);  //Per guanyar rapidesa en l'historic, aixi es sap la consulta anterior per a tornar-la a fer
			setNom_campus(hibernatebd.getNom_campus(campusId));
			return "consultar_responsable";
      }
      catch (Exception ex){
              ex.printStackTrace();
              return "err";
      }

}

//**********************************************************************
public String processActionAssisResponsable(){
	//
	setAssistencia(hibernatebd.getAssistencia(ticket_sel, userId));
	if (super.isHistoric==2) return "assist_historic"; //si ho es no ens cal cridar a la funcio per omplir el array d'operadors
	else 
	{
		setOperadorsActius(hibernatebd.getOperadorsActius());
		setLlistaCategoria_tipus(hibernatebd.getCategories());
		return "assist_responsable";
	}
}

//**********************************************************************
public String processActionDesarAssistencia(){
	super.processActionDesarAssistencia();
	//super.setAssistencies(hibernatebd.getAssistenciesRespo(campusId, isHistoric));

	//sakai22, els camps buits a la consulta sql de getAssistencies no els suporta.
	//super.setAssistencies(hibernatebd.getAssistenciesRespo(campusId, super.columna_sel,super.paraula_clau, isHistoric,ordenacio,asc_desc));
	super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId, super.columna_sel,super.paraula_clau, isHistoric,"assis.ticket", "DESC"));
	processActionConsAssisCampus();
	return "consultar_responsable";
}

//**********************************************************************
//Per capturar una assistencia concreta
public Assistencia getAssistencia(){
	return assistencia;
}

public void setAssistencia(Assistencia assistencia){
	this.assistencia=assistencia;
}

//**********************************************************************
public void processActionNeteja(){
	//Metode que neteja la llista d'assistències i l'historic, depenent d'on estem
	if(super.isHistoric==0) //no estem a l'historic
		processActionConsAssisCampus();
	
	else					 //estem a l'historic
		processActionConsultaHistoric();	
}

//**********************************************************************
public String RetornaCons(){
        //Metode cridat per tornar enrere ordenadament, mirem si estem a l'historic o q
        try{
                return "consultar_responsable";
        }
        catch (Exception ex){
                ex.printStackTrace();
                return "err";
        }
}

//**********************************************************************
//Metode de cerca sobreescrit
public String processActionCercar(){
	//es la consulta de la cerca del listbox
	super.pagina=1;
	super.setEstatConsulta(11); //Per guanyar rapidesa en l'historic, aixi es sap la consulta anterior per a tornar-la a fer
    	super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId,super.columna_sel,super.paraula_clau, super.isHistoric));
    	return "cons_assist_responsable";
}

//**********************************************************************
public String mouSentit1(){	
	//ordenem pel ticket
	super.mouSentit(1);
	super.asc_desc=(super.sentit==1)?"DESC":"ASC";
	//Per guanyar rapidesa en l'historic
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.ticket",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
	else
		super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId,super.columna_sel,super.paraula_clau,super.isHistoric, "assis.ticket",super.asc_desc));
	super.setEstatConsulta(2);
	return "con_assist_responsable";
}

//**********************************************************************
public String mouSentit2(){	
	//ordenem per data d'inici introduïda
	super.mouSentit(2);
	super.asc_desc=(super.sentit==1)?"DESC":"ASC";
	//Per guanyar rapidesa en l'historic         
	if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.data_inici",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
		super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId,super.columna_sel,super.paraula_clau,super.isHistoric, "assis.data_inici",super.asc_desc));
	super.setEstatConsulta(3);
	return "con_assist_responsable";
}

//**********************************************************************
public String mouSentit3(){	
	//aqui ordenarem per l'id de l'usuari
	super.mouSentit(3);
	super.asc_desc=(super.sentit==0)?"DESC":"ASC";
	//cas especial d'ordenacio, pq el nom, cognoms de l'usuari no es troba en cap taula
	int index=0;
	ArrayList assistencies= new ArrayList();
	Assistencia assis= new Assistencia();
	assistencies=hibernatebd.getAssistenciesRespo(userId, campusId,super.columna_sel,super.paraula_clau,super.isHistoric, "assis.usuari",super.asc_desc);
	
	super.setEstatConsulta(4); //Per guanyar rapidesa en l'historic, en aquest cas no fa res, ja que, el nom d'usuari no esta a la BD	
	
	if (super.asc_desc.equals("ASC")) Collections.sort(assistencies, new NomComparador());
	else Collections.sort(assistencies, new NomComparadorInv());

	super.setAssistencies(assistencies);
	return "con_assist_responsable";
}

//**********************************************************************
public String mouSentit4(){
        //ordenem pel nom de campus
        super.mouSentit(4);
        super.asc_desc=(super.sentit==0)?"DESC":"ASC";
        //Per guanyar rapidesa en l'historic
        if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "campus.nom",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
		super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId, super.columna_sel,super.paraula_clau,super.isHistoric, "campus.nom",super.asc_desc));
	super.setEstatConsulta(5);
        return "con_assist_responsable";
}

//**********************************************************************
public String mouSentit5(){	
	//ordenem pel nom de l'edifici
	super.mouSentit(5);
	super.asc_desc=(super.sentit==0)?"DESC":"ASC";
	//Per guanyar rapidesa en l'historic         
	if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "edifici.nom_edifici",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
		super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId,super.columna_sel,super.paraula_clau,super.isHistoric, "edifici.nom_edifici",super.asc_desc));
	super.setEstatConsulta(6);
	return "con_assist_responsable";
}

//**********************************************************************
public String mouSentit6(){	
	//ordenem pel nom del tecnic
	super.mouSentit(6);
	super.asc_desc=(super.sentit==0)?"DESC":"ASC";
	//Per guanyar rapidesa en l'historic         
	if(super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "tecnic.nom",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
		super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId, super.columna_sel,super.paraula_clau,super.isHistoric, "tecnic.nom",super.asc_desc));
	super.setEstatConsulta(7);
	return "con_assist_responsable";
}

//**********************************************************************
public String mouSentit7(){	
	//ordenem per tipus d'assistencia
	super.mouSentit(7);
	super.asc_desc=(super.sentit==0)?"DESC":"ASC";
	//Per guanyar rapidesa en l'historic
	if (super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "categoria.tipus",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else
		super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId,super.columna_sel,super.paraula_clau,super.isHistoric, "categoria.tipus",super.asc_desc));
	super.setEstatConsulta(8);
	return "con_assist_responsable";
}

//**********************************************************************
public String mouSentit8(){	
	//ordenem per prioritat
	super.mouSentit(8);
	super.asc_desc=(super.sentit==0)?"DESC":"ASC";
	//Per guanyar rapidesa en l'historic         
	if (super.isHistoric==2)
                super.setAssistencies(hibernatebd.getAssistenciesHistoric(super.columna_sel,super.paraula_clau,super.isHistoric, "assis.prioritat",super.asc_desc,Integer.parseInt(super.reg_per_pag),super.pagina));
        else	
		super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId,super.columna_sel,super.paraula_clau,super.isHistoric, "assis.prioritat",super.asc_desc));
	super.setEstatConsulta(9); 
	return "con_assist_responsable";
}

//**********************************************************************
public String mouSentit9(){
	//ordenem per estat	
	super.mouSentit(9);
	super.asc_desc=(super.sentit==1)?"DESC":"ASC";
	super.setAssistencies(hibernatebd.getAssistenciesRespo(userId, campusId, super.columna_sel,super.paraula_clau,super.isHistoric, "assis.estat",super.asc_desc));
	return "con_assist_responsable";
}

//**********************************************************************
//metode per a la gestio del responsable de campus (per poder-se canviar ell mateix el responsable del seu campus)
public String processGestioResponsableCampus(){
	//per a que aparegui el campus al jsp
	campusId=hibernatebd.getCampusResponsable(userid);
	setNom_campus(hibernatebd.getNom_campus(campusId));
	
	setOperadorsActius(hibernatebd.getOperadorsActius());
        return "gestio_respon_campus";
}

//**********************************************************************
//subtitueix un responsable per un operador
public String processActionSubstituirResponsableCampus(){
        //'campus' de Bescanviar es un string, passem de int a string
	String campus = "" + campusId;	   
		
	hibernatebd.Bescanviar(campus, tecnic_sel);
	getNom_responsable();
	setOperadorsActius(hibernatebd.getOperadorsActius());

	return "pantalla_sortir";
}

//**********************************************************************
//Metode que genera un .pdf amb la llista d'assistencies, amb possibilitat d'imprimir-lo
public String imprimeixAssistencies(){
        //Genero un informe
        super.imprimeixAssistencies();
        return "con_assist_responsable";
}
//**********************************************************************
//Metode que genera un .pdf amb l'assistencia que hi ha en pantalla
public String imprimeixUnaAssistencia(){
        //Genero un informe
        super.imprimeixUnaAssistencia();
        return "con_assist_responsable";
}


}
