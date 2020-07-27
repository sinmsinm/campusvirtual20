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
					var eventSrc = parentEvent.source;
					var actionTarget = curaction.target; 
					var value = curaction.value;
					var variablesw = actionTarget.split(".");
					var variablesr= value.split(".");
					var workingVariable = localData;
					var originVariable = localData; 
					
					for (n = 0; n< variablesw.length -1; n++){ // Initialize non created variables
						   var strFnw = variablesw[n];
						   if (workingVariable[strFnw]==undefined){
						   		workingVariable[strFnw] = new Object();
						   }
						   
						   workingVariable = workingVariable[strFnw];
					}  
					
		
					for (m = 0; m< variablesr.length; m++){ // Initialize non created variables
						   var strFnr = variablesr[m];
						   if (originVariable[strFnr]==undefined){
						   		break;
						   }
						   
						   originVariable = originVariable[strFnr];
					}  
	
					var strFnw = variablesw[variablesw.length-1];
					workingVariable[strFnw] = originVariable;
					