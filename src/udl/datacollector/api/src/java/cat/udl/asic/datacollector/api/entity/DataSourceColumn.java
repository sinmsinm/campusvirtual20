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

import java.io.Serializable;
import java.io.*;

public class DataSourceColumn implements Serializable {

	private static final long serialVersionUID = 6523471535622546146L;
	private String id = null;
	private String name = null;
	private String columnName= null;
	private String description= null;
	
	public DataSourceColumn(String id, String columnName,
			String description) {
		super();
		this.id = id;
		this.columnName = columnName;
		this.description = description;
	}
	
	public String getId() {
		return id;
	}
	public String getColumnName() {
		return columnName;
	}
	public String getDescription() {
		return description;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public void setDescription(String description) {
		this.description = description;
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
