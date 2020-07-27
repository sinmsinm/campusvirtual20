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
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat),   and usuaris-cvirtual@llistes.udl.cat
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
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import java.util.Properties;
//import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cat.udl.asic.datacollector.api.entity.DataSource;
import cat.udl.asic.datacollector.api.entity.DataSourceColumn;
import cat.udl.asic.datacollector.api.entity.DataSourceValue;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.entity.SectionInfo;
import cat.udl.asic.datacollector.api.service.DataSourceService;

public class DynamicPropertiesDataSourceImpl implements DataSourceService {
		protected Cache cache = null;
		
		private static Log M_log = LogFactory.getLog(DynamicPropertiesDataSourceImpl.class);
		
		
		public DataSourceValue getDataSourceValue (SectionInfo currentSection,DataSource dataSource){
				DataSourceValue retDataSource = null;
				try{
					//User user = UserDirectoryService.getCurrentUser();   
					Session sakaiSession = SessionManager.getCurrentSession();
					Element cacheElement = null;
					Properties userProperties = null;
					Map value = new LinkedHashMap <String,Object>();
					Object propVal = null;
					
					if(currentSection.existsSiteId()){
						cacheElement = cache.get("dataCollectorDynamicSession:" + currentSection.getSiteId() + ":" + sakaiSession.getId());
						if (cacheElement !=null){
							userProperties = (Properties) cacheElement.getObjectValue();
							propVal = userProperties.get(dataSource.getId());
						}
					}
					
					if(propVal == null)
						value.put ("simple.value","Value not found");
					else
						value.put ("simple.value",propVal);
					
					retDataSource = new DataSourceValue (dataSource.getId(),currentSection.getId(),value,currentSection.isUpdatable() && dataSource.isUpdatable());
					
				}catch (Exception ex){
					M_log.debug("Error recuperant propietats");
					ex.printStackTrace();
				}				
				 return retDataSource;
		}
		public boolean saveDataSourceValue(SectionInfo currentSection,DataSource dataSource,DataSourceValue dataSourceValue){
					Properties p = new Properties ();
					
					boolean saved=false;
					Session sakaiSession = SessionManager.getCurrentSession();
					
					try{
						if(currentSection.existsSiteId()){
							//User user = UserDirectoryService.getCurrentUser();   
							Element cacheElement = cache.get("dataCollectorDynamicSession:" + currentSection.getSiteId() + ":" + sakaiSession.getId());
								
							if (cacheElement == null){
								cacheElement = new Element ("dataCollectorDynamicSession:" + currentSection.getSiteId() + ":" +sakaiSession.getId(), new Properties());
								cache.put(cacheElement);
							}
							Properties props = (Properties) cacheElement.getObjectValue();
							
							if (DataSource.SIMPLE_TYPE.equals(dataSource.getType())){
								Map value = (Map) dataSourceValue.getValue();
								props.put (dataSource.getId(),value.get(DataSource.SIMPLE_TYPE));
								saved=true;
							}else{
								//NOT IMPLEMENTED
								saved=false;
							}
						}
						
					}catch (Exception ex){
						M_log.debug("Error al desar les propietats");
						ex.printStackTrace();
					}
					return saved;
		}
		public boolean deleteDataSourceValue(SectionInfo currentSection,DataSource dataSource,String key){
			try{
				if(currentSection.existsSiteId()){
					//User user = UserDirectoryService.getCurrentUser();   
					Session sakaiSession = SessionManager.getCurrentSession();
					Element cacheElement = cache.get("dataCollectorDynamicSession:" + currentSection.getSiteId() + ":" + sakaiSession.getId());
					Properties userProperties = null;
			
					if (cacheElement !=null){
						userProperties = (Properties) cacheElement.getObjectValue();
						Object propVal = userProperties.get(dataSource.getId());
						
						if (propVal!=null){
							userProperties.remove(dataSource.getId());
							return true;
						}else{
							return true;
						}
					}else{
						return true;
					}
				}
				
			}catch (Exception ex){
				M_log.debug("Error recuperant propietats");
				ex.printStackTrace();
			}				
			return false;
		}

		public Cache getCache()
		{
			return cache;
		}

		public void setCache(Cache cache)
		{
			this.cache = cache;
		}
}
