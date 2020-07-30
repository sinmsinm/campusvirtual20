package es.udl.asic.sakaiproject.tool.einahelpdesk.mappings;

public class Tecnics{
private String id_tecnic;
private String nom;
private int activat;

public Tecnics()
{
	this.id_tecnic="";
	this.nom="";
	this.activat=1;
}

public void setId_tecnic(String id_tecnic)
{
	this.id_tecnic=id_tecnic;
}

public String getId_tecnic()
{
	return id_tecnic;
}

public void setNom(String nom)
{
	this.nom=nom;
}

public String getNom()
{
	return nom;	
}

public void setActivat(int activat)
{
	this.activat=activat;
}
public Integer getActivat()
{
	return new Integer(this.activat);
}


}