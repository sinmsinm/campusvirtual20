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

var parentEvent= curaction.parentEvent;
var options = curaction.options;
var eventType = parentEvent.type;
var eventSrc = parentEvent.source;
var actionTarget = curaction.target;
var value = curaction.value;
var async = curaction.async;
var deleteid = undefined;

var manageState = {};
if(curaction.id != undefined){
	manageState.idAction = curaction.id;
	manageState.src = parentEvent.source;
}

if (value != undefined){

			if (value.substr(0, 1) == "$"){//take the value from dom
					var varid = getObjectFromSelector(value, referenceObject);
					 
					 var text = new Object ();
						
						varid.filter(':input').each(function(){
							text = $(varid).val();
						});
						
						varid.filter(':not(:input)').each(function(){
							text = "" + $(varid).text();
						});
						
						deleteid = text;
					}else{
						deleteid= value;
					}
}

//gestionar crides ASYNCRONES
if(async == "true") async = true;
else async = false;	

deleteData(section,deleteid, actionTarget, function(){
		if(options['screenreshow'] != undefined)
					 renderScreen(section, container, data, pantalles, options['screenreshow'],"defaultCase","");
		if(options['notificationbox'] != undefined)
				$(options['notificationbox']).empty().append('Dades esborrades correctament').slideDown('slow', function(){
										setTimeout('$(\'' + options['notificationbox'] + '\').slideUp(\'slow\')', 5000);
				});
}, manageState, async);
