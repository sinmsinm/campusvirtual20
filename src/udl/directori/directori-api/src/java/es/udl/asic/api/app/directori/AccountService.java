package es.udl.asic.api.app.directori;

import java.util.List;
import java.util.Properties;

public interface AccountService{
	public void test();
	public List<String> cercaPerEmailAlternatiu (String email);
	public List<DadesPersonals> cercaPerNomUsuari (String username);
	public String creaToken (String username);
	public DadesPersonals checkForToken (String token);
	public boolean canviaClau (String username, String password);
	public boolean desaEvidencia (String accio,String dni, String login, String mailAlternatiu, String ip, String agent);
}