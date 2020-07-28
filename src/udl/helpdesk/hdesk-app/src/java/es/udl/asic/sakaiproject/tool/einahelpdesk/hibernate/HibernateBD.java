package es.udl.asic.sakaiproject.tool.einahelpdesk.hibernate;

//import net.sf.hibernate.HibernateException;
import org.hibernate.HibernateException;

//sakai22
import org.sakaiproject.user.cover.UserDirectoryService;
//import net.sf.hibernate.Query;
//import net.sf.hibernate.Session;
//import net.sf.hibernate.SessionFactory;

//import net.sf.hibernate.Transaction;
//import net.sf.hibernate.cfg.Configuration;

//import net.sf.hibernate.*; //Hibernate;

//Per la 2.2***************************
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import org.hibernate.Hibernate;
import org.hibernate.*;
import org.hibernate.dialect.Dialect;

//sakai22
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryProvider;
import org.sakaiproject.user.api.UserEdit;

//*************************************

import java.util.*;
import java.util.Date;

import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Assistencia;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Campus;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Tecnics;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Categoria;
import es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Edifici;

//Pel pool dbcp
import es.udl.asic.sakaiproject.service.einahelpdesk.HDeskService;

//per acces a bbdd
import org.apache.commons.dbcp.BasicDataSource;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;

