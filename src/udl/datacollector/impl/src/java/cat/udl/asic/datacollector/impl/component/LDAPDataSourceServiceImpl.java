package cat.udl.asic.datacollector.impl.component;

import javax.naming.*;
import javax.naming.directory.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceColumn;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.DataSourceValueRow;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataSourceService;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class LDAPDataSourceServiceImpl implements DataSourceService {
	
	public static String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
	
	private static Log M_log = LogFactory.getLog(LDAPDataSourceServiceImpl.class);
	
	private String ldapServer = null;
	
	private String port = null;
	
	private String securityPrincipal = null;

	private String securityCredentials = null;
	
	public String getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(String ldapServer) {
		this.ldapServer = ldapServer;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSecurityPrincipal() {
		return securityPrincipal;
	}

	public void setSecurityPrincipal(String securityPrincipal) {
		this.securityPrincipal = securityPrincipal;
	}

	public String getSecurityCredentials() {
		return securityCredentials;
	}

	public void setSecurityCredentials(String securityCredentials) {
		this.securityCredentials = securityCredentials;
	}	

	public void init (){
		System.out.println ("-------------INICIO EL LDAPDataSourceService----------------");
	}	
	

	public boolean deleteDataSourceValue(SectionInfo currentSection,
			DataSource dataSource, String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public DataSourceValue getDataSourceValue(SectionInfo currentSection,
			DataSource dataSource){

		DirContext ctx=null;
		//Its linked for the order
		LinkedHashMap valuesMap = new LinkedHashMap(); 		
		
		String searchBase = dataSource.getTableSrc();
		String filter = getFilter(currentSection, dataSource);
		
		if (!"".equals(filter)){

			try {			

				//Hashtable for environmental information
				Hashtable<String,String> env = new Hashtable<String,String>();
	
				//Specify which class to use for our JNDI filter
				env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
				env.put(Context.PROVIDER_URL, "ldap://" + getLdapServer() + ":" + getPort());

				//Get a reference to a directory context
				ctx =  new InitialDirContext(env);
				SearchControls constraints = new SearchControls();
	            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

	            NamingEnumeration results = ctx.search(searchBase, filter, constraints);

	            
	            while (results!=null && results.hasMore()){

	            	final boolean composed =DataSource.COMPOSED_TYPE.equals(dataSource.getType());

	            	SearchResult sr = (SearchResult) results.next();
	            	String dn = sr.getName();
	   
	            	Attributes attrs = sr.getAttributes();            	
	            		
	            	
	            	
	             	// if isUpdatable, we must verifyDN
	            	if (currentSection.isUpdatable() && dataSource.isUpdatable()){
		            	if (!verifyDN(dn,currentSection,dataSource)){
		            			M_log.debug("Exception LDAPDataSourceServiceImpl.getDataSourceValue: DN no verified");
		        				if (ctx!=null) ctx.close();
		        				throw new IllegalArgumentException ("DN no verified");
		            	}
	            	}
	            	
	            	
	            	if (!composed)    					
    					if (dataSource.getColumnKey().equals ("jpegPhoto")){
    							Base64 b64 = new Base64();
    						byte[] data =  (byte[]) attrs.get("jpegPhoto").get();
    						String sortida = b64.encode(data);
    						valuesMap.put("simple.value",sortida);
    					}else{
    							valuesMap.put("simple.value",attrs.get(dataSource.getColumnKey()).get());
    					}
	            	else{
					 	DataSourceValueRow dsvr = new DataSourceValueRow(dn);
    					for (DataSourceColumn dsc: dataSource.getColumns().values()){
    							Attribute  attr  = (Attribute)attrs.get(dsc.getColumnName());
    							if (attr!=null)
    								if (dsc.getColumnName().equals ("jpegPhoto")){
    									//M_log.debug("Demano" +dataSource.getColumnKey()) ;
    	    							Base64 b64 = new Base64();
    	    							byte[] data =  (byte[]) attrs.get("jpegPhoto").get();
    	    							String sortida = b64.encode(data);
    	    							//M_log.debug("La sortida és " + sortida);
    	    						dsvr.addColumnValue (dsc.getId(), sortida);
    								}else{
    	    							dsvr.addColumnValue (dsc.getId(), attrs.get(dsc.getColumnName()).get());
    	    						}
    							else
    								dsvr.addColumnValue (dsc.getId(), "");
    					}
    					valuesMap.put(dsvr.getId(), dsvr);
    				}
	            	
	            }	            
			} catch (NamingException e) {
				e.printStackTrace();
				M_log.debug("Error LDAPDataSourceServiceImpl.getDataSourceValue() "+e);
			}
			finally{
				try {
					if (ctx!=null) ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
					M_log.debug("Error LDAPDataSourceServiceImpl.getDataSourceValue() "+e);
				}
			}
		}
		
        return new DataSourceValue (dataSource.getId(),currentSection.getId(),valuesMap,currentSection.isUpdatable() && dataSource.isUpdatable());		
		
	}
	
	// get parameters initialized in SectionInfo and defined in <parameters></parameters> block of dataSource xml
	// get extra parameters: searchColumns and contains
	private String getFilter(SectionInfo currentSection, DataSource dataSource){
		
		String filter = "", filterParams = "", filterExtra = "";
		int nParam = dataSource.getParameterList().size();
		int nParamSearch = 0;
		
		if (nParam > 0) {
			
			//Verify that is ordered by position
			Iterator<Parameter> it = dataSource.getParameterList().iterator();			
			while (it.hasNext()){
					Parameter param = it.next();
					Parameter paramSec = currentSection.getParameter(param.getId());
					
					if (paramSec != null)
						filterParams += "(" + param.getColumnName() + "=" +  paramSec.getValue() + ")";
					else
						filterParams += "(" + param.getColumnName() + "=" +  param.getValue() + ")";
			}
			if(nParam > 1)
				filterParams = "(&" + filterParams + ")";
		}
		
		
		//Search for extra parameters
		if (dataSource.getSearchColumns()!=null && dataSource.getSearchColumns().size() >0){
			
			nParamSearch = dataSource.getSearchColumns().size();
			
			Iterator <String> it = dataSource.getSearchColumns().iterator();
			while (it.hasNext() ){
					String columnName = it.next();
					filterExtra += "(" + dataSource.getColumns().get(columnName).getColumnName() + "=*" +  dataSource.getContains() + "*)";					
			}
			if (nParamSearch > 1)
				filterExtra = "(|" + filterExtra + ")";		
		}		
		
		if (nParam > 0 && nParamSearch > 0)
			filter = "(&" + filterParams + filterExtra + ")";
		else if(nParam > 0 || nParamSearch > 0)
			filter = filterParams + filterExtra;

		return filter;
	}
	
	// get parameters initialized in SectionInfo and defined in <parameters></parameters> block of dataSource xml
	private String getDN(SectionInfo currentSection, DataSource dataSource){
		
		String dnParam = "";
		int nParam = dataSource.getParameterList().size();
		
		if (nParam > 0) {		
			
			//Verify that is ordered by position
			Iterator<Parameter> it = dataSource.getParameterList().iterator();			
			do{
					Parameter param = it.next();
					Parameter paramSec = currentSection.getParameter(param.getId());
					
					if (paramSec != null)
						dnParam = dnParam  + param.getColumnName() + "=" +  paramSec.getValue();
					else
						dnParam = dnParam  + param.getColumnName() + "=" +  param.getValue();
					
					if(it.hasNext())
						dnParam = dnParam + ",";

			}while (it.hasNext());
		}

		return dnParam;
	}		
	

	private Boolean verifyDN(String dn, SectionInfo currentSection, DataSource dataSource){
		String dnParam = getDN(currentSection, dataSource);
		return dnParam.equals(dn);
	}	
	

	public boolean saveDataSourceValue(SectionInfo currentSection,
			DataSource dataSource, DataSourceValue dataSourceValue){
		
		DirContext ctx=null;
		String searchBase = dataSource.getTableSrc();
		
		try {		
			
			if (verifyIdKey(currentSection, dataSource, dataSourceValue.getId())){

				//Hashtable for environmental information
				Hashtable<String,String> env = new Hashtable<String,String>();
				
				//Specify which class to use for our JNDI filter
				env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
				env.put(Context.PROVIDER_URL, "ldap://" + getLdapServer() + ":" + getPort());
				env.put(Context.SECURITY_AUTHENTICATION, "simple");
				env.put(Context.SECURITY_PRINCIPAL, getSecurityPrincipal());
				env.put(Context.SECURITY_CREDENTIALS, getSecurityCredentials());


				//Get a reference to a directory context
				ctx = new InitialDirContext(env);
				Attributes attrs = new BasicAttributes(false);
				
				final boolean composed =DataSource.COMPOSED_TYPE.equals(dataSource.getType());
	         	
	        	Map value = (Map) dataSourceValue.getValue();
	        	
	        	
				if (!composed)
					attrs.put(dataSource.getColumnKey(),value.get(dataSource.getColumnKey()));				
				else{				
					for (DataSourceColumn dsc: dataSource.getColumns().values()){
						attrs.put(dsc.getColumnName(),value.get(dsc.getId()));
						}
				}
				
				ctx.modifyAttributes(getDN(currentSection,dataSource)+","+searchBase, DirContext.REPLACE_ATTRIBUTE, attrs); // ADD_ATTRIBUTE, REPLACE_ATTRIBUTE, REMOVE_ATTRIBUTE
				return true;
				
			}
			else{
				//M_log.debug("Exception LDAPDataSourceServiceImpl.saveDataSourceValue: You are not allowed to save that datasource");
				if (ctx!=null) ctx.close();
				throw new SecurityException ("You are not allowed to save that datasource");			
			}		
		}catch (NamingException e) {
			e.printStackTrace();
			M_log.debug("Error LDAPDataSourceServiceImpl.getDataSourceValue() "+e);
		}finally{
			try {
				if (ctx!=null) ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
				M_log.debug("Error LDAPDataSourceServiceImpl.getDataSourceValue() "+e);
			}
		}		
		return false;
	}
	
	
	
	private boolean verifyIdKey(SectionInfo currentSection, DataSource dataSource, String value){
		
		M_log.debug("LDAPProvide verifyIdKey: currentSection " + currentSection.getId());
		M_log.debug("LDAPProvide verifyIdKey: dataSource Id" + dataSource.getId() + " columnName" + dataSource.getColumnKey());
		M_log.debug("LDAPProvide verifyIdKey: value " + value);
		
		if (DataSource.COMPOSED_TYPE.equals(dataSource.getType())){
			Iterator<Parameter> it = dataSource.getParameterList().iterator();
			
			while (it.hasNext()){
					Parameter param = it.next();
					M_log.debug("LDAPProvide verifyIdKey: param Id" + param.getId());
					
					Parameter paramSec = currentSection.getParameter(param.getId());
					M_log.debug("LDAPProvide verifyIdKey: paramSec Id" + paramSec.getId() + " value" + paramSec.getValue());
					
					if (param.getColumnName().equals(dataSource.getColumnKey())){					
						if (paramSec != null){
							if (paramSec.getValue().equals(value))
								return true;
							else
								return false;
						}
					}
			}	
				
		}
		else {
			String uid = getUID(currentSection, dataSource);
			if (uid != null)	
				return value.equals(uid);
		}
		return false;
	}
	
	// get UID columnName parameter initialized in SectionInfo and defined in <parameters></parameters> block of dataSource xml
	private String getUID(SectionInfo currentSection, DataSource dataSource){

		if (dataSource.getParameterList().size() > 0) {		
			
			//Verify that is ordered by position
			Iterator<Parameter> it = dataSource.getParameterList().iterator();			
			do{
					Parameter param = it.next();
					if (param.getColumnName().equals("uid")){
						return (String) currentSection.getParameter(param.getId()).getValue();
					}
			}while (it.hasNext());
		}

		return null;
	}	
	
	
}
