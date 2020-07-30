package es.udl.asic.api.app.directori;

import java.util.List;
/**
 * 
 * @author Alex - ASIC
 *
 * Aquesta inteficie te els metodes per obtenir les dades d'un usuari
 * 
 */

public interface Directori{
	
	public String getId(); 
	public String getNom();
	public String getCognoms();
	public String getTlf();
	public String getEmail();
	public String getFax();
	public String getLocal();
	public String getProv();
	public String getposadd();
	public String getcodpos();
	public String getRoomNumber();
	public String getCentre();
	public String getWebpersonal();
	//public String getMissatgeria();
	public List getLlistaUsuaris(String cerca);
	public boolean getUserInf(String id);
	public void setBasePath(String str);
}



