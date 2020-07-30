package es.udl.asic.sakaiproject.tool.einahelpdesk.mappings;
import java.lang.Integer;
import java.util.Date;
import java.text.SimpleDateFormat;
import es.udl.asic.sakaiproject.tool.einahelpdesk.HDesk;

//sakai22
//import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.user.cover.UserDirectoryService;

//sakai22
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryProvider;
import org.sakaiproject.user.api.UserEdit;


public class Assistencia{
private int ticket;
		int id_campus;
		int id_edifici;
		String usuari;
		Date data_inici;
		Date data_fi;
		String consulta;
		String solucio;
		String solucio_interna;
		int id_categoria;
		int prioritat;
		int estat;
		String codi_udl;
		
		String id_tecnic;
		String nom_usuari;
		String nom_campus;
		String nom_edifici;
		String nom_tecnic;
		String nom_categoria;
		String nom_prioritat;
		String nom_estat;
		String strData_inici;
		String strData_fi;
		
		String localitzacio;
		String telefon;
		String correu_usuari;
		int estat_activa;

public Assistencia(){
	this.ticket=0;
	this.usuari="";
	this.consulta="";
	this.id_campus=0;
	this.id_categoria=0;
	this.prioritat=0;
	this.estat=0;
	this.estat_activa=0;
}

public void setTicket(int ticket){
	this.ticket= ticket;
}

public int getTicket(){
	return ticket;
}

public void setUsuari(String usuari){
	this.usuari=usuari;
}

public String getUsuari(){
	return usuari;
}

public void setConsulta(String consulta){
	this.consulta=consulta;
}

public String getConsulta(){
	return consulta;
}

public void setId_campus(int campus){
	this.id_campus=campus;	
}

public int getId_campus(){
	return id_campus;	
}

public void setId_edifici(int edifici){
	this.id_edifici=edifici;	
}
public int getId_edifici(){
	return id_edifici;	
}

public void setId_categoria(int categoria){
	this.id_categoria=categoria;	
}

public int getId_categoria(){
	return id_categoria;	
}

public void setPrioritat(int prioritat){
	this.prioritat=prioritat;	
}

public int getPrioritat(){
	return prioritat;	
}

public void setEstat(int estat){
	this.estat=estat;	
}

public int getEstat(){
	return estat;	
}

public void setEstat_activa(int estat_activa){
	this.estat_activa=estat_activa;
}

public int getEstat_activa(){
	return estat_activa;
}

public Date getData_inici(){
	return data_inici;	
}

public void setData_inici(Date data_inici){
	this.data_inici=data_inici;	
}

public Date getData_fi(){
	return data_fi;	
}

public void setData_fi(Date data_fi){
	this.data_fi=data_fi;	
}

public String getId_tecnic(){
	return id_tecnic;	
}

public void setId_tecnic(String id_tecnic){
	this.id_tecnic=id_tecnic;	
}

public void setNom_campus(String nom_campus){
	this.nom_campus=nom_campus;
}

public String getNom_campus(){
	return nom_campus;	
}

public void setNom_tecnic(String nom_tecnic){
	this.nom_tecnic=nom_tecnic;
}

public String getNom_tecnic(){
	return nom_tecnic;	
}

public void setNom_categoria(String nom_categoria){
	this.nom_categoria=nom_categoria;
}

public String getNom_categoria(){
	return nom_categoria;	
}

public void setNom_prioritat(String nom_prioritat){
	this.nom_prioritat=nom_prioritat;
}

public String getNom_prioritat(){
	return nom_prioritat;	
}

public void setNom_estat(String nom_estat){
	this.nom_estat=nom_estat;
}

public String getNom_estat(){
	return nom_estat;	
}

public void setNom_edifici(String nom_edifici){
	this.nom_edifici=nom_edifici;
}

public String getNom_edifici(){
	return nom_edifici;	
}

public void setNom_usuari(String nom_usuari){
	this.nom_usuari=nom_usuari;
}

public String getNom_usuari(){
	return nom_usuari;	
}

public void setSolucio(String solucio){
	this.solucio=solucio;
}

public String getSolucio(){
	return solucio;	
}

public void setSolucio_interna(String solucio_interna){
        this.solucio_interna=solucio_interna;
}

public String getSolucio_interna(){
        return solucio_interna;
}

//Despatx
public void setLocalitzacio(String localitzacio){
	this.localitzacio=localitzacio;
}

public String getLocalitzacio(){
	return localitzacio;	
}

public void setTelefon(String telefon){
	this.telefon=telefon;
}

public String getTelefon(){
	return telefon;	
}

public void setCodi_udl(String codi_udl){
	this.codi_udl=codi_udl;
}

public String getCodi_udl(){
	return codi_udl;	
}

public String getStrData_inici(){
	return strData_inici;	
}

public void setStrData_inici(Date data_inici){
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	this.strData_inici=sdf.format(data_inici);	
}

public String getStrData_fi(){
	return strData_fi;	
}

public void setStrData_fi(Date data_fi){
	if (data_fi==null)	this.strData_fi="";
	else {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.strData_fi=sdf.format(data_fi);
	}
}

public String getCorreu_usuari(){
	String correu="";
	try {
		//sakai22, per a que agafi correctament el correu de l'usuari
		//correu=UserDirectoryService.getUser(usuari).getEmail();
		User usr=UserDirectoryService.getUserByEid(usuari);
		correu=usr.getEmail();
	}
	catch(Exception e){}
	return correu;
}


}
