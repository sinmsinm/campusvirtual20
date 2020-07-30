package es.udl.asic.sakaiproject.tool.einahelpdesk.mappings;

public class Edifici{
private int id_edifici;
		int id_campus;
		String nom_edifici;

public Edifici(){
}

public void setId_edifici(int id_edifici){
	this.id_edifici=id_edifici;
}

public int getId_edifici(){
	return id_edifici;
}

public void setNom_edifici(String nom_edifici){
	this.nom_edifici=nom_edifici;
}

public String getNom_edifici(){
	return nom_edifici;	
}

public void setId_campus(int id_campus){
	this.id_campus=id_campus;
}

public int getId_campus(){
	return id_campus;
}

}