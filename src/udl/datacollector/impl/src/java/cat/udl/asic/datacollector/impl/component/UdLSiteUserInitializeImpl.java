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

/**
 * This class is a very close implementation of the initializeService each institution that uses must implement its own.
 */

package cat.udl.asic.datacollector.impl.component;

import java.util.Map;

import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestStorage;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import cat.udl.asic.datacollector.api.entity.Parameter;
import cat.udl.asic.datacollector.api.service.InitializeService;
import java.util.Properties;
import java.util.Map.Entry;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import java.util.Enumeration;
import org.sakaiproject.tool.api.Session;

import java.util.Locale;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.cover.PreferencesService;
import org.sakaiproject.i18n.InternationalizedMessages;


public class UdLSiteUserInitializeImpl implements InitializeService {
	private Map <String,Properties> sitePropertiesMap ;
	protected Cache cache = null;
	private static final String LOCALE_PREFS = "sakai:portal:sitenav";
	
	public Map<String, Parameter> initialize(
			DeveloperHelperService devHelperInstance,
			RequestStorage reqStorageService) {
			String siteId = (String) reqStorageService.getStoredValue("siteId");
			Session session = (Session)  reqStorageService.getStoredValue("sakai.session");
			String sessionId = session.getId();
			Map <String, Parameter> map = new java.util.HashMap<String, Parameter> ();
		
			try{
				User user = UserDirectoryService.getUser(devHelperInstance.getCurrentUserId()); 
				String location = devHelperInstance.getCurrentLocationId();
				String sdni = (String) user.getProperties().get("dni");
				String seid = (String) user.getEid();
				//String locale = (String) devHelperInstance.getCurrentLocale().toString();
				String locale = (String) getLocale(devHelperInstance.getCurrentUserId()).toString();
				
				Parameter dni = new Parameter ("dni",sdni,"NO_COLUM"); // Hauria de ser el dni
				map.put (dni.getId(),dni);
				Parameter eid = new Parameter ("eid",seid,"NO_COLUMN");
				Parameter plocale = new Parameter ("locale",locale,"NO_COLUMN");
				
				/*Tool registered properties*/
				if (siteId != null && !siteId.equals("") && sitePropertiesMap!= null){
						Properties siteProps = sitePropertiesMap.get(siteId);
						if (siteProps!=null){
								for (Entry entry : siteProps.entrySet()){
										Parameter param = new Parameter ((String) entry.getKey(),(String) entry.getValue(),"NO_COLUMN");
										map.put (param.getId(),param);
								}
						}
				}
				map.put (dni.getId(), dni);
				map.put (eid.getId(), eid);
				map.put (plocale.getId(), plocale);
				
				if (siteId != null && !siteId.equals("")){
					/*Finalment reescribim totes les variables de sessio*/
					Element cacheElement = cache.get("dataCollectorDynamicSession:" + siteId + ":" + sessionId);
					Properties userProperties = null;
					
					if (cacheElement !=null){
						userProperties = (Properties) cacheElement.getObjectValue();
						if (userProperties != null){
							Enumeration propNames = userProperties.propertyNames();
							while (propNames.hasMoreElements()){
								String propName = (String) propNames.nextElement();
								Parameter aux = new Parameter (propName,userProperties.get(propName),"NO_COLUM"); // Hauria de ser el dni
								map.put (propName,aux);
							}
						}
					}
				}

		}catch (Exception ex){
				ex.printStackTrace();
		}
		
		return map;
	}
	
	
	public void addProperty (String siteId,String name, String value){ 
			if (sitePropertiesMap== null){
				sitePropertiesMap = new java.util.HashMap<String,Properties>();
			}
			
			Properties siteProps = sitePropertiesMap.get(siteId);
			if (siteProps==null){
				siteProps = new Properties ();
			}
			
			siteProps.put(name,value);
			sitePropertiesMap.put(siteId,siteProps);
	}
	
	public Cache getCache()
	{
		return cache;
	}

	public void setCache(Cache cache)
	{
		this.cache = cache;
	}
	
	
	public Locale getLocale(String userId)
	{
		Locale loc = null;
		Preferences prefs = PreferencesService.getPreferences(userId);
		ResourceProperties locProps = prefs.getProperties(InternationalizedMessages.APPLICATION_ID);
		String localeString = locProps.getProperty(InternationalizedMessages.LOCALE_KEY);

		if (localeString != null)
		{
			//l'usuari té locale definit a les propietats
			String[] locValues = localeString.split("_");
			if (locValues.length > 2)
				loc = new Locale(locValues[0], locValues[1], locValues[2]); // language, country, variant
			else if (locValues.length == 2)
				loc = new Locale(locValues[0], locValues[1]); // language, country
			else if (locValues.length == 1)
				loc = new Locale(locValues[0]); // just language
		}else{
			
			//agafem el locale per defecte
			loc = Locale.getDefault();
			
			//si no hi ha locale per defecte ni l'usuari té locale definit, retornem locale en català
			if(loc == null){
				loc = new Locale("ca","ES"); // language, country
			}
		}
		return loc;
	}
	
	
}