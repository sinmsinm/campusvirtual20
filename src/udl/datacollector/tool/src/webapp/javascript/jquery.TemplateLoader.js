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

var registeredAction = new Object (); 
var localData = new Object();


(function($){
	
	function debug(s) {
		if ($.fn.TemplateLoader.debug)
			log(s);
	}		

	
	$.fn.TemplateLoader = function(section, options) {  

		//defalut options
		
		
		var defaults = {
			firstScreen: "screen0",
			validatorFunction: "initializeValidator()",
			validatorMethodsFunction: "addMethodsValidator()",
			effectHide: "",
			effectShow: ""
		};
		var options = $.extend(defaults, options);
		
		
		if(typeof section == "undefined"){
			log('section parameter not set! Finishing...');
			return this.each();
		}
		
		return this.each(function() {  
			  
			//plugin goes and starts here!
			var pantalles = new Array();
			
			window.dataTemplateLoader = new Object();	
			var data = new Array();
			var container = this;
			var data = new Object();
			
			var startScreen = options.firstScreen;
			var messages = options.messages; 
			
			getSectionDefinition (null,null,section,function (acurblock,atype,cursection){
				var template = cursection["viewTemplate"];
				 
				localData['sectionInfo'] = cursection;
				

				getSection (null,null,section,function (bcurblock,btype,cursectioni){
					var locale = "ca_ES";
					if(cursectioni != undefined && 
					   cursectioni.initializedParameters != undefined && 
					   cursectioni.initializedParameters.locale != undefined && 
					   cursectioni.initializedParameters.locale.value != undefined)
						locale = cursectioni.initializedParameters.locale.value;
					var messagesUrl = null;
					
					if (cursection["viewMessages"] != null && cursection["viewMessages"] != ""){
						var messagesUrl = cursection["viewMessages"] + locale;
					}
				     
					localData['locale'] = locale;

					//cau d'importació d'screens
					//tots els identificadors d'screen que es carreguin es guarden dins la variable "templatesLoaded" al body 
					$('body').removeData('templatesLoaded');
					var templatesLoaded = new Array();
					templatesLoaded.push(template);
					$.data(document.body, 'templatesLoaded', templatesLoaded);
					
					getTemplate(template,messagesUrl,undefined, function(temp,messages){
						if (messages != null){
							localData['messages'] = messages;
						}	
						var elem = document.createElement('div');
						$(elem).html(temp);
						var screens, screensDef;
						screens = $(".screen", elem);
						screensDef = new Array(screens.length);
						for (var i=0; i<screens.length; i++) {
							screensDef[$(screens[i]).attr("id")] = screens[i];
						}
						$(elem).empty().remove();
						pantalles = screensDef;
						renderScreen(section, container, data, pantalles, startScreen, undefined, undefined,undefined);
						});
				});
			});
		});
	};
})(jQuery);  

		function log() {
			if (window.console && window.console.log)
				window.console.log('[TemplateLoader] ' + Array.prototype.join.call(arguments,' '));
		};

		function renderScreen(section, container, data, pantalles, screenId, position, anchorRef,extraoptions){

				var screenObj = pantalles[screenId];
				var paramUrlJson, internalRef,localVariable,dynamicOptions;
				
				$(screenObj).find("required").each(function(){

					paramUrlJson = $(this).attr("json");
					internalRef = $(this).attr("json");
					localVariable = $(this).attr("local");
					dynamicOptions = new Array();
					
					var rows =$(this).attr("rows");
					var page =$(this).attr("page");
					var orderby =$(this).attr("orderby");
					var ordertype =$(this).attr("ordertype");
					var containsWord =$(this).attr("containsWord");
					var searchable =$(this).attr("searchable");
					
					//Afegim la variable params per les crides request amb paràmetres als requireds			
					var params = $(this).attr("params");
					var paramIndex = 0;
					var async =$(this).attr("async");

					//gestionem events onSuccess i onFail...		
					var manageState = {};
					if($(this).attr("id") != undefined){
						manageState.idAction = $(this).attr("id");
						manageState.src = "body";
						activateErrorManagementEvent(screenObj, section, container, data, pantalles, screenId, $(this).attr("id"), "body");
					}

					if (rows != undefined && page != undefined){
							var oRows = new Object ();
							var oPage = new Object();
							
							oPage.name= "page";
							oRows.name = "rows";
						
							
							if (("" + parseInt(rows)) == rows){//Its a number
								oRows.value = parseInt(rows);
								dynamicOptions[paramIndex] = oRows;
								paramIndex++;
							}else { //Its a variable name
								var value = eval ("localData."+rows);
								oRows.value = value;
								dynamicOptions[paramIndex] = oRows;
								paramIndex++;
							}
							
							if (("" + parseInt(page)) == page){//Its a number
								oPage.value = parseInt(page);
								dynamicOptions[paramIndex] = oPage;
								paramIndex++;
							}else { //Its a variable name
								oPage.value = eval ("localData."+page);
								dynamicOptions[paramIndex] = oPage;
								paramIndex++;							
							}
					}
						
					if (containsWord !=undefined && searchable != undefined){
							var containsLocal = eval ("localData."+containsWord); 
						    var searchableLocal = eval ("localData." + searchable);
						    
						    if (containsLocal == undefined){
						    	containsLocal = containsWord;
						    }
						    
						    if (searchableLocal == undefined){
						    	searchableLocal = searchable;
						    }
				
						   var oSearch = new Object();
						   oSearch.name="searchable";
						   oSearch.value=searchableLocal;
						
						   var oContains = new Object();
						   oContains.name="contains";
						   oContains.value=containsLocal;
							
							dynamicOptions[paramIndex] = oContains;
							paramIndex++;
							dynamicOptions[paramIndex] = oSearch;
							paramIndex++;
						
					}
						
					if (orderby !=undefined && ordertype != undefined){
  						var orderbyLocal = eval ("localData."+orderby); 
						var ordertypeLocal = eval ("localData." + ordertype);
						   		 
						if (orderbyLocal == undefined){
							orderbyLocal = orderby;
		   				 }
						    
			    		if (ordertypeLocal == undefined){
			    			ordertypeLocal = ordertype;
		    			}
				
					    var oOrdert = new Object();
					    oOrdert.name="ordertype";
					    oOrdert.value=ordertypeLocal;
					
					    var oOrderb = new Object();
					    oOrderb.name="orderby";
						oOrderb.value=orderbyLocal;
							
						dynamicOptions[paramIndex] = oOrderb;
						paramIndex++;
						dynamicOptions[paramIndex] = oOrdert;
						paramIndex++;
					}
					
//afegim un altre if per controlar si hi ha el paràmetre "params". Aquest canvi és per afegir els paràmetres a les crides request del required					
					if (params != undefined){
						var llistParam = eval ("localData." + params);
						
						for (var key in llistParam){
							var paramAct = {};
							paramAct.name = key;		
							paramAct.value = llistParam[key];
							dynamicOptions[paramIndex] = paramAct;
							paramIndex++;
						}
						
					}
					
					if($(this).attr("ref") != undefined)
						internalRef = $(this).attr("ref");
					
					var field1 = $(this).attr("field1");
					var paramJson = "";
					
					//gestionar crides ASYNCRONES
					if(async == "true") async = true;
					else async = false;	

					if (localVariable == undefined && paramUrlJson!= undefined){

							getRequiredDynamicData(section, paramUrlJson, dynamicOptions, function(dades){
									data[internalRef] = dades;
							},manageState, async);
					}else{
						data[internalRef] = localData[localVariable];
					}

					localVariable == undefined;
					paramUrlJson!= undefined;
					
				});
				deleteScreen(container, screenId);
				
				var jstCodeCloned = ($(screenObj).find('textarea:first').clone()).val();
				var result;
		
				if ($(screenObj).find('required').length > 0){
					result = TrimPath.parseTemplate(jstCodeCloned).process(data); 
				}else
					result = jstCodeCloned;
								
				result = $('<div>').append($(screenObj).clone().empty().append(result)).remove().html();
				injectScreenToDOM(container, result, position, anchorRef, screenId,extraoptions);
				activateDefaultEvents(section, container, data, pantalles, screenId);
		}
		
		function deleteScreen(container, temp){
			$(container).find("#" + temp).remove();
		}
	
		function injectScreenToDOM(container, result, position, anchorRef, screenId,extraoptions){
			if (extraoptions == undefined){
				extraoptions = new Object();
			}
				if (extraoptions['bgiframe'] == undefined)
					extraoptions['bgiframe']= true;
				if (extraoptions['modal'] == undefined)
					extraoptions['modal']= true;
				if (extraoptions['position'] == undefined)
					extraoptions['position']= 'top';
				if (extraoptions['close'] == undefined)
					extraoptions['close']= function(event, ui){
				                $(this).remove();
				   }; 
			
			var htmlresult = $(result);
			
			if (extraoptions['show']!=undefined){
					htmlresult.hide();
			}

			if(anchorRef == "undefined" || typeof anchorRef == "undefined")
				position = "defaultCase";
	
			switch (position) {
			
				case "dialog":
					$(container).append(result);
					$('#' + screenId).dialog(extraoptions);
					break;
				case "before":
					anchorRef.before(htmlresult);
					break;
				case "after":
					anchorRef.after(htmlresult);
					break;
				case "prepend":
					anchorRef.prepend(htmlresult);
					break;
				case "append":
					anchorRef.append(htmlresult);
					break;
				default:
					$(container).append(htmlresult);
					break;
			}

			if (extraoptions['show']!=undefined){
					htmlresult.show(extraoptions['show']);
			}
			
		}
	
		function createjQueryObject(actionParam){
	
			if (actionParam.substr(0, 1) == "$")
				return eval(actionParam);
			else
				return $(actionParam);
		}