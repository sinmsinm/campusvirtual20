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

//Policy management

//the value var is an object with these structure:
// 'id' -> realm to update
// AnyNameOfUserProperty -> can be any valid property javascript name and inside has to contain three properties:
//	'login' of the user
//				'action', can be grant or revoke
//				'role', in case of grant we need the role to assign to the user
function Policy(value) {
	this.users = {};
	this.policy = {};
	this.policy.value = {};
	this.permisosActuals = '';
	this.datasource = '';
	
	if (value !== undefined) {
		if (value.id != undefined) {
			this.setPolicyId(value.id);
		}
		var pol = this;
		$.each(value, function(item, itemVal) { 
			if(item != 'id')
				pol.addPolicyUser(value[item].login,value[item].action,value[item].rol);
		});
	}
}
		
Policy.prototype.setPolicyId = function(realm){	//add realm to an existing policy
	this.policy.id = realm;
	return this;
}

Policy.prototype.addPolicyUser = function(user, permis, rol){	//add user and role to an existing policy
	this.users[user] = {};
	this.users[user].permis = permis;
	this.users[user].rol = rol;
	this.policy.value[user] = (permis == 'grant')?permis + ':' + rol : permis;
	return this;
}

Policy.prototype.setPermisosActuals = function(perms){
	this.permisosActuals = perms;
	return this;
}

Policy.prototype.getPolicy = function(){
	return this.policy;
}

Policy.prototype.save = function(datasource, manageState, reqOptions){
	
	this.datasource = datasource;
	setData(section, this.getPolicy(), datasource + '/site', function(){
			if(options['screenreshow'] != undefined){
		   	    renderScreen(section,container,data,pantalles,options['screenreshow'],'defaultCase','');
			}
				
			if(options['notificationbox'] != undefined)
				$(options['notificationbox']).empty().append('Dades guardades correctament').slideDown('slow', function(){
					setTimeout('$(\'' + options['notificationbox'] + '\').slideUp(\'slow\')', 5000);
				});
	},reqOptions, manageState);
	return this;
}
 		
Policy.prototype.hasChanged = function(){

	if(this.permisosActuals == undefined)
		return false;
	for (var user in this.users) {

		if(hasChanged(this.policy.id, user, this.users[user].permis, this.users[user].rol,this.permisosActuals))
			return true;
	}
	return false;
}
 		
Policy.prototype.saveIfChanged = function(datasource, manageState, reqOptions){

	this.datasource = datasource;
	if(this.hasChanged())
		this.save(datasource, manageState, reqOptions);
	return this;
}


var parentEvent = curaction.parentEvent;
var options = curaction.options;
var eventType = parentEvent.type;
var eventSrc = parentEvent.source;
var actionTarget = curaction.target;
var value = localData[curaction.value];
var cauPermisosActuals = localData[section] == undefined ? getPermisosActuals(section, actionTarget) : localData[section];

var manageState = {};
if(curaction.id != undefined){
	manageState.idAction = curaction.id;
	manageState.src = parentEvent.source;
}

//one realm, one save...
if(value.id != undefined && typeof(value.id) == 'string'){

	var perm = new Policy(value);
	perm.setPermisosActuals(cauPermisosActuals);
	perm.saveIfChanged(actionTarget,manageState);
	
}else{

	//one save for multiple realms 
	$.each(value, function(permis, valor) { 
		var perm = new Policy(value[permis]);
		perm.setPermisosActuals(cauPermisosActuals);
		perm.saveIfChanged(actionTarget,manageState);
	});
}
//netegem variable on s'han emmagatzemant els permisos a desar
localData[curaction.value] = undefined;





//other support functions...		
function hasChanged(realm, user, tipusPermis, rol, permisosActuals){

	var rolActual = getRol(realm, user, permisosActuals);

	//alert('ACTUAL: ' + rolActual + ' NOU: ' + tipusPermis + ' ' + rol);
	if(rolActual == rol && tipusPermis == 'grant:')
		return false;
	else if(rolActual == '' && tipusPermis == 'revoke')
		return false;
	else
		return true;
}
	
function getRol(realm, user, permisosActuals){

	var realmSensePunts = (realm.substring(0,1) == ':') ? realm.substring(1,realm.length) : realm; 
	if(permisosActuals.value[realmSensePunts]!= undefined && permisosActuals.value[realmSensePunts].value[user] != undefined)
		return permisosActuals.value[realmSensePunts].value[user];
	else
		return "";
}

function getPermisosActuals(section, datasource){
	var permisosActuals;
	var url = "/direct/section/" +  section + "/datasource/" + datasource + ".json?sid=" + Math.random();
	$.ajax({
		type: "GET",
		url: url,
		dataType: "json",
		cache: false,
		async: false,
		success: function (data){ permisosActuals = data;}
	});
	return permisosActuals;
}