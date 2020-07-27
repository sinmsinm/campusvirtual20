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
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.CollectionResolvable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RedirectDefinable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestStorable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestStorage;
import org.sakaiproject.entitybroker.entityprovider.extension.TemplateMap;
import org.sakaiproject.entitybroker.entityprovider.search.Restriction;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;

import cat.udl.asic.datacollector.api.entity.Block;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.Section;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataCollectorService;
import cat.udl.asic.datacollector.api.service.InitializeService;
import cat.udl.asic.datacollector.api.service.RegisterHelper;

public class SectionProvider extends AbstractEntityProvider implements CoreEntityProvider, RESTful, RequestStorable, RedirectDefinable {

	public static String PREFIX = "section";
	private RequestStorage requestStorage;
	private DeveloperHelperService developerHelperService = null;
	private DataCollectorService dataCollectorService = null;
	private InitializeService initializeService = null;
	protected Cache cache = null;
	private static Log M_log = LogFactory.getLog(SectionProvider.class);
	private AuthzGroupService authzGroupService;

	public InitializeService getInitializeService() {
		return initializeService;
	}

	public void setInitializeService(InitializeService initializeService) {
		this.initializeService = initializeService;
	}
	
	public void setAuthzGroupService (AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}
	
	public AuthzGroupService getAuthzGroupService () {
		return this.authzGroupService;
	}

	public boolean entityExists(String sectionId) {
		SectionInfo curSection = null;

		if (sectionId == null) {
			return false;
		}
		if ("".equals(sectionId)) {
			return true;
		}

		return true;

	}

	public String getEntityPrefix() {
		return PREFIX;
	}

	public void setRequestStorage(RequestStorage requestStorage) {
		this.requestStorage = requestStorage;
	}

	public TemplateMap[] defineURLMappings() {
		return new TemplateMap[] { new TemplateMap("/{prefix}/{sectionId}/site/{siteId}/datasource/{dataSourceId}/{rowId}", DataSourceProvider.PREFIX + "/{dataSourceId}/{rowId}{dot-extension}"),
				new TemplateMap("/{prefix}/{sectionId}/site/{siteId}/datasource/{dataSourceId}", DataSourceProvider.PREFIX + "/{dataSourceId}{dot-extension}"),
				new TemplateMap("/{prefix}/{sectionId}/site/{siteId}", SectionProvider.PREFIX + "/{sectionId}{dot-extension}"),
				new TemplateMap("/{prefix}/{sectionId}/datasource/{dataSourceId}/{rowId}", DataSourceProvider.PREFIX + "/{dataSourceId}/{rowId}{dot-extension}"),
				new TemplateMap("/{prefix}/{sectionId}/datasource/{dataSourceId}", DataSourceProvider.PREFIX + "/{dataSourceId}{dot-extension}") };
	}

	private Section getSection(String sectionId) {

		Section csection = null;
		String sclear = (String) requestStorage.getStoredValue("clear");
		boolean clearCache=false;
		
		Element sectionElement = cache.get("section:" + sectionId);

		if (sclear!=null && "true".equals(sclear)){
			clearCache = true;
		}
		
		if (sectionElement != null && !clearCache) {
			csection = (Section) sectionElement.getObjectValue();
		}else {
			csection = dataCollectorService.getSection(sectionId);
			Element elem = new Element("section:" + sectionId, csection);
			cache.put(elem);
		}

		return csection;
	}

