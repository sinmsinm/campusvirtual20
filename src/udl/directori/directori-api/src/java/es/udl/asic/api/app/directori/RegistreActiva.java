package es.udl.asic.api.app.directori;

import java.util.List;

public class RegistreActiva{
	
	private String dni;
	private String login;
	private List <String> codiMatricula;
	
	public String getDni(){
		return dni;
	}
	
	public String getLogin(){
		return login;
	}
	public List <String> getCodiMatricula (){
		return codiMatricula;
	}
	
	public void setDni(String dni){
		this.dni = dni;
	}
	
	public void setLogin (String login){
		this.login = login;
	}
	
	public void setCodiMatricula ( List <String> codiMatricula){
		this.codiMatricula = codiMatricula;
	}
	
}