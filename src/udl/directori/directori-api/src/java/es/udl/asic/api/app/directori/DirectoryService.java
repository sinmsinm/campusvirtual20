package es.udl.asic.api.app.directori;

import java.util.List;
import java.util.Properties;

public interface DirectoryService{
	public void configParameters(Properties properties) throws ParameterConfigException;
	public boolean activaCompte(String userid,String password,String email);
	public boolean activaCompteAlu(String userid,String password);
	public boolean canviPasswd(String userid,String newpasswd);
	public boolean canviPasswdAlu(String userid,String newpasswd);
	public boolean canviDades(String userid,DadesPersonals dadesPersonals);
	public boolean canviDadesAlumne(String userid,DadesPersonals dadesPersonals); 
	public DadesPersonals obtenirDades(String userid);
	public DadesPersonals obtenirDadesAlumne(String userid);
	public DadesPersonals obtenirDadesAlumneAsAdmin(String userid);
	public DadesPersonals obtenirDadesArrel(String userid);
	public DadesPersonals obtenirDades(String userid,boolean isManager);
	public boolean hasThisPassword(String userId,String oldpasswd);
	public boolean hasThisPasswordAlu(String userId,String oldpasswd);
	public List getLlistaUsuaris(String filtre,boolean include);
	public boolean mailExisteix(String usermail);
	public boolean mailExisteix(String usermail,String login);
	public boolean validaUnicAliesLDAP(String aliesMissatgeria);
}


