package es.udl.asic.component.app.directori;

import java.util.Properties;
import es.udl.asic.api.app.directori.*;
import es.udl.utilities.ldap.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;


public class MatriculaServiceImpl implements MatriculaService{
	
	private String REGISTRE_ALU = "SELECT ALU_DNIALU, EXP_NUMORD, LOGIN FROM udl_alulma_ldap_sakai WHERE ALU_DNIALU=? ORDER BY DAT_INS desc";
	String driver="";
	String db=""; 
	
	
	public RegistreActiva getRegistreActiva(String dni){
		//Obtenim la connexio
		Connection conn = getConnection();
		List <String> codi_matricula = null;
		String login=null;
		RegistreActiva rActiva = null;
		
		
		List llista = new ArrayList();
		CallableStatement call = null;
		
		try {
			call = conn.prepareCall(REGISTRE_ALU);

			// Carreguem els paràmetres
			call.setString(1,dni); //Anyaca
		
			// Carreguem les dades a la llista de retorn
			ResultSet rst = call.executeQuery();
			// Carreguem les dades a la llista de retorn
			
			// recuperem registres ordenats desc per data_ins
			// el primer ja ens serveix
			if (rst.next()) {
				login = rst.getString("LOGIN");
				codi_matricula = new ArrayList <String>(); 
				codi_matricula.add(rst.getString("EXP_NUMORD"));
				
				while (rst.next()){
					codi_matricula.add(rst.getString("EXP_NUMORD"));
				}
			}
			
			if (dni!=null && codi_matricula != null && !"".equals(dni) && !"".equals(codi_matricula)){
				rActiva = new RegistreActiva ();
				rActiva.setDni (dni);
				rActiva.setLogin(login);
				rActiva.setCodiMatricula(codi_matricula);
			}
			
			rst.close();
			call.close();
			
			
		}
		catch (Exception ex){
			System.out.println("Error al AL OBTENIR login");
			return null;
		}

		//Retornem la connexio
		returnConnection(conn);
		return rActiva;
	}
	
	
	
	/*Setter dels parametres driver i db*/
	public void setDriver(String dd) {
		driver = dd;
	}

	public void setDb(String data) {
		db = data;
	}

	
	//Obté una connexió del pool de connexions
			
	private Connection getConnection() {
		Connection conn = null;

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try{
			conn = DriverManager.getConnection(db);
		}catch (SQLException ex){
			System.out.println("No s'ha pogut obtenir la connexió");
		}
		
		return conn;
	}

	//Tanca la connexió solicitada
	
	private void returnConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println("Error al tancar la connexio");
		}
	}
	
}
