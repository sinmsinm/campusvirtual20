/********************************************************************/
/*        EINA HELPDESK -> CODI PER AL PERFIL PAS/PDI               */
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

//configuracio eina
//import org.sakaiproject.api.kernel.tool.Tool;
//import org.sakaiproject.api.kernel.tool.Placement;
//import org.sakaiproject.api.kernel.tool.cover.ToolManager;
//sakai22
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;

public class HDesk_pas_pdi extends HDesk{
	
private 
		//String userId= UsageSessionService.getSessionUserId();
		//sakai22
		String userId=getUserid();
				
		//cerca
		ArrayList columna_cerca=new ArrayList();
				
		String consulta_formatada;
		
		//per retenir la resposta de la pantalla assist_pas_pdi.jsp
		String resposta;

//**********************************************************************	
public void setColumna_cerca(ArrayList columna_cerca){
	this.columna_cerca=columna_cerca;
}

//**********************************************************************
public ArrayList getColumna_cerca(){
	return columna_cerca;
}

//**********************************************************************
public HDesk_pas_pdi(){
	//Constructor
	
}

//**********************************************************************
public String processActionGoPregFrequents(){
	//Metode que enllaça amb preg_frequents.jsp
	try{
		return "preg_frequents_pas_pdi";
	}
	catch (Exception ex){
		ex.printStackTrace();
		return "err";
	}
}

//**********************************************************************
public String processActionConsAssistencies(){
    //Metode que mostra la llista d'assistencies del PAS/PDI (cons_assist_pas_pdi.jsp)
    try{
			//nom_pas_pdi = UserDirectoryService.getCurrentUser().getDisplayName();
			
			//consultem les assistencies actives i/o resoltes (0 i/o 1) i el seu historic
		 	super.setIsHistoric(0);
			super.sentit=1;
			super.columna_ordre=1;
			super.pagina=1;
			super.setColumna_cerca("sense_usuari");
			setColumna_cerca(super.getColumna_cerca());
		
			super.paraula_clau="";
			
			super.setAssistencies(hibernatebd.getAssistenciesPAS(userId, isHistoric));

			return "cons_assist_pas_pdi";
    }
    catch (Exception ex){
            ex.printStackTrace();
            return "err";
    }
}

//**********************************************************************
public String processActionConsAssistenciesHistoric(){
    //Metode que mostra la llista d'assistencies HISTÒRIQUES del PAS/PDI (cons_assist_pas_pdi.jsp)
    try{
                        //nom_pas_pdi = UserDirectoryService.getCurrentUser().getDisplayName();

                        //consultem les assistencies actives i/o resoltes (0 i/o 1) i el seu historic
			super.setIsHistoric(2);
                        super.sentit=1;
                        super.columna_ordre=1;
                        super.pagina=1;
                        super.setColumna_cerca("sense_usuari");
                        setColumna_cerca(super.getColumna_cerca());

                        super.paraula_clau="";

                        super.setAssistencies(hibernatebd.getAssistenciesPAS(userId, isHistoric));

                        return "cons_assist_pas_pdi";
    }
    catch (Exception ex){
            ex.printStackTrace();
            return "err";
    }
}

//**********************************************************************
public String processActionCercar(){
	super.pagina=1;
	super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,super.columna_sel,super.paraula_clau, super.isHistoric));
    	return "cons_assist_admin";
}

//**********************************************************************
public void processActionNeteja(){
        //Metode que neteja la llista d'assistències i l'historic, depenent d'on estem
        if(super.isHistoric==0)                 //no estem a l'historic
                processActionConsAssistencies();
        else                                    //estem a l'historic
                processActionConsAssistenciesHistoric();
}

//**********************************************************************
public String ConsultaPasPdi(){
	//Metode que mostra la consulta escollida en format data i usuari de (assist_pas_pdi)
	try{
		//per agafar l'assistencia escollida de la llista
		setAssistencia(hibernatebd.getAssistencia(ticket_sel, userId));
				
		return "assist_pas_pdi";
	}
	catch (Exception ex){
	    ex.printStackTrace();
		return "err";
	}
}

//**********************************************************************
public Assistencia getAssistencia(){
	return assistencia;
}

//**********************************************************************
public void setAssistencia(Assistencia assistencia){
	this.assistencia=assistencia;
}
//**********************************************************************
public void setConsulta_formatada(String consulta_formatada){
	this.consulta_formatada=consulta_formatada;
}

//**********************************************************************
public String getConsulta_formatada(){
	return consulta_formatada;
}