	private SectionInfo getSectionInfo(String sectionId) {
		SectionInfo curSection = null;

		// procces session id
		Session session = (Session) requestStorage.getStoredValue("sakai.session");
		String siteId = (String) requestStorage.getStoredValue("siteId");
		String sclear = (String) requestStorage.getStoredValue("clear");
		boolean clearCache = false;

		/* If clear first of all clear from cache the section info */

		User user = null;
		Element userDynamicCache = null;

		try {
			user = UserDirectoryService.getUser(developerHelperService.getCurrentUserId());
			userDynamicCache = cache.get("dataCollectorDynamicSession:" + siteId + ":" + session.getId());
		} catch (Exception ex) {
			M_log.debug("No existeix l'usuari");
		}

		// Session session
		String sessionId = session.getId();
		String sectionCachedId = null;

		if (siteId != null) {
			sectionCachedId = siteId + ":" + sectionId + ":" + sessionId;
		} else {
			sectionCachedId = sectionId + ":" + sessionId;
		}
		
		if (sclear!=null && "true".equals(sclear)){
			clearCache = true;
		}
		
		// Get current sectionInfo
		Element cacheElement = cache.get(sectionCachedId);

		if (cacheElement != null && userDynamicCache == null && !clearCache) {
			curSection = (SectionInfo) cacheElement.getObjectValue();
		} else {
			Element cacheElement2 = cache.get("section:" + sectionId);
			Section section = null;

			if (cacheElement2 != null && !clearCache) {
				section = (Section) cacheElement2.getObjectValue();
			} else {
				section = dataCollectorService.getSection(sectionId);
				Element elem = new Element("section:" + sectionId, section);
				cache.put(elem);
			}

			curSection = new SectionInfo(section.getId());
			curSection.setName(section.getName());
			curSection.setDescription(section.getDescription());
			curSection.setLongDescription(section.getLongDescription());
			curSection.setSiteId(siteId);

			Map<String, Parameter> mapParam = initializeService.initialize(developerHelperService, requestStorage);
			curSection.setInitializedParameters(mapParam);

			Element elem = new Element(sectionCachedId, curSection);
			cache.put(elem);
		}
		return curSection;
	}

	public Object getEntity(EntityReference ref) {
		SectionInfo curSection = null;
		Section csection = null;
		String sectionId = ref.getIdFromRef(ref.getReference());
		String siteId = (String) requestStorage.getStoredValue("siteId");

		if (sectionId.startsWith("definition:")) { // Looking for definition or
													// value

			sectionId = sectionId.split(":")[1];
			String refSectionInfo = "/" + PREFIX + "/" + sectionId;

			if (siteId != null) {
				refSectionInfo = "/site/" + siteId + refSectionInfo;
			}

			boolean canRead = authzGroupService.isAllowed(developerHelperService.getCurrentUserId(), RegisterHelper.SECTION_READ, refSectionInfo);
			boolean canUpdate = authzGroupService.isAllowed(developerHelperService.getCurrentUserId(), RegisterHelper.SECTION_UPDATE, refSectionInfo);

			if (canRead || canUpdate) {
				csection = getSection(sectionId);
			} else {
				throw new SecurityException("You are not allowed to read that section definition");
			}
			return csection;
		} else {
			String refSection = "/" + PREFIX + "/" + sectionId;

			if (siteId != null) {
				refSection = "/site/" + siteId + refSection;
			}

			boolean canUpdate = authzGroupService.isAllowed(developerHelperService.getCurrentUserId(), RegisterHelper.SECTION_UPDATE, refSection);
			boolean canRead = authzGroupService.isAllowed(developerHelperService.getCurrentUserId(), RegisterHelper.SECTION_READ, refSection);

			if (canRead || canUpdate) {
				curSection = getSectionInfo(sectionId);
				curSection.setUpdatable(canUpdate);
			} else {
				throw new SecurityException("You are not allowed to read that section");
			}

			return curSection;
		}
	}

	public void deleteEntity(EntityReference ref, Map<String, Object> params) {
		throw new SecurityException("Delete operation is not permitted");
	}

	public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
		throw new SecurityException("Create sections is an unsave operation and is not allowed");
	}

	public Object getSampleEntity() {
		return new SectionInfo("sample");
	}

	public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
		throw new SecurityException("Update sections is an unsafe operation and is not allowed");
	}

	public List<?> getEntities(EntityReference ref, Search search) {
		throw new SecurityException("Gettiong Sections list operation is not permitted for security reasons");
	}

	public String[] getHandledOutputFormats() {
		return new String[] { Formats.XML, Formats.JSON, Formats.HTML };
	}

	public String[] getHandledInputFormats() {
		return new String[] { Formats.XML, Formats.JSON, Formats.HTML };
	}

	public DeveloperHelperService getDeveloperHelperService() {
		return developerHelperService;
	}

	public void setDeveloperHelperService(DeveloperHelperService developerHelperService) {
		this.developerHelperService = developerHelperService;
	}

	public DataCollectorService getDataCollectorService() {
		return dataCollectorService;
	}

	public void setDataCollectorService(DataCollectorService dataCollectorService) {
		this.dataCollectorService = dataCollectorService;
	}

	/**
	 * @return the cache
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * @param cache
	 *            the cache to set
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}

}
