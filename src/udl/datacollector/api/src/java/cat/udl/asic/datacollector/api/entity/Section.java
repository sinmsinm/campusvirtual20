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
* Authors: Alex Ballesté
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat) and usuaris-cvirtual@llistes.udl.cat
* Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
*
**/

package cat.udl.asic.datacollector.api.entity;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.io.Serializable;
import java.io.*;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Section implements Entity, Serializable {

	private static final long serialVersionUID = 7526471535622776146L;
	private String id = null;
	private String name=null;
	private String description = null;
	private String longDescription=null;
	private String viewTemplate = null;
	private String viewMessages = null;
	
	public String getViewMessages() {
		return viewMessages;
	}

	public void setViewMessages(String viewMessages) {
		this.viewMessages = viewMessages;
	}

	private Properties extraProperties = null;
	private Map <String,DataSource> dataSourceMap=null;
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Section() {
		super();
	}
	
	public Section(String id) {
		super();
		this.id = id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public DataSource getDataSource (String dataSourceId){
		return dataSourceMap.get(dataSourceId);
	}
	
	public String getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(String viewTemplate) {
		this.viewTemplate = viewTemplate;
	}

	public Map<String,DataSource> getDataSourceMap() {
		return dataSourceMap;
	}

	public void setDataSourceMap(Map<String,DataSource> dataSourceMap) {
		this.dataSourceMap = dataSourceMap;
	}

	public ResourceProperties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReference() {
		 return ServerConfigurationService.getAccessUrl() + "/section/" + this.getId();
	}

	public String getReference(String arg0) {
		return getReference();
	}

	public String getUrl() {
		  return ServerConfigurationService.getAccessUrl() + "/section/" + this.getId();
	}

	public String getUrl(String arg0) {
		return getUrl();
	}

	public Element toXml(Document arg0, Stack arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public Properties getExtraProperties() {
		return extraProperties;
	}

	public void setExtraProperties(Properties extraProperties) {
		this.extraProperties = extraProperties;
	}

	
	   /**
	   * This is the default implementation of readObject.
	   * Customise if necessary.
	   */
	   private void readObject(
	     ObjectInputStream aInputStream
	   ) throws ClassNotFoundException, IOException {
	     //always perform the default de-serialization first
	     aInputStream.defaultReadObject();
	  }

	    /**
	    * This is the default implementation of writeObject.
	    * Customise if necessary.
	    */
	    private void writeObject(
	      ObjectOutputStream aOutputStream
	    ) throws IOException {
	      //perform the default serialization for all non-transient, non-static fields
	      aOutputStream.defaultWriteObject();
	    }

}
