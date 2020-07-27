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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Collections;
import java.util.Stack;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Block implements Entity,Comparable<Block> {

	private String id = null;
	private String name=null;
	private String description=null;
	private String longDescription = null;
	private Properties extraProperties = null;
	List <String> sectionIdList = null;
	
	
	public Block(String id, String name, String description,String longDescription) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.longDescription = longDescription;
	}
	public Block() {
		super();

	}
	public Block(String id) {
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
		// TODO Auto-generated method stub
		return null;
	}

	public String getReference() {
		  return ServerConfigurationService.getAccessUrl() + "/block/" + Entity.SEPARATOR + this.getId();
	}

	public String getReference(String arg0) {
		// TODO Auto-generated method stub
		return getReference();
	}

	public String getUrl() {
		  return ServerConfigurationService.getAccessUrl() + "/block/" + this.getId();
	}

	public String getUrl(String arg0) {
		// TODO Auto-generated method stub
		return getUrl();
	}

	public Element toXml(Document arg0, Stack arg1) {
		// TODO Auto-generated method stub
		return null;
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

	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	public List<String> getSectionIdList() {
		Collections.sort((List<String>)  sectionIdList);
		return sectionIdList;
	}
	public void setSectionIdList(List<String> sectionIdList) {
		this.sectionIdList = sectionIdList;
	}

	public int compareTo(Block o1) {
		return	this.getId().compareTo(o1.getId());
	}
	
	public Properties getExtraProperties() {
		return extraProperties;
	}
	public void setExtraProperties(Properties extraProperties) {
		this.extraProperties = extraProperties;
	}
	

}
