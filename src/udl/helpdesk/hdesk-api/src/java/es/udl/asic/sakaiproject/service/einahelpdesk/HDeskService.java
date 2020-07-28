package es.udl.asic.sakaiproject.service.einahelpdesk;

//import es.udl.asic.sakaiproject.tool.einahelpdesk.Assistencia;
import java.util.*;
import java.sql.Connection;
//import javax.sql.DataSource;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public interface HDeskService{
	//public Connection getConnection();
	public Session getSession();
	//public DataSource setupDataSource();
	//public void shutdown();
	public void closeSession(Session session);
	//public void closeConnection(Connection con);
	public void setProps(Configuration cfg);
}
