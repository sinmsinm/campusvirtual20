 /**
 * Copyright (c) 2010 Universitat de Lleida
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
* Authors: Noemi Lorente
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat), Noemi Lorente (noemi@oqua.udl.cat)  and usuaris-cvirtual@llistes.udl.cat
* Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
*
**/

package cat.udl.asic.datacollector.impl.component;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceColumn;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataSourceService;

public class PDIDataSourceServiceImpl implements DataSourceService {
	
	private String server = null;
	
	private String start = null;
	
	private String status = null;
	
	private String user = null;
	
	private String password = null;
	
	public void init (){		
	}	

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}	
	
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	

	public boolean deleteDataSourceValue(SectionInfo currentSection,
			DataSource dataSource, String key) {
		return false;
	}

	public DataSourceValue getDataSourceValue(SectionInfo currentSection,
			DataSource dataSource) {

		return new DataSourceValue (dataSource.getId(),currentSection.getId(),new LinkedHashMap() ,currentSection.isUpdatable() && dataSource.isUpdatable());
	}

	public boolean saveDataSourceValue(SectionInfo currentSection,
			DataSource dataSource, DataSourceValue dataSourceValue) {
		
		URL u;
	    InputStream is = null;
	    Document doc;
	    String urlParam = "/?name=" + dataSource.getTableSrc() + "&xml=y" + 
	    					getParameters(currentSection, dataSource) + 
	    					getDataSourceColumn(dataSource, dataSourceValue);
	    
	    try {
	    	//Start Transformation
	    	u = new URL(server + start + urlParam);
 	
	    	
	    	Authenticator au = new Authenticator() {
	    			         @Override
	    			         protected PasswordAuthentication
	    			            getPasswordAuthentication() {
	    			            return new PasswordAuthentication
	    			               (user, password.toCharArray());
	    			         }
	    			      };
	        Authenticator.setDefault(au);  	
	    	
	    	is = u.openStream(); 	    	
	    	
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	doc = builder.parse(is);    	
	    	
	    	Element root = doc.getDocumentElement();
	    	root.normalize();
	    	
    		if (!root.getElementsByTagName("result").item(0).getChildNodes().item(0).getNodeValue().equals("OK"))
    			return false;
    		else{
    			long initTime = System.currentTimeMillis();
    			
    			do{
    				//Status Transformation    			
        			u = new URL(server + status + "/?name=" + dataSource.getTableSrc() + "&xml=y");
        	    	is = u.openStream(); 	    	
        	    	
        	    	doc = builder.parse(is);    	
        	    	
        	    	root = doc.getDocumentElement();
        	    	root.normalize();
        	    	
        	    	if(root.getElementsByTagName("status_desc").item(0).getChildNodes().item(0).getNodeValue().equals("Running"))
        	    		Thread.currentThread().sleep(1000);
        	    	else if((root.getElementsByTagName("status_desc").item(0).getChildNodes().item(0).getNodeValue().equals("Waiting"))
        	    			||(root.getElementsByTagName("status_desc").item(0).getChildNodes().item(0).getNodeValue().equals("Finished"))){
            			if(root.getElementsByTagName("nr_errors").item(0).getChildNodes().item(0).getNodeValue().equals("0"))
            				return true;	
            			else
            				return false;        	    		
        	    	}
    				
    			}while ( (System.currentTimeMillis() - initTime) < 60000);
    		}

	    } catch (MalformedURLException mue) {	          
	          mue.printStackTrace();
	    } catch (IOException ioe) {
	          ioe.printStackTrace();
	    } catch (ParserConfigurationException e) {
	    	  e.printStackTrace();
	    } catch (SAXException e) {
	    	  e.printStackTrace();
		} catch (InterruptedException e) {
			  e.printStackTrace();
		} finally {
	         try {
	        	 is.close();
	         } catch (IOException ioe) {
	        	 ioe.printStackTrace();
	         }
	      }
		return false;
	}
	
	
	public String getParameters(SectionInfo currentSection,
			DataSource dataSource){
		
		String urlParam="";
		
		for (Parameter param : dataSource.getParameterList()){

			Parameter paramSec = currentSection.getParameter(param.getId());			
			if (paramSec != null)
				urlParam = urlParam + "&" + param.getColumnName() + "=" + paramSec.getValue();
			else
				urlParam = urlParam + "&" + param.getColumnName() + "=" + param.getValue();
				
		}
		
		return urlParam;
	}
	
	
	public String getDataSourceColumn(DataSource dataSource, 
			DataSourceValue dataSourceValue){
		
		String urlParam="";
		if (DataSource.getCOMPOSED_TYPE().equals (dataSource.getType())){
			Map value = (Map) dataSourceValue.getValue();
			for (DataSourceColumn dsc : dataSource.getColumns().values()){
				urlParam = urlParam + "&" + dsc.getColumnName() + "=" + value.get(dsc.getId());
			}
		}			
	
		return urlParam;
	}	

}
