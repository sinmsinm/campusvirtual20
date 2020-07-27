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

public class SectionInfo implements Entity,java.io.Serializable {

	private String id = null;
	private String sectionId = null;
	private String name=null;
	private String description = null;
	private String longDescription = null;
	private String siteId = null;
	
	private  Map <String,Parameter> initializedParameters = null;
	private Boolean updatable=null;
	
	public Boolean isUpdatable() {
		if (this.updatable==null){
			this.updatable = false;
		}
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		if (this.updatable==null){
			this.updatable = updatable;
		}
	}

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

	public SectionInfo() {
		super();
	}
	
	public SectionInfo(String id) {
		super();
		this.id = id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}


	public ResourceProperties getProperties() {
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
		return null;
	}

	public void addParameter (Parameter newParameter){
		if (initializedParameters==null){
			initializedParameters = new java.util.HashMap();
		}
		initializedParameters.put (newParameter.getId(),newParameter);
	}
	public Parameter getParameter (String parameterId){

		if (initializedParameters==null){
			initializedParameters = new java.util.HashMap();
		}
		return (Parameter) initializedParameters.get(parameterId);
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public Map<String, Parameter> getInitializedParameters() {
		return initializedParameters;
	}

	public void setInitializedParameters(
			Map<String, Parameter> initializedParameters) {
		this.initializedParameters = initializedParameters;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	
	public String getSiteId() {
		return this.siteId;
	}
	
	public boolean existsSiteId() {
		if(this.siteId != null && !this.siteId.equals(""))
			return true;
		return false;
	}
}
