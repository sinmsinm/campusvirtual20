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
* Authors: Alex Ballest� i Xavier Noguero
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballest� (alex@asic.udl.cat), Xavier Noguero <xnoguero@asic.udl.cat> and usuaris-cvirtual@llistes.udl.cat
* Universitat de Lleida  Pla�a V�ctor Siurana, 1  25005 LLEIDA SPAIN
*
**/ 
					var lloc="container";
					var situacio="defaultcase";

					var parentEvent= curaction.parentEvent;
					var options = curaction.options;
					var eventType = parentEvent.type;
					var eventSrc = parentEvent.source;
					var extraoptions = options['extraoptions'];
					
					var actionTarget = curaction.target; 

	
					if (options["dialog"] != undefined && options["dialog"] == 'true') {
						situacio = "dialog";
						lloc = "dialog";
					}else if(options['after'] != undefined) {
						lloc = options['after'];
						situacio = "after";
					}
					else if (options["before"] != undefined) {
						lloc = options["before"];
						situacio = "before";
					}
					else if (options["prepend"] != undefined) {
						lloc = options["prepend"];
						situacio = "prepend";
					}else if (options ["append"] != undefined) {
						lloc = options ["append"];
						situacio = "append";
					}
					renderScreen(section, container, data, pantalles, actionTarget, situacio, getObjectFromSelector(lloc, referenceObject),extraoptions);
					
					
					
					