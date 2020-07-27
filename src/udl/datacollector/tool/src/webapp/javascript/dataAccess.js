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

function getBlocks(type, siteId,callbackFunction){
	var ftype = "";
	var finalSite = "";

	if (siteId!=undefined){
		finalSite = "/site/" +siteId;
	}
	
	if(type == "Edit")
		ftype = "?updateOnly=true&sid=" + Math.random();
	else
		ftype = "?sid=" + Math.random(); 
    $.getJSON("/direct/blockdata" + finalSite +".json" + ftype , function (data){
    		var dataLocal = data;
			$(callbackFunction(dataLocal.blockdata_collection));
	});
}

function getSectionDefinition (cblock,cutype, sectionId, callbackFunction){
	 
	$.getJSON("/direct/section/definition:"+sectionId+".json?sid=" + Math.random(), function (data){
    		var dataLocal = data;
			$(callbackFunction(cblock,cutype,dataLocal));
	});
}

function getSection (cblock,cutype, sectionId, callbackFunction){
	 $.getJSON("/direct/section/"+sectionId+".json?sid=" + Math.random(), function (data){
   			var dataLocal = data;
			$(callbackFunction(cblock,cutype,dataLocal));
	});
}

function getTemplate(template, messagesUrl, container, callbackFunction){
	
	$.get(template,{},function(dataG){
		if (container!=undefined){
			$(container).html(dataG);
		}
		var messages = null;
		if (messagesUrl != null){
		
			$.ajax({
				type: "GET",
				url: messagesUrl + ".json",	
				dataType: "json",
				cache: false,
				async: false,
				success: function (dataM){
					messages = dataM;
				}
			}); 
		}
		$(callbackFunction(dataG,messages));
	});
}

//not used on datacollector versions > 0.1
function getRequiredData(section, paramJson, callbackFunction){
	
	var url = "/direct/section/" +  section + "/datasource/" + paramJson + ".json?sid="+ Math.random();
	$.ajax({
		type: "GET",
		url: url,
		dataType: "json",
		cache: false,
		async: false,
		success: function (data){

			$(callbackFunction(data));
		}
	});
}

function getMessages (url,locale,callbackFunction){
	$.get(url+"_"+locale+".json" ,{},function(dataG){
		$(callbackFunction(dataG));
	});
}

function getRequiredDynamicData(section, paramJson, dynamicOptions, callbackFunction, manageState, async){
	
	var url = "/direct/section/" +  section + "/datasource/" + paramJson + ".json?sid="+ Math.random();
	if (dynamicOptions.length > 0){
		url = "/direct/section/" +  section + "/datasource/" + paramJson + ".json?sid="+ Math.random();

		for (var index = 0; index < dynamicOptions.length;index++){
			url = url + "&" + dynamicOptions[index].name+ "=" + dynamicOptions[index].value; 
		}
	}
	
	$.get_(url, null, callbackFunction, "json", manageState, async);
}

//method callable from actions save and delete
function setData(section, data, objectToPut, funcio, reqoptions, manageState, async){
	
	var url = "/direct/section/" +  section + "/datasource/" + objectToPut + ".json?sid="+ Math.random(); 
	
	if (reqoptions != undefined){
		var index = 1;
		$.each( reqoptions, function(k, v){
			url=url+ "&" + k + "=" + v;
		});
	}
	
	$.put(url, $.toJSON(data), funcio, "json", manageState, async);
}

function deleteData(section, id, objectToPut, funcio, manageState, async){
	var url = "";
	if (id!=undefined){
		 url = "/direct/section/" +  section + "/datasource/" + objectToPut + "/" + id + ".json";
	}else{
		 url = "/direct/section/" +  section + "/datasource/" + objectToPut +  ".json";
	}
	
	$.delete_(url, {}, funcio,"json", manageState, async);
}

function getUrl(section,objectToPut,reqoptions){
	var url = "/direct/section/" +  section + "/datasource/" + objectToPut + ".json"; 
	if (reqoptions != undefined){
		var index = 1;
		$.each( reqoptions, function(k, v){
			if (i==1){
				url=url+ "?" + k + "=" + v;
				index++;
			}else{
				url=url+ "&" + k + "=" + v;
			}
		});
	}
	return url;
}

/* Extend jQuery with functions for PUT and DELETE requests. */
function _ajax_request(url, data, callback, type, method, manageState, async) {	

	 $.ajaxSetup ({  
         cache: false
     });

    if (jQuery.isFunction(data)) {
        callback = data;
        data = {};
    }

    return jQuery.ajax({
        type: method,
        url: url,
        async: async,
        data: data,
        success: function (data, textStatus, jqXHR){  
        	
        	$(callback(data));
			if(manageState != undefined && manageState.idAction != undefined){
          		$(manageState.src).trigger("onSuccess" + manageState.idAction);
          	}
        }, 
		error:function (xhr, ajaxOptions, thrownError){

			if(manageState != undefined && manageState.idAction != undefined){
          		$(manageState.src).trigger("onFail" + manageState.idAction);
          	}
        },
        dataType: type
        });
}

jQuery.extend({

	put: function(url, data, callback, type, manageState, async) {
        return _ajax_request(url, data, callback, type, 'PUT', manageState, async);
    },

    delete_: function(url, data, callback, type, manageState, async) {
        return _ajax_request(url, data, callback, type, 'DELETE', manageState, async);
    },

    get_: function(url, data, callback, type, manageState, async) {
        return _ajax_request(url, data, callback, type, 'GET', manageState, async);
    }
});