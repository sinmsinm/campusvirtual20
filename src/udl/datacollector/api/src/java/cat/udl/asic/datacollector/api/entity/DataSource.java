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
import java.io.Serializable;
import java.io.*;

public class DataSource implements Serializable {

	private static final long serialVersionUID = 6526471535622776146L;
	public static String SIMPLE_TYPE = "simple.value"; 
	public static String COMPOSED_TYPE = "collection.value";
	public static String AUTHZ_TYPE = "authz.value";
	public static String EVENT_TYPE = "event.value";
	public static String SCHEDULER_TYPE = "scheduler.value";
	
	private String id =null; /*The id to localize the datasource*/
	private String name=null; /*The name to reference the that datasource*/
	private String description=null;
	private String type = null; /* Typa can be simple or collection*/
	private boolean readable = false; /*could be an readable value*/
	private boolean updatable = false; /*could be an updatable value*/
	private boolean deletable = false; /*could be an deletable value*/
	private boolean escapeHtml = true; /*Antisamy option to escape html content by default*/
	private String tableSrc = null; /*Name of the table*/
	private String columnKey = null;  /*The column name to licalize a single dataValue in case of simple 
															is the name of column of where is the value in case of collection if is the primary key*/
	private String serviceProviderId = null;
	private Map <String,DataSourceColumn> columns = null;
	private List <Parameter> parameterList = null;
	private Map <String,Object> requestParameters = null; 
	

	public static int ASC_ORDER = 1;
	public static int DSC_ORDER = 2;
	
	/*Dynamic search parameters*/
	private String orderedby= null;
	private int page=0;
	private int rows=0;
	private int ordertype=ASC_ORDER;
	private List <String> searchColumns =null;
	private String contains = null; //only search string at moment
	
	private Event updateEvent = null;
	private Event deleteEvent = null;
	private Event getEvent = null;
	
	
	
	public DataSource(String id,String description, String type,
			boolean updatable, boolean escapeHtml, String tableSrc, String columnKey,
			String serviceProviderId, Map<String,DataSourceColumn> columns,
			List<Parameter> parameterList) {
		super();
		this.id = id;
		this.description = description;
		this.type = type;
		this.readable = true;
		this.updatable = updatable;
		this.deletable = updatable;
		this.escapeHtml = escapeHtml;
		this.tableSrc = tableSrc;
		this.columnKey = columnKey;
		this.serviceProviderId = serviceProviderId;
		this.columns = columns;
		this.parameterList = parameterList;
		this.ordertype=ASC_ORDER;
	}
	
	

	public DataSource(String id,String description, String type,
			boolean readable,boolean updatable,boolean deletable, boolean escapeHtml, String tableSrc, String columnKey,
			String serviceProviderId, Map<String,DataSourceColumn> columns,
			List<Parameter> parameterList,Map <String,Object> requestParameters) {
		super();
		this.id = id;
		this.description = description;
		this.type = type;
		this.readable = readable;
		this.updatable = updatable;
		this.deletable = deletable;
		this.tableSrc = tableSrc;
		this.columnKey = columnKey;
		this.serviceProviderId = serviceProviderId;
		this.columns = columns;
		this.parameterList = parameterList;
		this.ordertype=ASC_ORDER;
		this.requestParameters=requestParameters;
	}
	
	
	public DataSource(String id,  String description, String type,
			boolean readable, boolean updatable, boolean deletable, 
			boolean escapeHtml, String serviceProviderId) {
		super();
		this.id = id;
		this.description = description;
		this.type = type;
		this.readable = readable;
		this.updatable = updatable;
		this.deletable = deletable;
		this.escapeHtml = escapeHtml;
		this.serviceProviderId = serviceProviderId;
	}



	public static String getSIMPLE_TYPE() {
		return SIMPLE_TYPE;
	}
	public static String getCOMPOSED_TYPE() {
		return COMPOSED_TYPE;
	}
	public String getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
	public String getType() {
		return type;
	}
	public boolean isUpdatable() {
		return updatable;
	}
	public String getTableSrc() {
		return tableSrc;
	}
	public String getColumnKey() {
		return columnKey;
	}
	public String getServiceProviderId() {
		return serviceProviderId;
	}
	public Map <String,DataSourceColumn> getColumns() {
		return columns;
	}
	public List<Parameter> getParameterList() {
		return parameterList;
	}
	public static void setSIMPLE_TYPE(String simple_type) {
		SIMPLE_TYPE = simple_type;
	}
	public static void setCOMPOSED_TYPE(String composed_type) {
		COMPOSED_TYPE = composed_type;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}
	public void setTableSrc(String tableSrc) {
		this.tableSrc = tableSrc;
	}
	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}
	
	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}
	public void setColumns(Map <String,DataSourceColumn> columns) {
		this.columns = columns;
	}
	public void setParameterList(List<Parameter> parameterList) {
		this.parameterList = parameterList;
	}
	
	public String getOrderedby() {
		return orderedby;
	}

	public void setOrderedby(String orderedby) {
		this.orderedby = orderedby;
	}

	public int getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(int ordertype) {
		this.ordertype = ordertype;
	}

	public int getPage() {
		return page;
	}

	public int getRows() {
		return rows;
	}


	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}


	public List<String> getSearchColumns() {
		return searchColumns;
	}





	public String getContains() {
		return contains;
	}





	public void setSearchColumns(List<String> searchColumns) {
		this.searchColumns = searchColumns;
	}





	public void setContains(String contains) {
		this.contains = contains;
	}





	public String getName() {
		return name;
	}





	public void setName(String name) {
		this.name = name;
	}





	public boolean isReadable() {
		return readable;
	}





	public void setReadable(boolean readable) {
		this.readable = readable;
	}





	public boolean isDeletable() {
		return deletable;
	}





	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	
	
	
	public boolean isEscapeHtml() {
		return escapeHtml;
	}

	public void setEscapeHtml(boolean escapeHtml) {
		this.escapeHtml = escapeHtml;
	}

	public Event getUpdateEvent() {
		return updateEvent;
	}



	public void setUpdateEvent(Event updateEvent) {
		this.updateEvent = updateEvent;
	}



	public Event getDeleteEvent() {
		return deleteEvent;
	}



	public void setDeleteEvent(Event deleteEvent) {
		this.deleteEvent = deleteEvent;
	}



	public Event getGetEvent() {
		return getEvent;
	}



	public void setGetEvent(Event getEvent) {
		this.getEvent = getEvent;
	}
	
	public Map<String, Object> getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(Map<String, Object> requestParameters) {
		this.requestParameters = requestParameters;
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