//**********************************************************************
public String EnviarResposta(){
	//Metode que guarda una resposta a una consulta amb la data i l'usuari (assist_pas_pdi)
	
	//tinc la consulta -> assistencia.consulta 
	//tinc la resposta -> resposta
	//agafo la data del sistema
	Date data_resposta=new Date();
	String data;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	data=sdf.format(data_resposta);

	int estat=assistencia.getEstat();
	int estat_activa;
	if (estat==1) 	estat_activa=2;	//si l'assistencia està resolta la marco com 2 per a que es vegi en vermell negreta
	else		estat_activa=1;	//com que l'usuari ha fet una modificacio, ho marco amb vermell
					//estat_activa=0 -> normal, estat_activa=1 -> vermell, estat_activa=2 -> vermell negreta		

	estat=0;    //en el cas de que l'assistencia estigui resolta, l'he de passar a activa

	//agafo el nom de l'usuari del sakai -> nom_pas_pdi
	//uneixo la data el nom, la resposta amb la solucio i la guardo a la base de dades
	String texte= assistencia.getSolucio();
	if (texte==null) texte="";
	String resposta_formatada= texte +"\n"+ data +"\t (" + super.nom + ") "+ resposta;
		
	//busco de quin es l'identificador de la resposta
	int ticket= assistencia.getTicket();
	//escric la resposta a la base de dades
	if (resposta=="");
	else hibernatebd.BDEscriuResposta(ticket, resposta_formatada, estat, estat_activa);
		
	resposta="";
	processActionConsAssistencies();
	return "cons_assist_pas_pdi";
}

//**********************************************************************
public String RetornaCons(){
        //Metode cridat per tornar enrere ordenadament, mirem si estem a l'historic o q
        try{
                //System.out.println("RETORNO AL Altre");
          	return "cons_assist_pas_pdi";
        }
        catch (Exception ex){
                ex.printStackTrace();
                System.out.println("M'EQUIVOCO AL MAIN");
                return "err";
        }
}

//**********************************************************************
public void setResposta(String resposta){
	this.resposta=resposta;
}

//**********************************************************************
public String getResposta(){
	return resposta;
}

//**********************************************************************
public void mouSentit(int sentit_ordre) {
	super.mouSentit(sentit_ordre);
}

//**********************************************************************
public String mouSentit1(){	
	//ordenem pel ticket
	super.mouSentit(1);
	super.asc_desc=(sentit==1)?"DESC":"ASC";
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,4, "assis.ticket",asc_desc));
	else
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,3, "assis.ticket",asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit2(){	
	//ordenem per data d'inici introduïda
	super.mouSentit(2);
	super.asc_desc=(sentit==1)?"DESC":"ASC";
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,4, "assis.data_inici",asc_desc));
	else
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,3, "assis.data_inici",asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit4(){	
	//ordenem pel nom del campus
	super.mouSentit(4);
	super.asc_desc=(sentit==0)?"DESC":"ASC";
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,4, "campus.nom",asc_desc));
	else
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,3, "campus.nom",asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit5(){	
	//ordenem pel nom de l'edifici
	super.mouSentit(5);
	super.asc_desc=(sentit==0)?"DESC":"ASC";
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,4, "edifici.nom_edifici",asc_desc));
	else
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,3, "edifici.nom_edifici",asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit6(){	
	//ordenem pel nom del tecnic
	super.mouSentit(6);
	super.asc_desc=(sentit==0)?"DESC":"ASC";
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,4, "tecnic.nom",asc_desc));
	else
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,3, "tecnic.nom",asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit7(){	
	super.mouSentit(7);
	super.asc_desc=(sentit==0)?"DESC":"ASC";
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,4, "categoria.tipus",asc_desc));
	else
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,3, "categoria.tipus",asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit8(){	
	super.mouSentit(8);
	super.asc_desc=(sentit==0)?"DESC":"ASC";
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,4, "assis.prioritat",asc_desc));
	else
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,3, "assis.prioritat",asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public String mouSentit9(){	
	super.mouSentit(9);
	super.asc_desc=(sentit==1)?"DESC":"ASC";
	if(super.isHistoric==2)
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,4, "assis.estat",asc_desc));
	else
		super.setAssistencies(hibernatebd.getAssistenciesPAS(userId,columna_sel,paraula_clau,3, "assis.estat",asc_desc));
	return "con_assist_admin";
}

//**********************************************************************
public void processActionViewTicket(ActionEvent evt)
{
	//Metode que captura el ticket a la llista
	FacesContext ctx = FacesContext.getCurrentInstance();
    	String ticket_selStr = (String) ctx.getExternalContext().getRequestParameterMap().get("ticket");
    	ticket_sel = Integer.parseInt(ticket_selStr);     
}


} //fi clase
