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

import java.util.Map;
import java.util.Stack;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.Serializable;
import java.io.*;

public class DataSourceValue implements Entity,Serializable {

	private static final long serialVersionUID = 6523471535622546246L;
	protected String id = null;
	protected String sectionId = null;
	protected Map value=null;
	protected Boolean updatable=null;
	
	public DataSourceValue(String id,String sectionId,  Map value, boolean updatable) {
		super();
		this.id = id;
		this.sectionId = sectionId;
		this.value = value;
		this.updatable = updatable;
	}

	public DataSourceValue() {
		super();
	}
	
	public DataSourceValue(String id) {
		super();
		this.id = id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return  id;
	}


	public ResourceProperties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReference() {
		  return ServerConfigurationService.getAccessUrl() + "/section/" + sectionId + "/datasource/" +  this.getId();
	}

	public String getReference(String arg0) {
		// TODO Auto-generated method stub
		return getReference();
	}

	public String getUrl() {
		  return ServerConfigurationService.getAccessUrl() + "/section/" + sectionId + "/datasource/" +  this.getId();
	}

	public String getUrl(String arg0) {
		// TODO Auto-generated method stub
		return getUrl();
	}

	public Element toXml(Document arg0, Stack arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getValue() {
		return value;
	}

	public boolean isUpdatable() {
		if (updatable==null){
			this.updatable=false;
		}
		
		return updatable;
	}

	
	public void setValue(Map value) {
		this.value = value;
	}

	public void setUpdatable(boolean updatable) {
		if (this.updatable==null){
			this.updatable = updatable;
		}
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
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
