package es.udl.asic.sakaiproject.tool.einahelpdesk.mappings;

public class Campus{
private int id_campus;
private String nom;
private String responsable;
private int activat;

public Campus()
{
this.id_campus=0;
this.nom="";
this.responsable="";
this.activat=1;

}
public void setId_campus(int id_campus)
{
	this.id_campus=id_campus;
}
public int getId_campus()
{
	return id_campus;
}
public void setNom(String nom)
{
	this.nom=nom;
}
public String getNom()
{
	return nom;
}
public void setResponsable(String responsable)
{
	this.responsable=responsable;
}
public String getResponsable()
{
	return responsable;
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
