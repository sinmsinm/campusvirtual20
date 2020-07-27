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
		//Action Manager: it activates all the actions of an screen, easy actions referring to common tasks of an screen and actions of data events 
		function activateDefaultEvents(section, container, data, pantalles, screenId){

			//segon param opcional per especificar un selector concret per tal que només s'assignin events de les actions
			// sobre ell, la resta de actions es despreciaran


			var screenObj = pantalles[screenId];
			var compSrcSelected;
			var screenValidators = new Array();
			var form;
			
			$("validator", screenObj).each (function (i){
					
					if ($('#' +screenId).find("form.validatorForm").length == 0 ){//wrap the form
							form = $(document.createElement("form"));
							$(form).attr('class','validatorForm'); 
							$('#' +screenId).children().wrapAll(form);
					}
					
					var classname = $(this).attr("type");
					//include the option value en classname

					var value = getObjectFromSelector($(this).attr("value"), screenObj);
					var target = $(this).attr("target");
					$(value).data('msgErrorContainer',target);

					if($(this).attr("options") != undefined && $(this).attr("options") != '{}'){
						var options = eval('(' + $(this).attr("options") +')');
						classname = classname + Math.random();
						$.validator.addClassRules(classname,options);
					}
					$(value).addClass(classname);
					
					$(value).keyup(function(){ 
						isValid(section, container, data, pantalles, screenId);
					});
			});

			$("event[type!='onSuccess'],event[type!='onFail']", screenObj).each (function (i){
					/*Get the attributes*/
					var event = getDataEvent($(this));

					//definim events onSuccess i onFail sobre actions...
					jQuery.each(event['actions'], function(i, action) {
					
						if(action['id'] != undefined)
							activateErrorManagementEvent(screenObj, section, container, data, pantalles, screenId, action['id'], event['source']);
					});
					
					$(event['source']).data('event', event);
					

					if (event['type']=='exec'){
						executeActions (event,section,container,data,pantalles,screenId,null);

					}else if (event['type']=='hover'){
							var eventEnterHover = new Object();
							var eventExitHover = new Object();

							eventEnterHover['type'] = 'hover';
							eventEnterHover['source'] = event['source'];
							eventExitHover['type'] = 'hover';
							eventExitHover['source'] = event['source'];
							
							var enterActions = new Array();
							var exitActions = new Array();
							
							var actions = event['actions'];  
							
							for (i=0;i< (actions.length/2); i++){
								enterActions[i] = actions[2*i];	
								exitActions[i] = actions[2*i+1];
							}
							eventEnterHover['actions']= enterActions;
							eventExitHover['actions'] = exitActions;
							
							$(event['source']).data('eventEnter', eventEnterHover);
							$(event['source']).data('eventExit', eventExitHover);
							
							$(event['source']).hover(function (){
										var locevent = $(this).data('eventEnter');	
										executeActions (locevent,section,container,data,pantalles,screenId,this);	
							},function(){
										var locevent = $(this).data('eventExit');
										executeActions (locevent,section,container,data,pantalles,screenId,this);
							});
					}else if(event['type']=='validForm'){

							var form = $('#' +screenId).find("form.validatorForm");
							
							$(form).data('validEvent',event);

								$("input").attr("name", function (arr) {
								  if($(this).attr("name") == undefined || $(this).attr("name") == "")
						          	return "name-input-" + arr;
								  else 
								  	return $(this).attr("name");
					    	});
							
							jQuery.validator.setDefaults({
									errorPlacement: function(error, element){
										if(element.data('msgErrorContainer') != undefined){
											//$(element.data('msgErrorContainer')).empty();
											error.appendTo( $(element.data('msgErrorContainer')) );
										}else{
											error.insertAfter(element);
										}
									},
									unhighlight: function( element, errorClass, validClass ) {
										if($(element).data('msgErrorContainer') != undefined)
											$($(element).data('msgErrorContainer')).empty();
										$(element).removeClass(errorClass).addClass(validClass);
									}
							});

							initializeValidator();
							$(form).validate();

							if ($(form).data('invalidEvent') != undefined){
								isValid(section, container, data, pantalles, screenId);
							}

							var espaiNoms = '.namespace' + (Math.random() *10);
							$(document).bind('click' + espaiNoms, function(){ 
								if($('#'+ screenId).find("form.validatorForm").validate() != undefined){
									isValid(section, container, data, pantalles, screenId);
								}else{
									$(document).unbind(espaiNoms);
								}
							});
					
					}else if (event['type'] == 'invalidForm'){
							var form = $('#' + screenId).find("form.validatorForm");
							$(form).data('invalidEvent',event);
							if ($(form).data('validEvent') != undefined){
								isValid(section, container, data, pantalles, screenId);
							}
					}else{
			
						$(event['source']).bind(event['type'], {ev: event}, function (event){
								//var locevent = $(this).data('event');
								if(!$(this).hasClass("ui-state-disabled"))
									executeActions (event.data.ev,section,container,data,pantalles,screenId,this);
						});
					}
			});
		}
			
	function executeActions (event,section,container,data,pantalles,screenId,referenceObject)	{
			var actions = event['actions'];  
		    var screenIdTmp = screenId;
			$(event['actions']).each(function(index, element){

					var curaction = element;
					var actionType = curaction['type'];
					
					if (registeredAction[actionType] != undefined){
							registeredAction[actionType].executeAction (curaction,section,container,data,pantalles,screenId,referenceObject);
					}else{
			
						$.ajax({
										url: "./javascript/actions/"+curaction.type+".js",
										cache: true,
										dataType: "text",
										async: false,
										success: function (js){
											if (registeredAction==undefined){
												registeredAction = new Object();
											}
											registeredAction[actionType] = new Object();
											registeredAction[actionType].executeAction = new Object(); 
											registeredAction[actionType].executeAction = new Function ("curaction","section","container","data","pantalles","screenId","referenceObject", js);
											registeredAction[actionType].executeAction (curaction,section,container,data,pantalles,screenIdTmp,referenceObject);
										}
								});
					}
					curaction = element;
			});
	}
			
	function isValid (section, container, data, pantalles, screenId)	{

				var form = $('#' + screenId).find("form.validatorForm");
				//form v�lid, fem les actions per quan es v�lid
				if (form.validate().formWithoutShowingErrors()){
						var locevent = $(form).data('validEvent');
						executeActions (locevent,section,container,data,pantalles,screenId,form);
				}else{
						var locevent = $(form).data('invalidEvent');
						executeActions (locevent,section,container,data,pantalles,screenId,form);
				}
	} 		

		function getObjectFromSelector(goal, jThisObjectJS){
			
			goal = goal.replace("(this)", "(jThisObjectJS)");
			goal = goal.replace("(this,", "(jThisObjectJS,");
			goal = goal.replace("(this.", "(jThisObjectJS.");

			if (goal.substr(0, 1) == "$"){
				var po = eval(goal);
				return po;
			}
			else
				return $(goal);
		}
		
		function isDefined(object, variable)
		{
			var variables = variable.split(".");
			for (var i = 0; i < variables.length; i++) {
				
				log("[isDefined] OBJECT: " + object + " VARIABLE: " + variables[i] + " Evaluacio: ");
				log(typeof(eval(object)[variables[i]]) == 'undefined' || eval(object)[variables[i]] == null);
				if (typeof(eval(object)[variables[i]]) == 'undefined' || eval(object)[variables[i]] == null)
					return false;
				object = object + '.' + variables[i]; 
			}
			return true;
		}
		
		
		function getDataEvent($eventTag){
			var event = new Object ();
			event['type'] = $eventTag.attr("type");
			event['source'] = $eventTag.attr("src"); 
			event['chain'] = $eventTag.attr("chain");
			event['actions'] = new Array ();
			
			var action;
			$eventTag.find ("action").each (function (j){

				 action = new Object();
				 action['target'] = $(this).attr("target");
				 action['type']  = $(this).attr("type");
				 if($(this).attr("options") != undefined)
					 action['options'] = eval("(" + $(this).attr("options") +')');
				 else
					 action['options'] = {};
				 action['value'] = $(this).attr("value");
				 action['parentEvent'] = event;
				 if($(this).attr("id") != undefined)
					action['id'] = $(this).attr("id");

				 //event['actions'][j]= action;
				 event['actions'].push(action);
				 
				 if($(this).attr("async") != undefined)
						action['async'] = $(this).attr("async");				 
			});
			return event;
		}

		function activateErrorManagementEvent(screenObj, section, container, data, pantalles, screenId, idEvent, srcEvent){

			var event;
			$("event[type='onSuccess'][src='" + idEvent + "'],event[type='onFail'][src='" + idEvent + "']", screenObj).each (function (i){

				event = getDataEvent($(this));
				if(event['source'] != undefined)
					event['type'] += event['source'];

				//si de cas, assegurem que src no tingui el mateix event activat d'altres ocasions passades
				$(srcEvent).unbind(event['type']);
				$(srcEvent).bind(event['type'], {ev: event},function (event){
						executeActions (event.data.ev,section,container,data,pantalles,screenId,null);
				});
			});
		}