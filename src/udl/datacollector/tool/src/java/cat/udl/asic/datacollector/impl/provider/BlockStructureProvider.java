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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;


import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RedirectDefinable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestStorable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestStorage;
import org.sakaiproject.entitybroker.entityprovider.extension.TemplateMap;
import org.sakaiproject.entitybroker.entityprovider.search.Restriction;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;

import cat.udl.asic.datacollector.api.entity.Block;
import cat.udl.asic.datacollector.api.entity.Section;
import cat.udl.asic.datacollector.api.service.DataCollectorService;
import cat.udl.asic.datacollector.api.service.RegisterHelper;


public class BlockStructureProvider  extends AbstractEntityProvider implements CoreEntityProvider, RESTful,RequestStorable,RedirectDefinable {
	
	public static String PREFIX = "blockdata";
	private static String blockCacheIds = "blockCacheIds";
	
	private DeveloperHelperService developerHelperService=null;
	private DataCollectorService dataCollectorService = null;
	private RequestStorage requestStorage;
	private SectionProvider sectionProvider = null;
	protected Cache cache = null;
	private AuthzGroupService authzGroupService;
	
	public Cache getCache()
	{
		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(Cache cache)
	{
		this.cache = cache;
	}

	public DeveloperHelperService getDeveloperHelperService() {
		return developerHelperService;
	}

	public RequestStorage getRequestStorage() {
		return requestStorage;
	}

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
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getEntityPrefix() {
	
		return PREFIX;
	}
	
	public String createEntity(EntityReference ref, Object entity,
			Map<String, Object> params) {
	
		return null;
	}
	
	public Object getSampleEntity() {
	
		return new Block();
	}
	
	public void updateEntity(EntityReference ref, Object entity,
			Map<String, Object> params) {
	}
	
	public Object getEntity(EntityReference ref) {
		return null;
	}
	
	public void deleteEntity(EntityReference ref, Map<String, Object> params) {
	
	}
	
	public List<?> getEntities(EntityReference ref, Search search) {
			List<Block> blockList = getAllBlocks();
			List<Block> finalBlockList = new ArrayList <Block>(); 
			String updateOnly = (String) requestStorage.getStoredValue("updateOnly");
			String siteId = (String) requestStorage.getStoredValue("siteId");
			
			// First getAll Block
			for (Block b : blockList){
				List <String> secIdList = b.getSectionIdList();
				List <String> newSecIdList = new ArrayList <String>();
				List <String>filterSectionIdList = new ArrayList<String>();
				
				for (String secId: secIdList){
					
					if (siteId==null){
						//General sections
						filterSectionIdList.add(Entity.SEPARATOR +"section"+ Entity.SEPARATOR + secId);	
					}else{
						//Under site sections
						filterSectionIdList.add("/site/" + siteId +  Entity.SEPARATOR +"section"+ Entity.SEPARATOR + secId);
					}
				}
			
			   String permissionToCheck = RegisterHelper.SECTION_READ;
	
			   if ("true".equals(updateOnly)){
				   	permissionToCheck = RegisterHelper.SECTION_UPDATE;
			   }
				
				Set  <String>filtraRead = (Set <String>) authzGroupService.getAuthzGroupsIsAllowed(developerHelperService.getCurrentUserId(),permissionToCheck,filterSectionIdList);
				 
				for (String filteredsecId: filtraRead){
					String finalSectionId = filteredsecId.substring(filteredsecId.lastIndexOf("/")+1);
					if (siteId!=null){
						finalSectionId = finalSectionId + "/site/" + siteId;   
					}
					
					newSecIdList.add(finalSectionId);
				}
				
				if (newSecIdList.size()>0){
					b.setSectionIdList(newSecIdList);
					finalBlockList.add(b);
				}
			}
			Collections.sort(finalBlockList);
			return finalBlockList;
	}
	
	public String[] getHandledOutputFormats() {
	      return new String[] {Formats.XML, Formats.JSON};
	}
	
	public String[] getHandledInputFormats() {
	     return new String[] {Formats.XML, Formats.JSON, Formats.HTML};
	}
	
	
	private List <Block> getAllBlocks(){
				List <Block> blockList = new ArrayList <Block> ();
				List<String> blockIds=null;
				Element blockIdsElement = cache.get(blockCacheIds);
			    
				if (blockIdsElement==null){
			    		blockIds = dataCollectorService.getVisibleBlockIds();
			    }else{
			    		blockIds =  (List<String>) blockIdsElement.getObjectValue();
			    }
				
				for (String block : blockIds){ 
					Element cachedBlock = cache.get(block);
					if (cachedBlock == null){
						blockList.add(dataCollectorService.getBlock(block)); //a cache
					}else{
						blockList.add((Block) cachedBlock.getObjectValue());
					}
				}
				return blockList;
			
			}
			
			public void setDeveloperHelperService(
					DeveloperHelperService developerHelperService) {
				this.developerHelperService = developerHelperService;
			}

			public DataCollectorService getDataCollectorService() {
				return dataCollectorService;
			}

			public void setDataCollectorService(DataCollectorService dataCollectorService) {
				this.dataCollectorService = dataCollectorService;
			}

			public void setRequestStorage(RequestStorage requestStorage) {
				this.requestStorage = requestStorage;
			}

			public TemplateMap[] defineURLMappings() {
			    return new TemplateMap[] {
			    		new TemplateMap("/{prefix}/site/{siteId}",BlockStructureProvider.PREFIX+"{dot-extension}")
			    };
			}

	}
