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

package cat.udl.asic.datacollector.api.service;

import java.util.Map;


import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestStorage;

import cat.udl.asic.datacollector.api.entity.Parameter;

public interface InitializeService {
			public Map <String,Parameter> initialize(DeveloperHelperService devHelperInstance,RequestStorage reqStorageService);
			public void addProperty (String siteId,String name, String value);
}
