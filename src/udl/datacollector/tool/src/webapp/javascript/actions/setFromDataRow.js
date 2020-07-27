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
* Authors: Alex Ballesté i Xavier Noguero
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat), Xavier Noguero <xnoguero@asic.udl.cat> and usuaris-cvirtual@llistes.udl.cat
* Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
*
**/ 

					var parentEvent= curaction.parentEvent;
					var eventType = parentEvent.type;
					var options = curaction.options;
					var eventSrc = parentEvent.source;
					var actionTarget = curaction.target; 
					var value = curaction.value;
					var variables = actionTarget.split(".");
					
					var workingVariable = localData;
					var reference = options['reference']
					
					var originalVariable = data[reference];
					
					for (n = 0; n< variables.length -1; n++){ // Initialize non created variables
						   var strFn = variables[n];
						   if (workingVariable[strFn]==undefined){
						   		workingVariable[strFn] = new Object();
						   }
						   
						   workingVariable = workingVariable[strFn];
					}   
					
					var strFn = variables[variables.length-1];
					
					// Assign value 
					if (value.substr(0, 1) == "$"){//take the value from dom
						var varid = getObjectFromSelector(value, referenceObject);
						var text = new Object ();
						
						varid.filter(':input').each(function(){
							text = $(varid).val();
						});
						
						varid.filter(':not(:input)').each(function(){
							text = $(varid).text();
						});
						
						workingVariable[strFn] =originalVariable.value[text]; 
					}else{//take the value from other variable
						var varid = value;
						workingVariable[strFn] =originalVariable[text];
					}					
