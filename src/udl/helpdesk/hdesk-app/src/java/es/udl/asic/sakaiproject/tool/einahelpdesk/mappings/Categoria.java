package es.udl.asic.sakaiproject.tool.einahelpdesk.mappings;

public class Categoria{
private int id_categoria;
		String tipus;
		String descripcio;
public Categoria()
{
	this.id_categoria=0;
	this.tipus="";
	this.descripcio="";
}
public void setId_categoria(int id_categoria)
{
	this.id_categoria=id_categoria;
}
public int getId_categoria()
{
	return id_categoria;
}
public void setTipus(String tipus)
{
	this.tipus=tipus;
}
public String getTipus()
{
return tipus;	
}
public void setDescripcio(String descripcio)
{
	this.descripcio=descripcio;
}
public String getDescripcio()
{
return descripcio;	
}
}