//pel SQL SERVICE
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HibernateBD {

	private String dialect;

	String url;

	String driver_class;

	String username;

	String password;

	int num_assistencies;

	Configuration cfg=null;

	private HDeskService hdeskservice;

	// **********************************************************************
	public HibernateBD(HDeskService hd) {
		this.hdeskservice=hd;
		//System.out.println("Creadora HBD: "+this.hdeskservice+" i el parametre "+ hd);
	}

	// **********************************************************************
	public void setPropietats(String url, String driver, String user, String passwd, String dialect) {
		this.dialect = dialect;
		this.url = url;
		this.driver_class = driver;
		this.username = user;
		this.password = passwd;
		iniciarConnexio();
	}

	// **********************************************************************
	// Metode que emmagatzema el nombre total d'assistencies que retorna una consulta
	// Es per a que vagin mes rapides les consultes
	public void setNum_assistencies(int num_assistencies){
        	this.num_assistencies=num_assistencies;
	}

	// **********************************************************************
	// Metode que dona el nombre total d'assistencies que retorna una consulta
        // Es per a que vagin mes rapides les consultes
	public int getNum_assistencies(){
        	return num_assistencies;
	}

	// **********************************************************************
	public void iniciarConnexio() {
		try {
			Class.forName(driver_class);
			cfg = new Configuration();
			cfg.setProperty("hibernate.dialect", dialect);
			cfg.setProperty("hibernate.connection.url", url);
			cfg.setProperty("hibernate.connection.driver_class",driver_class);
			cfg.setProperty("hibernate.connection.username", username);
			cfg.setProperty("hibernate.connection.password", password);
	
			cfg.addClass(es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Assistencia.class);
			cfg.addClass(es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Campus.class);
			cfg.addClass(es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Tecnics.class);
			cfg.addClass(es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Categoria.class);
			cfg.addClass(es.udl.asic.sakaiproject.tool.einahelpdesk.mappings.Edifici.class);
					
			try {
				hdeskservice.setProps(cfg);
				//System.out.println("Ja li he enviat la configuracio");
			}
			catch(Exception w){
				w.printStackTrace();
			}
		}
		catch (Exception e) {
			System.err.print(e);
		}
	}

	// **********************************************************************
	public void setAssistencia(String usuari, String localitzacio,String telefon, String id_campus, String id_edifici, String id_categoria, String consulta, String data_inici, String solucio) {
	
	}

        // **********************************************************************
	public void AlterAssistencia(int ticket, int prioritat, int estat, int estat_activa, String id_tecnic, String seguiment, String seguiment_intern, int tipus_categoria) {
		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			Assistencia assistencia = new Assistencia();
			assistencia = getAssistencia(ticket," ");
			assistencia.setId_tecnic(id_tecnic);
			assistencia.setEstat(estat);
			assistencia.setEstat_activa(estat_activa);
			assistencia.setPrioritat(prioritat);
			assistencia.setId_categoria(tipus_categoria);
			// si es resolta o historica, ens interessa guardar la data d'avui
			// de finalitzacio
		
			if (estat == 1 || estat == 2)
				assistencia.setData_fi(new Date());
			if (seguiment.equals(""))
				;
			else {
				if (assistencia.getSolucio() == null)
					assistencia.setSolucio(seguiment); 			// aixo ho fem per no
									   			// inserir un null al
									   			// seguiment
				else	assistencia.setSolucio(assistencia.getSolucio() + seguiment);
			}
			
			if (seguiment_intern.equals(""))
                                ;
                        else {
				if (assistencia.getSolucio_interna() == null)
                                        assistencia.setSolucio_interna(seguiment_intern);       // aixo ho fem per no
                                                                                                // inserir un null al
                                                                                                // seguiment intern
                                else    assistencia.setSolucio_interna(assistencia.getSolucio_interna() + seguiment_intern);
			}

			//session.saveOrUpdateCopy(assistencia);
			session.merge(assistencia); // per la 2.2
			tx.commit();
			hdeskservice.closeSession(session);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

        // **********************************************************************
	public Assistencia getAssistencia(int ticket, String userId) {

		Assistencia assistencia = new Assistencia();
		try {
			String nomCampus, nomEdifici, nomTecnic, nomUsuari, nomEstat, nomCategoria, nomPrioritat;
			Session session=hdeskservice.getSession();
			
			String prioritats[] = { "Critica", "Normal", "Urgent" };// 0,1,2 per
										// ordenar
			String estats[] = { "Activa", "Resolta", "Històric" };	// 0,1,2 per
										// ordenar

			assistencia = (Assistencia) session.load(Assistencia.class, new Integer(ticket));
			// assistencia=(Assistencia) session.find("from Assistencia assis
			// where assis.ticket="+ticket);
			// hem de fer una altra consulta per agafar totes les dades
			// relacionades de les altres taules

			// sakai22
			// nomUsuari=""+UserDirectoryService.getUser(assistencia.getUsuari()).getDisplayName();
			nomUsuari = "";
			String nom = assistencia.getUsuari();
			try {
				// sakai22: el getUserByEid retorna de un login (nom) un usr i
				// d'aquest podem obtenir el seu nom complet
				User usr = UserDirectoryService.getUserByEid(nom);
				nomUsuari = usr.getDisplayName();
			} catch (Exception ex) {
				System.out.println("No s'ha pogut obtenir l'usuari");
			}

			// nomCampus=""+session.find("Select campus.nom from Campus campus
			// where campus.id_campus="+assistencia.getId_campus());
			nomCampus = ""+ session.createQuery("Select campus.nom from Campus campus where campus.id_campus="+ assistencia.getId_campus()).list();
			// nomEdifici=""+session.find("Select edifici.nom_edifici from
			// Edifici edifici where
			// edifici.id_edifici="+assistencia.getId_edifici());
			nomEdifici = ""+ session.createQuery("Select edifici.nom_edifici from Edifici edifici where edifici.id_edifici="+ assistencia.getId_edifici()).list();
			// nomTecnic=""+session.find("Select tecnic.nom from Tecnics tecnic
			// where tecnic.id_tecnic='"+assistencia.getId_tecnic()+"'");
			nomTecnic = ""+ session.createQuery("Select tecnic.nom from Tecnics tecnic where tecnic.id_tecnic='"+ assistencia.getId_tecnic() + "'").list();
			// nomCategoria=""+session.find("Select categoria.tipus from
			// Categoria categoria where
			// categoria.id_categoria="+assistencia.getId_categoria());
			nomCategoria = ""+ session.createQuery("Select categoria.tipus from Categoria categoria where categoria.id_categoria="+ assistencia.getId_categoria()).list();
			nomCampus = nomCampus.substring(nomCampus.indexOf("[") + 1,nomCampus.indexOf("]"));
			nomEdifici = nomEdifici.substring(nomEdifici.indexOf("[") + 1,nomEdifici.indexOf("]"));
			nomTecnic = nomTecnic.substring(nomTecnic.indexOf("[") + 1,nomTecnic.indexOf("]"));
			nomCategoria = nomCategoria.substring(nomCategoria.indexOf("[") + 1, nomCategoria.indexOf("]"));

			nomPrioritat = prioritats[assistencia.getPrioritat()];
			nomEstat = estats[assistencia.getEstat()];

			assistencia.setNom_campus(nomCampus);
			assistencia.setNom_edifici(nomEdifici);
			assistencia.setNom_tecnic(nomTecnic);
			assistencia.setNom_usuari(nomUsuari);
			assistencia.setNom_categoria(nomCategoria);
			assistencia.setNom_prioritat(nomPrioritat);
			assistencia.setNom_estat(nomEstat);
			assistencia.setStrData_inici(assistencia.getData_inici());
			assistencia.setStrData_fi(assistencia.getData_fi());
		
			//posem el camp 'estat_activa' a 0 per si de cas esta en vermell
			//es per al cas de que un usuari hagi fet una modificació (llavors es mostra l'assistencia en vermell)
			//i al veure-la el operador/responsable assignat s'ha de tornar a posar en negre.

			//Poso una condicio per a que sol es canvii l'estat si qui veu l'assistencia es qui la te assignada
			String id_tecnic=assistencia.getId_tecnic();
			if(id_tecnic.equals(userId)){
				Transaction tx = session.beginTransaction();
				assistencia.setEstat_activa(0);			
				session.merge(assistencia);
                        	tx.commit();
			}
 
			hdeskservice.closeSession(session);
		
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return assistencia;
	}

        // **********************************************************************
	public ArrayList getAssistenciesOperador(String userId, int isHistoric) {
		return getAssistenciesOperador(userId, "", "", isHistoric);
	}

        // **********************************************************************
	public ArrayList getAssistenciesOperador(String userId, String columna_sel,String paraula_clau, int isHistoric) {
		if (isHistoric==0) isHistoric=44; //per a posar primer les actives i despres les resoltes
		return getAssistenciesOperador(userId, columna_sel, paraula_clau,isHistoric, "assis.ticket", "DESC");
	}

        // **********************************************************************
	public ArrayList getAssistenciesOperador(String userId, String columna_sel,String paraula_clau, int estat, String columna_ordre, String sentit) {
		//Agafa totes les assistencies i les filtra per l'operador que les ha de veure
		ArrayList Totes = new ArrayList();
		ArrayList Filtrades = new ArrayList();
		Assistencia assistencia = new Assistencia();
			
		Totes = getAssistencies(columna_sel, paraula_clau, estat, columna_ordre, sentit);
		int index = 0;
		if (estat == 2)
			return Totes;
		else {
			while (index < Totes.size()) {
				assistencia = (Assistencia) Totes.get(index);
				// per a que els operadors vegin les seves assistencies
				if (assistencia.getId_tecnic().equals(userId)|| assistencia.getUsuari().equals(userId)){
					Filtrades.add(assistencia); // Les filtrem per campus
				}
				index++;
			}
			return Filtrades;
		}
	}

        // **********************************************************************
	public ArrayList getAssistenciesRespo(String userId, int campusId,int isHistoric) {
		// TODO: comprovar TOTS els campus del qual userid es responsable FALS, nomes ho sera d'un
		return getAssistenciesRespo(userId, campusId, "", "", isHistoric);
	}

        // **********************************************************************
	public ArrayList getAssistenciesRespo(String userId, int campusId,String columna_sel, String paraula_clau, int isHistoric) {
		if (isHistoric==0) isHistoric=44; //per a posar primer les actives i despres les resoltes
		return getAssistenciesRespo(userId, campusId, columna_sel, paraula_clau, isHistoric, "assis.ticket", "DESC");
	}

        // **********************************************************************
	public ArrayList getAssistenciesRespo(String userId, int campusId, String columna_sel, String paraula_clau, int estat, String columna_ordre, String sentit) {
		ArrayList Totes = new ArrayList();
		ArrayList Filtrades = new ArrayList();
		Assistencia assistencia = new Assistencia();
		Totes = getAssistencies(columna_sel, paraula_clau, estat, columna_ordre, sentit);
		int index = 0;
		if (estat == 2)
			return Totes;
		else {
			while (index < Totes.size()) {
				assistencia = (Assistencia) Totes.get(index);
				if (assistencia.getId_campus() == campusId || assistencia.getId_tecnic().equals(userId))
					Filtrades.add(assistencia); // Les filtrem per campus
				index++;
			}

			return Filtrades;
		}
	}

        // **********************************************************************
	public int getCampusResponsable(String userid) {
		int campus = 0;
		String strCampus;
		try {
			Session session=hdeskservice.getSession();
			
			// per al sakai2.2
			// strCampus="" + session.find("Select campus.id_campus from Campus
			// campus where campus.responsable='"+userid+"'");
			strCampus = ""+ session.createQuery("Select campus.id_campus from Campus campus where campus.responsable='"+ userid + "'").list();
			strCampus = strCampus.substring(strCampus.indexOf("[") + 1,strCampus.indexOf("]"));
			campus = Integer.parseInt(strCampus);
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return campus;
	}

        // **********************************************************************
	public ArrayList getAssistenciesPAS(String userId, String columna_sel, String paraula_clau, int isHistoric) {
		if (isHistoric==0) 	return getAssistenciesPAS(userId, columna_sel, paraula_clau, 3, "assis.ticket", "DESC");
		else			return getAssistenciesPAS(userId, columna_sel, paraula_clau, 4, "assis.ticket", "DESC");
	}

        // **********************************************************************
	public ArrayList getAssistenciesPAS(String userId, int isHistoric) {
		return getAssistenciesPAS(userId, "", "", isHistoric);
	}

        // **********************************************************************
	public ArrayList getAssistenciesPAS(String userId, String columna_sel, String paraula_clau, int estat, String columna_ordre, String sentit) {
		ArrayList Totes = new ArrayList();
		ArrayList Filtrades = new ArrayList();
		Assistencia assistencia = new Assistencia();
		Totes = getAssistencies(columna_sel, paraula_clau, estat, columna_ordre, sentit, userId, "paspdi",0,0);
		int index = 0;
		while (index < Totes.size()) {
			assistencia = (Assistencia) Totes.get(index);
			if (assistencia.getUsuari().equals(userId))Filtrades.add(assistencia); // Les filtrem per userId
			index++;
		}
		return Filtrades;
	}

        // **********************************************************************
	public ArrayList getAssistencies(int estat) {
		if (estat==0)	estat=44; //per a posar primer les actives i despres les resoltes
		return getAssistencies("", "", estat);
	}

	// **********************************************************************
        public ArrayList getAssistencies(int estat, int midapagina, int numdepagina) {
                if (estat==0)   estat=44; //per a posar primer les actives i despres les resoltes
                return getAssistencies("", "", 2, "assis.ticket", "DESC", midapagina, numdepagina);
        }	

        // **********************************************************************
	// Assistencies del support agent
	//
	public ArrayList getAssistencies(String columna_sel, String paraula_clau, int estat) {
		return getAssistencies(columna_sel, paraula_clau, estat, "assis.ticket", "DESC");
	}

        // **********************************************************************
	// Retorna la llista d'assistencies segons criteris de cerca
	//
			
	// **********************************************************************
	// Per a que vagin mes rapides les consultes de l'historic perfil suport/responsable/operador
	public ArrayList getAssistencies( String columna_sel, String paraula_clau, int estat, String columna_ordre, String sentit, int midapagina, int numdepagina){
		return getAssistencies(columna_sel, paraula_clau, estat, columna_ordre, sentit, "", "", midapagina, numdepagina);
	}

        // **********************************************************************
        // Per a que vagin mes rapides les consultes de l'historic perfil suport/responsable/operador, quan ordenem per columna
	public ArrayList getAssistenciesHistoric(String columna_sel, String paraula_clau, int estat, String columna_ordre, String sentit, int midapagina, int numdepagina){
		return getAssistencies(columna_sel, paraula_clau, estat, columna_ordre, sentit, "", "", midapagina, numdepagina);
	}

        // **********************************************************************
	// Per a que vagin mes rapides les consultes en el perfil pas/pdi
	public ArrayList getAssistencies(String columna_sel, String paraula_clau, int estat, String columna_ordre, String sentit){
                return getAssistencies( columna_sel,  paraula_clau,  estat,  columna_ordre,  sentit, "", "", 0, 0);
	}

        // **********************************************************************
	public ArrayList getAssistencies(String columna_sel, String paraula_clau, int estat, String columna_ordre, String sentit, String userId, String pfl, int midapagina, int numdepagina){

		ArrayList assistencies = new ArrayList();
		String prioritats[] = { "Critica", "Normal", "Urgent" };// 0,1,2 per
									// ordenar
		String estats[] = { "Activa", "Resolta", "Històric" };// 0,1,2 per
								      // ordenar
		
		try {
			Session session=hdeskservice.getSession();
					
			Transaction tx = session.beginTransaction();
			Iterator iter = null;
			String estat_assistencia = "";
			String nomCampus, nomEdifici, nomTecnic, nomUsuari, nomEstat, nomCategoria, nomPrioritat;
			Assistencia assis = new Assistencia();
			if (estat == 0 || estat == 1 || estat==44)	//estat=44 vol dir que volem ordenar primer les actives i despres les resoltes
				estat_assistencia = "(assis.estat=0 OR assis.estat=1)";	// Actives i Resoltes
			else if (estat == 2)
				estat_assistencia = "assis.estat=2";			// Historiques
			else if (estat == 3)
				estat_assistencia = "(assis.estat=0 OR assis.estat=1)"; // Totes actives i resoltes pas/pdi, no paginades
			else if (estat == 4)
				estat_assistencia = "assis.estat=2"; 			// Totes historiques del pas/pdi, no paginades

			String base = "Select assis from Assistencia assis, Campus campus, Edifici edifici, Tecnics tecnic, Categoria categoria where "
					+ estat_assistencia;
			
			String base1="SELECT count(*) FROM Assistencia assis, Campus campus, Edifici edifici, Tecnics tecnic, Categoria categoria where "
                                        + estat_assistencia;	//Per guanyar rapidesa a l'historic

			int index = 0;
			
			//Tracto la cadena paraula_clau per a treure les cometes simples
			paraula_clau= paraula_clau.replaceAll("'","''");
			
			if (!columna_sel.equals("") && !paraula_clau.equals("")) {
				// Hem de buscar primer les relacions id amb nom de les altres taules
				
				//tots aquests if s'executen quan triem l'opcio de cercar al listbox i NO quan premem la columna
				if (columna_sel.equals("ticket")){	
					iter = session.createQuery(base	+ " AND assis.ticket like '%" + paraula_clau + "%' and assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici AND assis.id_categoria=categoria.id_categoria and assis.id_tecnic=tecnic.id_tecnic order by  " + columna_ordre + " " + sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}

				else if (columna_sel.equals("data_inici")){
					iter = session.createQuery(base + " AND assis.id_campus=campus.id_campus AND assis.id_edifici=edifici.id_edifici AND assis.id_tecnic=tecnic.id_tecnic AND assis.id_categoria=categoria.id_categoria AND assis.data_inici like '%" + paraula_clau + "%' order by " + columna_ordre + " " + sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}

				else if (columna_sel.equals("id_campus")){
					iter = session.createQuery(base + " AND assis.id_campus=campus.id_campus AND assis.id_edifici=edifici.id_edifici and assis.id_tecnic=tecnic.id_tecnic AND assis.id_categoria=categoria.id_categoria AND upper(campus.nom) like upper('%" + paraula_clau + "%') order by " + columna_ordre + " " + sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}

				else if (columna_sel.equals("id_edifici")){
					iter = session.createQuery(base + " AND assis.id_campus=campus.id_campus AND assis.id_edifici=edifici.id_edifici and assis.id_tecnic=tecnic.id_tecnic AND assis.id_categoria=categoria.id_categoria AND upper(edifici.nom_edifici) like upper('%" + paraula_clau + "%') order by "+ columna_ordre + " " + sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}

				else if (columna_sel.equals("id_categoria")){
					iter = session.createQuery(base	+ " AND assis.id_campus=campus.id_campus AND assis.id_edifici=edifici.id_edifici and assis.id_tecnic=tecnic.id_tecnic AND assis.id_categoria=categoria.id_categoria and upper(categoria.tipus) like upper('%" + paraula_clau + "%') order by " + columna_ordre + " " + sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}

				else if (columna_sel.equals("id_tecnic")){
					iter = session.createQuery(base	+ " AND assis.id_tecnic=tecnic.id_tecnic AND assis.id_campus=campus.id_campus AND assis.id_categoria=categoria.id_categoria AND assis.id_edifici=edifici.id_edifici AND upper(tecnic.nom) like upper('%" + paraula_clau + "%') order by " + columna_ordre + " " + sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}

				else if (columna_sel.equals("prioritat")) {
					// comparem la prioritat amb el array que hem creat
					index = 0;
					String consulta_prior = "AND (assis.prioritat=15";
					while (index < 3) {
						if (prioritats[index].toUpperCase().indexOf(paraula_clau.toUpperCase()) != -1)
							consulta_prior = consulta_prior	+ " OR assis.prioritat=" + index;
						index++;
					}
					consulta_prior = consulta_prior + ")";
					// per al sakai 2.2
					/*
					 * iter=session.iterate(base+" AND "+estat_assistencia+" and
					 * assis.id_campus=campus.id_campus and
					 * assis.id_edifici=edifici.id_edifici and
					 * assis.id_categoria=categoria.id_categoria and
					 * assis.id_tecnic=tecnic.id_tecnic "+consulta_prior+ "
					 * order by "+columna_ordre+" "+sentit);
					 */
					iter = session.createQuery(base + " AND " + estat_assistencia + " and assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici and assis.id_categoria=categoria.id_categoria and assis.id_tecnic=tecnic.id_tecnic " + consulta_prior + " order by " + columna_ordre + " " + sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				} 

				else if (columna_sel.equals("estat")) {
					// comparem estat amb el array que hem creat
					index = 0;
					int index_array = 3;
					if (estat == 0 || estat == 1)
						index_array = 2;
					else
						;// no hi pot arrivar pq la columna d'estat desapareix.

					String consulta_estat = "AND (assis.estat=15";
					while (index < index_array) {
						if (estats[index].toLowerCase().indexOf(paraula_clau.toLowerCase()) != -1)
							consulta_estat = consulta_estat+ " OR assis.estat=" + index;
						index++;
					}
					consulta_estat = consulta_estat + ")";
					// per al sakai 2.2
					/*
					 * iter=session.iterate(base+" AND
					 * assis.id_campus=campus.id_campus and
					 * assis.id_edifici=edifici.id_edifici and
					 * assis.id_categoria=categoria.id_categoria and
					 * assis.id_tecnic=tecnic.id_tecnic "+consulta_estat+" order
					 * by "+columna_ordre+" "+sentit);
					 */
				 	iter = session.createQuery(base	+ " AND assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici and assis.id_categoria=categoria.id_categoria and assis.id_tecnic=tecnic.id_tecnic " + consulta_estat + " order by " + columna_ordre + " " + sentit).iterate(); 
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}

				else if (columna_sel.equals("descripcio_seguiment")){ //quan al desplegable escollim l'opcio de Descripcio/Seguiment

					iter=session.createQuery(base+" AND assis.id_tecnic=tecnic.id_tecnic AND assis.id_campus=campus.id_campus AND assis.id_categoria=categoria.id_categoria AND assis.id_edifici=edifici.id_edifici AND (upper(assis.consulta) like upper('%"+paraula_clau+"%') OR upper(assis.solucio) like upper('%"+paraula_clau+"%'))  order by "+columna_ordre+" "+sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}

				else if (columna_sel.equals("descripcio_seguiment_intern")){ //quan al desplegable escollim l'opcio de Descripcio/Seguiment

                                        iter=session.createQuery(base+" AND assis.id_tecnic=tecnic.id_tecnic AND assis.id_campus=campus.id_campus AND assis.id_categoria=categoria.id_categoria AND assis.id_edifici=edifici.id_edifici AND (upper(assis.consulta) like upper('%"+paraula_clau+"%') OR upper(assis.solucio) like upper('%"+paraula_clau+"%') OR upper(assis.solucio_interna) like upper('%"+paraula_clau+"%'))  order by "+columna_ordre+" "+sentit).iterate();
                                        if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
                                }


				else if (columna_sel.equals("usuari")) {
					// per al sakai22
					// Iterator iter2=session.iterate("Select assis.usuari from
					// Assistencia assis");
					Iterator iter2 = session.createQuery("Select assis.usuari from Assistencia assis").iterate();
					String usuari;
					String nom_complet;
					String consulta = "AND (assis.usuari='nobody'"; // construirem la consulta definitiva
					while (iter2.hasNext()) {
						usuari = (String) iter2.next();

						// sakai22
						// System.out.println("usuari:" +usuari);
						// nom_complet=""+UserDirectoryService.getUser(usuari).getDisplayName().toLowerCase();
						nom_complet = "";
						// String nom = assis.getUsuari();
						try {
							// sakai22: el getUserByEid retorna de un login
							// (nom) un usr i d'aquest podem obtenir el seu nom
							// complet
							User usr = UserDirectoryService.getUserByEid(usuari);
							nom_complet = usr.getDisplayName().toLowerCase();
						} catch (Exception ex) {
							System.out.println("No s'ha pogut obtenir l'usuari");
						}
						
						if (nom_complet.indexOf(paraula_clau.toLowerCase()) != -1) {
							if (consulta.indexOf(usuari) != -1);
							else
								consulta = consulta + " OR assis.usuari='" + usuari + "'";
						}

					}
					consulta = consulta + " )";

					// System.out.println("\n\n Consulta: "+consulta+"\n\n");

					// per al sakai22
					// iter=session.iterate("from Assistencia assis where
					// "+estat_assistencia+" "+consulta+" order by
					// "+columna_ordre+" "+sentit);
					iter = session.createQuery(base + " AND " + estat_assistencia + " AND assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici and assis.id_categoria=categoria.id_categoria and assis.id_tecnic=tecnic.id_tecnic " + consulta + " order by " + columna_ordre + " " + sentit).iterate();
					if(estat==2){/*cerca pel listbox i estic a l'historic, no he d'utilitzar la variable*/  setNum_assistencies(-1);}
				}
			}
			
			// en el cas que no volem cercar per cap camp i en l'ordenacio de les columnes
			else {	

				if(estat==44){		// Per a fer lo de actives primer i resoltes al final
 					iter = session.createQuery(base + " AND assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici and assis.id_categoria=categoria.id_categoria and assis.id_tecnic=tecnic.id_tecnic AND (assis.estat=0 OR assis.estat=1) order by assis.estat asc," + columna_ordre + " " + sentit).iterate();
				}
				
				else if(pfl=="paspdi"){	// Per a que vagi mes rapid la consulta d'assistencies del paspdi
					iter = session.createQuery("Select assis from Assistencia assis, Campus campus, Edifici edifici, Tecnics tecnic, Categoria categoria where assis.id_tecnic=tecnic.id_tecnic AND assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici AND assis.id_categoria=categoria.id_categoria AND " + estat_assistencia +" AND assis.usuari='"+userId+ "' order by " + columna_ordre + " " + sentit).iterate();
				setNum_assistencies(-1); //no vull paginar
				}
				
				// Per a que vagin mes rapides les consultes de l'historic perfil suport/responsable/operador
				else if(estat==2 && columna_ordre!="assis.usuari"){ 
					Query q = session.createQuery("Select assis from Assistencia assis, Campus campus, Edifici edifici, Tecnics tecnic, Categoria categoria where assis.id_tecnic=tecnic.id_tecnic AND assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici AND assis.id_categoria=categoria.id_categoria AND " + estat_assistencia + "  order by " + columna_ordre + " " + sentit+", assis.ticket DESC");

					// Conta els registres de la Query q i els passo a la variable num_assistencies
					Long count = (Long) session.createQuery("Select count(*) from Assistencia assis, Campus campus, Edifici edifici, Tecnics tecnic, Categoria categoria where assis.id_tecnic=tecnic.id_tecnic AND assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici AND assis.id_categoria=categoria.id_categoria AND " + estat_assistencia + "  order by " + columna_ordre + " " + sentit+", assis.ticket DESC").uniqueResult();

					int count1 = count.intValue();
					setNum_assistencies(count1);	// Emmagatzemo el nombre d'assistencies que tinc
					if (midapagina>0){		//midapagina = si volem 5 assistencies per pagina, 10, 20 o 50
									//numdepagina = per defecte 1, indica la pagina on estic de les assistencies
                               		q.setMaxResults(midapagina);
                             		q.setFirstResult((midapagina * numdepagina)-midapagina);
                        		}
                        		List page = q.list();
                        		iter = page.iterator();
                        	}
			
				else if(estat==2 && columna_ordre=="assis.usuari"){	//si ordeno per nom d'usuari (es una consulta especial, ja que,
											//el nom de l'usuari no esta a la BD) vul fer aquesta consulta
					iter = session.createQuery("Select assis from Assistencia assis, Campus campus, Edifici edifici, Tecnics tecnic, Categoria categoria where assis.id_tecnic=tecnic.id_tecnic AND assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici AND assis.id_categoria=categoria.id_categoria AND " + estat_assistencia + "  order by " + columna_ordre + " " + sentit +", assis.ticket DESC").iterate();
                        		setNum_assistencies(-1);
				}
			
				else{
					iter = session.createQuery("Select assis from Assistencia assis, Campus campus, Edifici edifici, Tecnics tecnic, Categoria categoria where assis.id_tecnic=tecnic.id_tecnic AND assis.id_campus=campus.id_campus and assis.id_edifici=edifici.id_edifici AND assis.id_categoria=categoria.id_categoria AND " + estat_assistencia + "  order by " + columna_ordre + " " + sentit).iterate();
				}
			}		

			while (iter.hasNext()) {
				assis = (Assistencia) iter.next();
				// hem de fer una altra consulta per agafar totes les dades
				// relacionades de les altres taules

				// sakai22
				nomUsuari = "";
				// nomUsuari=""+UserDirectoryService.getUser(assis.getUsuari()).getDisplayName();
				String nom = assis.getUsuari();

				try {
					// sakai22: el getUserByEid retorna de un login (nom) un usr
					// i d'aquest podem obtenir el seu nom complet
					User usr = UserDirectoryService.getUserByEid(nom);
					nomUsuari = usr.getDisplayName();
				} catch (Exception ex) {
					System.out.println("No s'ha pogut obtenir l'usuari");
				}

				// nomCampus=""+session.find("Select campus.nom from Campus
				// campus where campus.id_campus="+assis.getId_campus());
				nomCampus = "" + session.createQuery("Select campus.nom from Campus campus where campus.id_campus=" + assis.getId_campus()).list();

				// nomEdifici=""+session.find("Select edifici.nom_edifici from
				// Edifici edifici where
				// edifici.id_edifici="+assis.getId_edifici());
				nomEdifici = ""	+ session.createQuery("Select edifici.nom_edifici from Edifici edifici where edifici.id_edifici=" + assis.getId_edifici()).list();

				// nomTecnic=""+session.find("Select tecnic.nom from Tecnics
				// tecnic where tecnic.id_tecnic='"+assis.getId_tecnic()+"'");
				nomTecnic = "" + session.createQuery("Select tecnic.nom from Tecnics tecnic where tecnic.id_tecnic='" + assis.getId_tecnic() + "'").list();

				// nomCategoria=""+session.find("Select categoria.tipus from
				// Categoria categoria where
				// categoria.id_categoria="+assis.getId_categoria());
				nomCategoria = "" + session.createQuery("Select categoria.tipus from Categoria categoria where categoria.id_categoria="	+ assis.getId_categoria()).list();
				nomCampus = nomCampus.substring(nomCampus.indexOf("[") + 1, nomCampus.indexOf("]"));
				nomEdifici = nomEdifici.substring(nomEdifici.indexOf("[") + 1, nomEdifici.indexOf("]"));
				nomTecnic = nomTecnic.substring(nomTecnic.indexOf("[") + 1, nomTecnic.indexOf("]"));
				nomCategoria = nomCategoria.substring(nomCategoria.indexOf("[") + 1, nomCategoria.indexOf("]"));

				nomPrioritat = prioritats[assis.getPrioritat()];
				nomEstat = estats[assis.getEstat()];

				assis.setNom_campus(nomCampus);
				assis.setNom_edifici(nomEdifici);
				assis.setNom_tecnic(nomTecnic);
				assis.setNom_usuari(nomUsuari);
				assis.setNom_categoria(nomCategoria);
				assis.setNom_prioritat(nomPrioritat);
				assis.setNom_estat(nomEstat);
				assis.setStrData_inici(assis.getData_inici());
				assistencies.add(assis);
			}
			
			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return assistencies;
	}

	// **********************************************************************
	public void setOperador(String id_tecnic, int accio) {
	}

        // **********************************************************************
	// Metode per comprovar si l'usuari es un operador o un responsable de campus
	public String checkUser(String userid) {
		String perfil = "";
		try {

			Session session=hdeskservice.getSession();
			//conn);
			// per al sakai 2.2
			// Iterator iter = session.iterate("from Campus campus where
			// campus.responsable='"+userid+"'");
			Iterator iter = session.createQuery("from Campus campus where campus.responsable='" + userid + "'").iterate();
			if (iter.hasNext()) {
				perfil = "responsable";
			} else {
				// per al sakai 2.2
				// iter = session.iterate("from Tecnics tecnics where
				// tecnics.id_tecnic='"+userid+"'");
				iter = session.createQuery("from Tecnics tecnics where tecnics.id_tecnic='" + userid + "'").iterate();
				if (iter.hasNext()) {
					perfil = "operador";
				}
			}
			// process the user object here
			hdeskservice.closeSession(session);
						
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return perfil;
	}

        // **********************************************************************
	public Vector getResponsables() {
		Vector responsables = new Vector();
		Campus responsable = new Campus(); // sakai22
		String respon = "";

		try {
			Session session=hdeskservice.getSession();
			
			// per al sakai 2.2
			// Iterator iter=session.iterate("from Campus campus order by
			// campus.nom");
			Iterator iter = session.createQuery("from Campus campus order by campus.nom").iterate();

			while (iter.hasNext()) {
				// sakai22, s'ha de fer així, o sino, no funciona, amb el
				// System.out inclós
				// responsables.add((Campus) iter.next());
				responsable = (Campus) iter.next();
				// System.out.println("GETRESPONSABLES, responsable:
				// "+responsable);
				responsables.add((Campus) responsable);
				respon = responsable.getNom(); // aquesta variable no serveix
							       // per a rés, fa que funcioni el
							       // codi sense fer el System.out
			}
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responsables;
	}

        // **********************************************************************
	public Vector getOperadors() {
		Vector operadors = new Vector();
		Tecnics tecnic = new Tecnics(); // sakai22
		String nomoper = "";
		try {
			Session session=hdeskservice.getSession();
			
			// per al sakai 2.2
			// Iterator iter=session.iterate("from Tecnics tecnics order by
			// nom");
			Iterator iter = session.createQuery("from Tecnics tecnics order by nom").iterate();
			
			// sakai22
			while (iter.hasNext()) {
				// sakai22, s'ha de fer així, o sino, no funciona, amb el
				// System.out inclós
				 //operadors.add((Tecnics) iter.next());
				tecnic = (Tecnics) iter.next();
				// Thread.sleep(500);
				operadors.add((Tecnics) tecnic);
				nomoper = tecnic.getNom(); // aquesta variable no serveix per a
							   // rés, fa que funcioni el codi
							   // sense fer el System.out
				 //System.out.println("GETOPERADORS, tecnic:"+nomoper+" __ "+ tecnic);
			}
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return operadors;
	}
	
	// mètode per recuperar només els operadors actius
	public Vector getOperadorsActius() {
		Vector operadors = new Vector();
		Tecnics tecnic = new Tecnics(); // sakai22
		String nomoper = "";
		try {
			Session session=hdeskservice.getSession();
			
			// per al sakai 2.2
			// Iterator iter=session.iterate("from Tecnics tecnics order by
			// nom");
			Iterator iter = session.createQuery("from Tecnics tecnics where tecnics.activat=1 order by nom").iterate();
			
			// sakai22
			while (iter.hasNext()) {
				// sakai22, s'ha de fer així, o sino, no funciona, amb el
				// System.out inclós
				 //operadors.add((Tecnics) iter.next());
				tecnic = (Tecnics) iter.next();
				// Thread.sleep(500);
				operadors.add((Tecnics) tecnic);
				nomoper = tecnic.getNom(); // aquesta variable no serveix per a
							   // rés, fa que funcioni el codi
							   // sense fer el System.out
				 //System.out.println("GETOPERADORS, tecnic:"+nomoper+" __ "+ tecnic);
			}
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return operadors;
	}

        // **********************************************************************
	public String getOperador(String tecnics_sel) {
		String nom_oper = "";
		try {
			Session session=hdeskservice.getSession();
			
			// per al sakai 2.2
			// Iterator iter=session.iterate("from Tecnics tecnics where
			// tecnics.id_tecnic='"+tecnics_sel+"'");
			Iterator iter = session.createQuery("from Tecnics tecnics where tecnics.id_tecnic='" + tecnics_sel + "'").iterate();
			Transaction tx = session.beginTransaction();

			if (iter.hasNext()) {
				Tecnics tecnic = new Tecnics();
				tecnic = (Tecnics) iter.next();
				nom_oper = tecnic.getNom();
			}
			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nom_oper;
	}

        // **********************************************************************
	public void Bescanviar(String campus_sel, String tecnics_sel) {
		try {
			Session session=hdeskservice.getSession();
			
			String nom = "";
			Transaction tx = session.beginTransaction();
			// substituim el responsable per l'operador
			// per al sakai 2.2
			// Iterator iter = session.iterate("from Campus campus where
			// campus.id_campus='"+campus_sel+"'");
			Iterator iter = session.createQuery("from Campus campus where campus.id_campus='" + campus_sel + "'").iterate();
			if (iter.hasNext()) {
				Campus camp = (Campus) iter.next();
				nom = camp.getNom();// nom del campus
				int cc_c = Integer.parseInt(campus_sel);
				camp.setId_campus(cc_c);
				camp.setResponsable(tecnics_sel);
				// per al sakai 2.2
				// session.saveOrUpdateCopy(camp);
				session.merge(camp);
			}
			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

        // **********************************************************************
	public ArrayList getCategories() {
		ArrayList elements = new ArrayList();
		try {
			String descripcio = "";
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			// per al sakai 2.2
			// Iterator iter = session.iterate("from Categoria categoria order
			// by categoria.tipus");
			Iterator iter = session.createQuery("from Categoria categoria order by categoria.tipus").iterate();

			Categoria categoria = new Categoria();
			while (iter.hasNext()) {
				// categoria = (Categoria)iter.next();
				// elements.add(categoria);

				// sakai22, s'ha de fer així, o sino, no funciona, amb el
				// System.out inclós
				categoria = (Categoria) iter.next();
				// System.out.println("GETCATEGORIES, categoria: "+categoria);
				elements.add(categoria);
				descripcio = categoria.getDescripcio(); // aquesta variable no serveix per a rés, fa
									// que funcioni el codi sense fer el System.out
			}
			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return elements;
	}

        // **********************************************************************
	public String getDescripcio(String categ_sel) {
		String descripcio = "";
		// System.out.println("CAtegoria seleccionada al començamnent:"+categ_sel);

		// int cat_sel_int=Integer.parseInt(categ_sel);
		try {
			if (categ_sel == null || categ_sel == "")
				; // en el cas de que tinguem els ... seleccionats
			else {
				int cat_sel_int = Integer.parseInt(categ_sel);
				Session session=hdeskservice.getSession();
				
				Transaction tx = session.beginTransaction();
				// per al sakai 2.2
				// Iterator iter = session.iterate("from Categoria categoria
				// where categoria.id_categoria="+cat_sel_int);
				Iterator iter = session.createQuery("from Categoria categoria where categoria.id_categoria=" + cat_sel_int).iterate();
				Categoria categoria = new Categoria();
				if (iter.hasNext()) {
					categoria = (Categoria) iter.next();
					descripcio = categoria.getDescripcio();
				}
				tx.commit();
				hdeskservice.closeSession(session);
				
				//hdeskservice.closeConnection(conn);
				//conn.close();
				//conn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return descripcio;
	}

        // **********************************************************************
	// Modifica un tipus de la taula categories
	//
	public void setTipus(String categ_sel, String descripcio) {
		int cat_sel_int = Integer.parseInt(categ_sel);
		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			// per al sakai 2.2
			String tipus = ""+ session.createQuery("Select categoria.tipus from Categoria categoria where categoria.id_categoria=" + cat_sel_int).list();

			tipus = tipus.substring(tipus.indexOf("[") + 1, tipus.indexOf("]"));
			// Iterator iter = session.iterate("from Categoria categoria where
			// categoria.id_categoria="+cat_sel_int);
			Categoria categoria = new Categoria();
			// if (iter.hasNext()) {
			categoria.setId_categoria(cat_sel_int);
			categoria.setTipus(tipus);
			categoria.setDescripcio(descripcio);
			// per al sakai2.2
			// session.saveOrUpdateCopy(categoria);
			session.merge(categoria);
			// }
			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

        // **********************************************************************
	// Esborra un determinat tipus de la taula de categories
	//
	public void delTipus(String categ_sel) {
		int cat_sel_int = Integer.parseInt(categ_sel);
		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			// sakai 2.2, afegit el DELETE a la consulta
			String sel = "DELETE FROM Categoria AS categorias WHERE categorias.id_categoria = "+ cat_sel_int;

			// sakai 2.2, session.delete -> deprecated
			// session.delete(sel);
			session.createQuery(sel).executeUpdate();

			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

        // **********************************************************************
	// Introdueix un nou tipus a la taula de categories
	//
	public void crearTipus(String nou_tipus, String nova_descripcio) {
		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			Categoria categoria = new Categoria();
			categoria.setTipus(nou_tipus);
			categoria.setDescripcio(nova_descripcio);
			session.save(categoria);
			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// **********************************************************************
	public void BDEsborraOperador(String tecnic_sel) {
		// Metode que esborra a la BD l'operador sel.leccionat a
		// (gest_operadors.jsp)

		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			// sakai2.2, afegit el DELETE a la consulta
			String sel = "DELETE FROM Tecnics AS tecnic WHERE tecnic.id_tecnic = '"	+ tecnic_sel + "'";

			// sakai 2.2, session.delete -> deprecated
			// session.delete(sel);
			session.createQuery(sel).executeUpdate();

			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void BDDesactivaOperador(String tecnic_sel) {
		// Metode que desactiva a la BD l'operador sel.leccionat a
		// (gest_operadors.jsp)

		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			// sakai2.2, afegit el DELETE a la consulta
			String sel = "UPDATE Tecnics SET activat = 0 WHERE id_tecnic = '"	+ tecnic_sel + "'";

			// sakai 2.2, session.delete -> deprecated
			// session.delete(sel);
			session.createQuery(sel).executeUpdate();

			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void BDActivaOperador(String tecnic_sel) {
		// Metode que activa a la BD l'operador sel.leccionat a
		// (gest_operadors.jsp)

		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			// sakai2.2, afegit el DELETE a la consulta
			String sel = "UPDATE Tecnics SET activat = 1 WHERE id_tecnic = '"	+ tecnic_sel + "'";

			// sakai 2.2, session.delete -> deprecated
			// session.delete(sel);
			session.createQuery(sel).executeUpdate();

			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	// **********************************************************************
	public void BDCreaOperador(String id_operador, String nom_operador) {
		// Metode que insereix a la BD un operador nou (gest_operadors.jsp)

		try {
			//Connection conn = hdeskservice.getConnection();
			Session session=hdeskservice.getSession();
			Transaction tx = session.beginTransaction();

			Tecnics operador = new Tecnics();
			operador.setId_tecnic(id_operador);
			operador.setNom(nom_operador);
			// el creem com a actiu
			int activat = 1;
			operador.setActivat(activat);

			// per al sakai 2.2
			// session.saveOrUpdateCopy(operador);
			session.merge(operador);

			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// **********************************************************************
	public Vector getLlistaCampus() {
		// Metode que extreu la llista de tots els campus per a una listbox
		Vector campus = new Vector();
		Campus camp = new Campus(); // sakai22
		String nomcampus = "";

		try {
			Session session=hdeskservice.getSession();
			
			// per a sakai 2.2
			// Iterator iter=session.iterate("from Campus campus order by
			// campus.nom");
			Iterator iter = session.createQuery("from Campus campus where campus.activat=1 order by campus.nom").iterate();
			while (iter.hasNext()) {
				// campus.add((Campus) iter.next());
				// sakai22, s'ha de fer així, o sino, no funciona, amb el
				// System.out inclós
				camp = (Campus) iter.next();
				// System.out.println("GETLLISTACAMPUS, campus: "+camp);
				campus.add((Campus) camp);
				nomcampus = camp.getNom(); 	// aquesta variable no serveix per a
								// rés, fa que funcioni el codi
			    					// sense fer el System.out

			}
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return campus;
	}

	// **********************************************************************
	public Vector getLlistaEdificis(String campus_selec_int) {
		// Metode que extreu la llista de tots els edificis D'UN CAMPUS SEL.LECCIONAT
		Vector edifici = new Vector();
		Edifici edif = new Edifici(); // sakai22
		String nomedifici = "";

		try {
			if (campus_selec_int == null || campus_selec_int == "")
				; // en el cas de que tinguem els ... seleccionats
			else {
				Session session=hdeskservice.getSession();
				
				// per a sakai 2.2
				// Iterator iter=session.iterate("from Edifici edifici where
				// edifici.id_campus="+campus_selec_int+" order by
				// edifici.nom_edifici");
				Iterator iter = session.createQuery("from Edifici edifici where edifici.id_campus=" + campus_selec_int + " order by edifici.nom_edifici").iterate();
				while (iter.hasNext()) {
					// edifici.add((Edifici) iter.next());
					// sakai22, s'ha de fer així, o sino, no funciona, amb el
					// System.out inclós
					edif = (Edifici) iter.next();
					// System.out.println("GETLLISTAEDIFICIS, edifici: "+edif);
					nomedifici = edif.getNom_edifici(); 	// aquesta variable no serveix per a rés, fa
										// que funcioni el codi sense fer el System.out
					edifici.add((Edifici) edif);
				}
				hdeskservice.closeSession(session);
				
				//hdeskservice.closeConnection(conn);
				//conn.close();
				//conn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return edifici;
	}

	// **********************************************************************
	public String BDBuscaResponsable(String campus_selec) {
		// Metode que busca el nom del responsable de un determinat campus
		String id_tecnic = "";

		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			// per al sakai 2.2
			id_tecnic = ""+ session.createQuery("Select campus.responsable from Campus campus where campus.id_campus="+ campus_selec).list();

			// per eliminar els [ ] del id_tecnic
			id_tecnic = id_tecnic.substring(id_tecnic.indexOf("[") + 1,id_tecnic.indexOf("]"));

			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id_tecnic;
	}

	// **********************************************************************
	public void BDCreaAssistencia(String campus_selec, String categoria_selec,
			String consulta, int prioritat, Date data_inici, int estat, int estat_activa,
			String usuari, String id_tecnic, String telefon, String despatx,
			String edifici_selec, String codi_udl) {
		// Metode que insereix a la taula Assistencia de la BD una nova
		// assistencia

		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();

			Assistencia assistencia = new Assistencia();
			int c_s = Integer.parseInt(categoria_selec);
			int c_c = Integer.parseInt(campus_selec);
			int e_s = Integer.parseInt(edifici_selec);
			assistencia.setId_campus(c_c);
			assistencia.setId_categoria(c_s);
			assistencia.setConsulta(consulta);
			assistencia.setPrioritat(prioritat);
			assistencia.setData_inici(data_inici);
			assistencia.setEstat(estat);
			assistencia.setEstat_activa(estat_activa);	
			assistencia.setUsuari(usuari);
			assistencia.setId_tecnic(id_tecnic);
			assistencia.setTelefon(telefon);
			assistencia.setLocalitzacio(despatx);
			assistencia.setId_edifici(e_s);
			assistencia.setCodi_udl("" + codi_udl);
			assistencia.setSolucio("");
			assistencia.setSolucio_interna("");

			session.save(assistencia);
			tx.commit();
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// **********************************************************************
	public void BDEscriuResposta(int ticket, String resposta_formatada, int estat, int estat_activa) {
		// Metode que escriu a la BD una resposta formatada de la pantalla assist_pas_pdi.jsp i assist_admin.jsp
		try {
			Session session=hdeskservice.getSession();
			
			Transaction tx = session.beginTransaction();
			Assistencia assistencia = new Assistencia();

			// he de buscar l'assistencia amb el ticket
			assistencia = getAssistencia(ticket," ");

			// emmagatzemo totes les dades a la BD
			assistencia.setSolucio(resposta_formatada);

			//en el cas de que l'assistencia estigui resolta, l'he de passar a activa
			//sols per al perfil PAS-PDI
			assistencia.setEstat(estat);

			//quan un usuari fa una modificacio a la seva assistencia, s'ha de marcar aquest estat per 
			//a que a l'operador se li vegi l'assistencia en vermell, indicat que l'usuari a escrit algo.
			assistencia.setEstat_activa(estat_activa);

			// per al sakai 2.2
			// session.saveOrUpdateCopy(assistencia);
			session.merge(assistencia);
			tx.commit();
			hdeskservice.closeSession(session);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// **********************************************************************
	public boolean comprovaInactiva(String categ_sel) {
		int cat_sel_int = Integer.parseInt(categ_sel);
		boolean inactiva = false;
		try {
			Session session=hdeskservice.getSession();
			
			String existeix = ""+ session.createQuery("Select assis.ticket FROM Assistencia assis WHERE assis.id_categoria = " + cat_sel_int).list();
			if (existeix.equals("[]"))
				inactiva = true;
			else
				inactiva = false;
			
			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inactiva;
	}

	// **********************************************************************
	public String getNom_campus(int campusId) {
		String nom_campus = "";
		try {
			Session session=hdeskservice.getSession();
			
			// per al sakai 2.2
			nom_campus = ""+ session.createQuery("Select campus.nom FROM Campus campus WHERE campus.id_campus = " + campusId).list();
			nom_campus = nom_campus.substring(nom_campus.indexOf("[") + 1, nom_campus.indexOf("]"));

			hdeskservice.closeSession(session);
			
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nom_campus;
	}

        // **********************************************************************
	public void sendToHistoric(Date avui) {
		// TODO: sumar 4 dies al today, per a que als 5 dies es passi a l'historic
		// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// String strData_avui=sdf.format(today);
		try {
			Calendar cal = Calendar.getInstance();
			// cal.setTime(today); // Set it in the Calendar object
			Date resolta = new Date();

			Session session=hdeskservice.getSession();
			
			Assistencia assis = new Assistencia();
			//Transaction tx = session.beginTransaction();
			Transaction tx;
			// per a sakai 2.2
			// Iterator iter=session.iterate("from Assistencia assis where
			// assis.estat=1");
			Iterator iter = session.createQuery("from Assistencia assis where assis.estat=1").iterate();
			while (iter.hasNext()) {
				tx=session.beginTransaction();
				assis = (Assistencia) iter.next();
				resolta = (Date) assis.getData_fi();
				if (resolta == null)
					;
				else {
					cal.setTime(resolta);
					cal.add(Calendar.DATE, 4); 	// Afegim 4 dies i comprovem si execedeix la data d'avui
					resolta = cal.getTime(); 	// reaprofitem la mateixa variable
					if (resolta.before(avui) || resolta.equals(avui)) {
						assis.setEstat(2); // Aquestes les passem a l'historic.
						// per al sakai 2.2
						// session.saveOrUpdateCopy(assis);
						session.merge(assis);
					}

				}
				tx.commit();
			}
			//tx.commit();
			
			hdeskservice.closeSession(session);
			//hdeskservice.closeConnection(conn);
			//conn.close();
			//conn = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}// fi classe
