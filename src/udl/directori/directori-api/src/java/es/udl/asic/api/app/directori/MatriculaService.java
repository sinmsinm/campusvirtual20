package es.udl.asic.api.app.directori;

import java.util.List;
import java.util.Properties;

public interface MatriculaService{
	public void setDriver(String dd);
	public void setDb(String data);
	public RegistreActiva getRegistreActiva(String dni);
}


