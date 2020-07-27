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
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat)  and usuaris-cvirtual@llistes.udl.cat
* Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
*
**/

package cat.udl.asic.datacollector.impl.provider;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;


import net.sf.ehcache.Cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.CollectionResolvable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Createable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Inputable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RedirectDefinable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Redirectable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestStorable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestStorage;
import org.sakaiproject.entitybroker.entityprovider.extension.TemplateMap;
import org.sakaiproject.entitybroker.entityprovider.search.Restriction;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.tool.api.Session;

import cat.udl.asic.datacollector.api.entity.DataSourceColumn;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.DataSourceValueRow;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.Section;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.service.DataCollectorService;
import cat.udl.asic.datacollector.api.service.RegisterHelper;

import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.api.FormattedText.Level;


public class DataSourceProvider extends AbstractEntityProvider implements CoreEntityProvider, RESTful, 
RequestStorable, RedirectDefinable {
	
	private static Log m_log = LogFactory
	.getLog(DataSourceProvider.class);

	
	public static String PREFIX = "datasource";
	private RequestStorage requestStorage;
	private DeveloperHelperService developerHelperService=null;
	private DataCollectorService dataCollectorService = null; 
	private SectionProvider sectionProvider = null;
	private AuthzGroupService authzGroupService;

	public SectionProvider getSectionProvider() {
		return sectionProvider;
		
	}

	public void setSectionProvider(SectionProvider sectionProvider) {
		this.sectionProvider = sectionProvider;
	}
	
	public void setAuthzGroupService (AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}
	
	public AuthzGroupService getAuthzGroupService () {
		return this.authzGroupService;
	}
	
	public boolean entityExists(String id) {
		return true;
	}

	public String getEntityPrefix() {
		return PREFIX;
	}

	public void setRequestStorage(RequestStorage requestStorage) {
		this.requestStorage = requestStorage;
		
	}

	public String createEntity(EntityReference ref, Object entity,Map<String, Object> params) {
			throw new SecurityException ("There ara no option to create new dataSource");
	}

	public Object getSampleEntity() {
		return new DataSourceValue("sample-id");
	}

public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {

		String dataSourceId =  EntityReference.getIdFromRef(ref.getReference());
		String sectionId = (String) requestStorage.getStoredValue("sectionId");
		
		DataSourceValue dsv = (DataSourceValue) entity;
		Map val = dsv.getValue();
		
		EntityReference sectionReference = new EntityReference (sectionProvider.getEntityPrefix(), "definition:"+sectionId);
		Section curSection = (Section) sectionProvider.getEntity(sectionReference);
		
		EntityReference sectionInfoReference = new EntityReference (sectionProvider.getEntityPrefix(), sectionId);
		SectionInfo curSectionInfo = (SectionInfo) sectionProvider.getEntity(sectionInfoReference);
		DataSource currentDataSource = curSection.getDataSource(dataSourceId);

		Map.Entry pairs = null;
		Iterator it = null;
		StringBuilder alertMsg = new StringBuilder();

		if (!currentDataSource.isUpdatable() || !curSectionInfo.isUpdatable()){
			throw	new SecurityException ("That datasource can't be updated");
		}
		
		if (curSectionInfo != null ){

			//XSS prevention
			if(!val.isEmpty() && currentDataSource.isEscapeHtml()){
			
				it = val.entrySet().iterator();
				while (it.hasNext()) {

				    pairs = (Map.Entry)it.next();
				    if(pairs.getValue() instanceof String){

				    	//escapem tots els caràcters susceptibles de formar part d'un atac XSS, i no eliminem cap text.
				    	String content = FormattedText.processFormattedText((String)pairs.getValue(), alertMsg, Level.HIGH);
					    if(content == null)
					    	content = "";
					    val.put(pairs.getKey(),content);
					    //System.out.println("FormattedText.processFormattedText - TRUE " + pairs.getKey() + FormattedText.processFormattedText((String)pairs.getValue(), alertMsg, Level.HIGH) + "VALORDEF: " + content);
				    	
			        }else if(pairs.getValue() instanceof Map){
			        	
			        	//quan enviem un camp sense cap inicialització de text s'envia un objecte buit {} que s'interpreta com un MAP...
			        	
			        	Map value1 = (Map) pairs.getValue();
			        	Iterator it1 = value1.entrySet().iterator();
						while (it1.hasNext()) {

							//en teoria no hi hauria d'haver mai res... però per si les mosques ho mirem...
						    Map.Entry pair1 = (Map.Entry)it1.next();

					    	String content = FormattedText.processFormattedText((String)pair1.getValue(), alertMsg, Level.HIGH);
						    if(content == null)
						    	content = "";
						    value1.put(pair1.getKey(),content);
						    //System.out.println("FormattedText.processFormattedText " + FormattedText.processFormattedText((String)pair1.getValue(), alertMsg, Level.HIGH) + "VALORDEF1: " + content);
						    
						}
						val.put(pairs.getKey(),(Map)value1);
			        }
				}
			}
			
			boolean success = dataCollectorService.saveDataSourceValue(curSectionInfo,currentDataSource,dsv);
			if (!success){
				throw new IllegalArgumentException ("Update Operation failed for " + ref);
			}
		}else{
				throw new IllegalArgumentException ("There is no section info associated for this datasource");
		}
}

public Object getEntity(EntityReference ref) {

		String id = ref.getReference();
		String dataSourceId =  EntityReference.getIdFromRef(ref.getReference());
		String sectionId = (String) requestStorage.getStoredValue("sectionId");
		String rowId = (String) requestStorage.getStoredValue("rowId");
		String siteId= (String) requestStorage.getStoredValue("siteId");
		
		DataSource clonedDataSource = null;
		//Parametrized parameters
		
		/* Do it for regular and automated providers*/ 
		String orderby = (String) requestStorage.getStoredValue("orderby");
		String ordertype = (String) requestStorage.getStoredValue("ordertype");
		String page = (String) requestStorage.getStoredValue("page");
		String rows = (String) requestStorage.getStoredValue("rows");
		String searchable = (String) requestStorage.getStoredValue("searchable");
		String contains = (String)  requestStorage.getStoredValue("contains");
		

		
		
		
		boolean filterBySite = (siteId!=null); 


		EntityReference sectionReference = new EntityReference (sectionProvider.getEntityPrefix(), "definition:" + sectionId);
		Section curSection = (Section) sectionProvider.getEntity(sectionReference);
		
		EntityReference sectionInfoReference = new EntityReference (sectionProvider.getEntityPrefix(), sectionId);
		SectionInfo curSectionInfo = (SectionInfo) sectionProvider.getEntity(sectionInfoReference);
		DataSource currentDataSource = curSection.getDataSource(dataSourceId);

		
		Map<String,Object> params = requestStorage.getStorageMapCopy(false, false, true, false);

		if (params==null){
				//M_log.debug("There are no params");
		}else{
			//M_log.debug("Entro pero no hi ha elements");
			for (Entry<String,Object> param : params.entrySet()){
				//M_log.debug("Parameters are key" + param.getKey() + " values is " + param.getValue());
			}
				
		}
		
		
		if (curSectionInfo !=null){
			
			
			//Util for that datasources that are only an input point.
			if (!currentDataSource.isReadable()){

				if (rowId== null || "".equals(rowId)){
					return new DataSourceValue(currentDataSource.getId(), curSectionInfo.getId(), new LinkedHashMap<String, String>(), false);
				}else{
					DataSourceValueRow dsvr = new DataSourceValueRow(rowId);
					dsvr.setValue(new LinkedHashMap<String, String>());
					dsvr.setSectionId(curSectionInfo.getId());
					dsvr.setUpdatable(currentDataSource.isUpdatable());
					return  dsvr;
				}
				
				
				//throw	new SecurityException ("That datasource can't be updated");
			}
			//Look for parametrized parameters
			/* Use it as parameters for manual driven providers*/
			
			if (params != null && params.size()>0){
				if (clonedDataSource==null){
					clonedDataSource = cloneDataSource (currentDataSource);
				}
				
				LinkedHashMap<String,Object> reqParam = new  LinkedHashMap<String, Object>();
				
				for (Entry<String,Object> param : params.entrySet()){
					reqParam.put(param.getKey(), param.getValue());
					//M_log.debug("Parameters are key" + param.getKey() + " values is " + param.getValue());
				}
				clonedDataSource.setRequestParameters(reqParam);
				currentDataSource = clonedDataSource;
			}

			
			
			
			/*Order filters*/			
			if (orderby!=null && !"".equals(orderby) && !"null".equals(orderby)){ 
				
				if (clonedDataSource==null){
					clonedDataSource = cloneDataSource (currentDataSource);
				}
				
				clonedDataSource.setOrderedby(orderby);

				if (ordertype != null && !"".equals(ordertype)){
					if (ordertype.toUpperCase().equals("ASC"))	
						clonedDataSource.setOrdertype(DataSource.ASC_ORDER);
					else if (ordertype.toUpperCase().equals("DESC"))	
						clonedDataSource.setOrdertype(DataSource.DSC_ORDER);
				}
				currentDataSource = clonedDataSource;
			}
			
			/*Pagination filters*/			
			if (page != null && !"".equals(page) && !"null".equals(page) &&  rows != null && !"".equals(rows)  && !"null".equals(rows)){
				int pageNum = 0;
				int rowsNum = 0;
				try{
					pageNum = Integer.valueOf(page);
					rowsNum = Integer.valueOf(rows);
				
					if (clonedDataSource==null){
						clonedDataSource = cloneDataSource(currentDataSource);
					}
				
					clonedDataSource.setPage(pageNum);
					clonedDataSource.setRows(rowsNum);
				
					currentDataSource = clonedDataSource;
				}catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}
				

			/*Search data filters*/
				
			if (searchable!=null && !"".equals(searchable) && !"null".equals(searchable)){ 
				if (contains!=null && !"".equals(contains) && !"null".equals(contains)){
					List <String> columnsToSearch = new ArrayList <String> (); 
					
					for (String searchColumn : searchable.split(":")){
						columnsToSearch.add(searchColumn);
					}
					
					if (clonedDataSource==null){
						clonedDataSource = cloneDataSource(currentDataSource);
					}
					
					clonedDataSource.setSearchColumns(columnsToSearch);
					clonedDataSource.setContains(contains);
					currentDataSource = clonedDataSource;
				}
			}

			DataSourceValue curValue = dataCollectorService.getDataSourceValue(curSectionInfo,currentDataSource);
			
			if (rowId== null || "".equals(rowId)){
				return curValue;
			}else{
				Map values =  curValue.getValue();
				return values.get(rowId);
			}
								
 		}else{
 			throw new IllegalArgumentException ("There is no section info associated for this datasource");
 		}
	}

	public void deleteEntity(EntityReference ref, Map<String, Object> params) {

		String dataSourceId =  EntityReference.getIdFromRef(ref.getReference());
		String sectionId = (String) requestStorage.getStoredValue("sectionId");
		String rowId = (String) requestStorage.getStoredValue("rowId");
		EntityReference sectionReference = new EntityReference (sectionProvider.getEntityPrefix(), "definition:"+sectionId);
		Section curSection = (Section) sectionProvider.getEntity(sectionReference);
		EntityReference sectionInfoReference = new EntityReference (sectionProvider.getEntityPrefix(), sectionId);
		SectionInfo curSectionInfo = (SectionInfo) sectionProvider.getEntity(sectionInfoReference);
		DataSource currentDataSource = curSection.getDataSource(dataSourceId);

		if (!currentDataSource.isDeletable() || !curSectionInfo.isUpdatable()){
			throw	new SecurityException ("That datasource can't be updated");
		}
		
		if (curSectionInfo !=null){
			if ((DataSource.COMPOSED_TYPE.equals(currentDataSource.getType()) ||
						DataSource.EVENT_TYPE.equals(currentDataSource.getType()) ||
						DataSource.SCHEDULER_TYPE.equals(currentDataSource.getType())
			) && rowId!=null && !"".equals(rowId)){
					boolean success = dataCollectorService.deleteDataSourceValue(curSectionInfo,currentDataSource,rowId);
					if (!success){
						throw new IllegalArgumentException ("Update Operation failed for " + ref);
					}
			}else if ((DataSource.SIMPLE_TYPE.equals((currentDataSource.getType())) ||
					DataSource.EVENT_TYPE.equals(currentDataSource.getType()))) {
					boolean success = dataCollectorService.deleteDataSourceValue(curSectionInfo,currentDataSource,null); //Row Id could be null
					if (!success){
						throw new IllegalArgumentException ("Update Operation failed for " + ref);
					}
			}else{
					throw new IllegalArgumentException ("That element seems to be a composed element but you want to delete all. This action is not allowed " + ref);
			}
		}
		
	}

	public List<?> getEntities(EntityReference ref, Search search) {
		throw new SecurityException ("That operation is not allowed");
	}

	public String[] getHandledOutputFormats() {
		return new String[] {Formats.XML, Formats.JSON, Formats.HTML};
	}

	public String[] getHandledInputFormats() {
		return new String[] {Formats.XML, Formats.JSON, Formats.HTML};
	}

	public DeveloperHelperService getDeveloperHelperService() {
		return developerHelperService;
	}

	public void setDeveloperHelperService(
			DeveloperHelperService developerHelperService) {
		this.developerHelperService = developerHelperService;
	}

	public TemplateMap[] defineURLMappings() {
		return  new TemplateMap[] {
				//new TemplateMap("/{prefix}/{parentdatasourceId}/{datasourceId}", "{datasourceId}{dot-extension}"),
		};
	}
	

	public DataCollectorService getDataCollectorService() {
		return dataCollectorService;
	}

	public void setDataCollectorService(DataCollectorService dataCollectorService) {
		this.dataCollectorService = dataCollectorService;
	}

	public RequestStorage getRequestStorage() {
		return requestStorage;
	}
	
	private DataSource cloneDataSource (DataSource input){
		DataSource clonedDataSource = new DataSource(input.getId(),input.getDescription(),input.getType(),input.isReadable(),
				input.isUpdatable(),input.isDeletable(),input.isEscapeHtml(), input.getTableSrc(),input.getColumnKey(),
				input.getServiceProviderId(),input.getColumns(),input.getParameterList(),input.getRequestParameters());
			clonedDataSource.setGetEvent(input.getGetEvent());
			clonedDataSource.setUpdateEvent(input.getUpdateEvent());
			clonedDataSource.setDeleteEvent(input.getDeleteEvent());
		
		return clonedDataSource;
	}

}
