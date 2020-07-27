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
* Authors: Xavier Noguero
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballest� (alex@asic.udl.cat), Xavier Noguero <xnoguero@asic.udl.cat> and usuaris-cvirtual@llistes.udl.cat
* Universitat de Lleida  Pla�a V�ctor Siurana, 1  25005 LLEIDA SPAIN
*
**/ 
function runTest(){
	
	//Test 1 - Llista de block-section lectura
	getBlocks('Read', function(){
		$('#test1').text('OK');
	}, function(){
		$('#test1').text('FAILED');
	});
	
	//Test 2 - Llista de block-section escriptura
	getBlocks('Edit', function(){
		$('#test2').text('OK');
	}, function(){
		$('#test2').text('FAILED');
	});
	
	//Test 3 - Definició d'una section anomenada "sec-test"
	getSectionDefinition ('sec-test', function(){
		$('#test3').text('OK');
	}, function(){
		$('#test3').text('FAILED');
	});
	
	//Test 4 - Definició d'una section què no tenim permís pero que existeix (sec-forbidden)
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/definition:sec-forbidden.json?sid=" + Math.random(),
        dataType: "json",
        success: function(data){
			$('#test4').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test4ErrorCode').text(xhr.status);
          	if(xhr.status != '403'){ //error
            	$('#test4').text('FAILED');
          	}else{ //success - no content
            	$('#test4').text('OK');
          	}
        }
	});
	
	//Test 5 - GET section info de la section "sec-test"
	getSectionDefinition ('sec-test', function(){
		$('#test5').text('OK');
	}, function(){
		$('#test5').text('FAILED');
	});
	
	//Test 6 - GET section info de la section "sec-forbidden" què no tenim permís
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-forbidden.json?sid=" + Math.random(),
        dataType: "json",
        success: function(data){
			$('#test6').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test6ErrorCode').text(xhr.status);
          	if(xhr.status != '403'){ //error
            	$('#test6').text('FAILED');
          	}else{ //success - no content
            	$('#test6').text('OK');
          	}
        }
	});
	
	//Test 7 - GET DataSource? simple.value existent (hores.json)
	$.getJSON_("/direct/section/sec-test/datasource/hores.json", function(){
		$('#test7').text('OK');
	}, function(){
		$('#test7').text('FAILED');
	});
	
	//Test 8 - GET DataSource? simple.value inexistent (hores-de-po-zi-po-zi.json)
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/hores-de-po-zi-po-zi.json",
        dataType: "json",
        success: function(data){
			$('#test8').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test8ErrorCode').text(xhr.status);
          	if(xhr.status != '500'){ //error
            	$('#test8').text('FAILED');
          	}else{ //success - no content
            	$('#test8').text('OK');
            	
          	}
        }
	});
	
	//Test 9 - GET DataSource? simple.value que no tenim permís a la section corresponent
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-forbidden/datasource/hores.json",
        dataType: "json",
        success: function(data){
			$('#test9').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test9ErrorCode').text(xhr.status);
          	if(xhr.status != '403'){ //error
            	$('#test9').text('FAILED');
          	}else{ //success - no content
            	$('#test9').text('OK');
            	
          	}
        }
	});
	
	//Test 10 - PUT per insertar nou valor simple.value
	var objectToPut = new Object();
	objectToPut.id = "hores";
	objectToPut.value = new Object();
	objectToPut.value['simple.value'] = 25;
	
	$('#test10ObjPosat').text($.toJSON(objectToPut));
	$.put("/direct/section/sec-test/datasource/hores.json", $.toJSON(objectToPut), function(){
		$('#test10').text('OK');
	}, function(){
		$('#test10').text('FAILED');
	}, "json");
	
	//Test 11 - GET per veure el valor inserit
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/hores.json",
        dataType: "json",
        success: function(data){
			if(objectToPut.value['simple.value'] == data.value["simple.value"])
				$('#test11').text('OK');
			else
				$('#test11').text('FAILED');
			$('#test11ObjPosat').text(data.value["simple.value"]);
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
          	if(xhr.status != '204'){ //error
            	$('#test11').text('FAILED');
          	}else{ //success - no content
            	$('#test11').text('OK - no content');
          	}
        }
	});

	//Test 12 - PUT per inserir un valor a un datasource d'una section que no tenim permís
	var objectToPut = new Object();
	objectToPut.id = "hores";
	objectToPut.value = new Object();
	objectToPut.value['simple.value'] = 25;
	$('#test12ObjPosat').text($.toJSON(objectToPut));
	var errorCode = "";
	$.ajax({
        type: "PUT",
        cache: false,
        url: "/direct/section/sec-forbidden/datasource/hores.json",
        data: $.toJSON(objectToPut),
        dataType: "json",
        success: function(){
			$('#test12').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test12ErrorCode').text(xhr.status);
	  		if(xhr.status != '403'){ //error
            	$('#test12').text('FAILED');
          	}else{ //success - no content
            	$('#test12').text('OK');
          	}
        }
	});
	
	
	//Test 13 - PUT per inserir un valor a un datasource que té el camp updatable a false
	
	$('#test13ObjPosat').text($.toJSON(objectToPut));
	var errorCode = "";
	$.ajax({
        type: "PUT",
        cache: false,
        url: "/direct/section/sec-test/datasource/horesNoUpdatable.json",
        data: $.toJSON(objectToPut),
        dataType: "json",
        success: function(){
			$('#test13').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test13ErrorCode').text(xhr.status);
	  		if(xhr.status != '403'){ //error
            	$('#test13').text('FAILED');
          	}else{ //success - no content
            	$('#test13').text('OK');
          	}
        }
	});
	
	//Test 14 - PUT per modificar el valor anterior
	var objectToPut = new Object();
	objectToPut.id = "hores";
	objectToPut.value = new Object();
	objectToPut.value['simple.value'] = 26;

	$('#test14ObjPosat').text($.toJSON(objectToPut));
	$.put("/direct/section/sec-test/datasource/hores.json", $.toJSON(objectToPut), function(){
		$('#test14').text('OK');
	}, function(){
		$('#test14').text('FAILED');
	});
	
	
	//Test 15 - GET per comprovar els canvis
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/horesNoUpdatable.json",
        dataType: "json",
        success: function(data){
			if(objectToPut.value['simple.value'] == data.value["simple.value"])
				$('#test15').text('OK');
			else
				$('#test15').text('FAILED');
			$('#test15ObjPosat').text(data.value["simple.value"]);
  		},
  		async: false,
  		error:function (xhr, ajaxOptions, thrownError){
  			if(xhr.status != '204'){ //error
  				$('#test15').text('FAILED');
  			}else{ //success - no content
  				$('#test15').text('OK - no content');
  			}
  		}
	});


	//Test 16 - DELETE simple.value del valor inserit
	$.delete_("/direct/section/sec-test/datasource/hores.json", {},function(){
		$('#test16').text('OK');
	}, function(){
		$('#test16').text('FAILED');
	},"json");
	
	//Test 17 - GET comprovar que no existeix
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/hores.json",
        dataType: "json",
        success: function(data){
			if('value not found' == data.value["simple.value"])
				$('#test17').text('OK');
			else
				$('#test17').text('FAILED');
			$('#test17ObjPosat').text($.toJSON(data));
  		},
  		async: false,
  		error:function (xhr, ajaxOptions, thrownError){
  			if(xhr.status != '204'){ //error
  				$('#test17').text('FAILED');
  			}else{ //success - no content
  				$('#test17').text('OK - no content');
  			}
  		}
	});
	
	//Test 18 - DELETE simple.value a un datasource que té el camp updatable a false
	$.ajax({
        type: "DELETE",
        cache: false,
        url: "/direct/section/sec-test/datasource/horesNoUpdatable.json",
        dataType: "json",
        success: function(data){
			$('#test18').text('FAILED');
  		},
  		async: false,
  		error:function (xhr, ajaxOptions, thrownError){
  			$('#test18ErrorCode').text(xhr.status);
  			if(xhr.status != '403'){ //error
  				$('#test18').text('FAILED');
  			}else{ //success - no content
  				$('#test18').text('OK');
  			}
  		}
	});
		
	//Test 19 - GET DataSource? collection.value existent
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes.json",
        success: function(data){
			$('#test19').text('OK');
			$('#test19ObjPosat').text($.toJSON(data));
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
          	if(xhr.status != '204'){ //error
            	$('#test19').text('FAILED');
          	}else{ //success - no content
            	$('#test19').text('OK');
          	}
        },
        dataType: 'json'
     });
	
	
	//Test 20 - GET DataSource? collection.value inexistent
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes-po-zi-pos-no-se-yo-si.json",
        dataType: "json",
        success: function(data){
			$('#test20').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test20ErrorCode').text(xhr.status);
          	if(xhr.status != '500'){ //error
            	$('#test20').text('FAILED');
          	}else{ //success - no content
            	$('#test20').text('OK');
            	
          	}
        }
	});
	
	
	//Test 21 - GET DataSource? collection.value que no tenim permís a la section corresponent
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-forbidden/datasource/revistes.json",
        dataType: "json",
        success: function(data){
			$('#test21').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test21ErrorCode').text(xhr.status);
          	if(xhr.status != '403'){ //error
            	$('#test21').text('FAILED');
          	}else{ //success - no content
            	$('#test21').text('OK');
            	
          	}
        }
	});
	
	//Test 22 - PUT per insertar nova fila a collection.value
	var objectToPut = new Object();
	objectToPut.value = new Object();
	objectToPut.value.dsc ="Nova revista";
	objectToPut.value.human ="C";
	objectToPut.value.codi_human ="000765";
	objectToPut.value.id = 23;
	
	var errorCode = "";
	$.ajax({
        type: "PUT",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes-upd.json",
        data: $.toJSON(objectToPut),
        dataType: "json",
        success: function(data){
		alert(data);
			$('#test22').text('OK');
			$('#test22ObjPosat').text($.toJSON(objectToPut));
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test22ErrorCode').text(xhr.status);
	  		if(xhr.status != '204'){ //error
            	$('#test22').text('FAILED');
          	}else{ //success - no content
            	$('#test22').text('OK - no content');
          	}
        }
	});
	
	//Test 22-1 - PUT per insertar nova fila a collection.value amb id concret: 354
	var objectToPut = new Object();
	objectToPut.id = 354;
	objectToPut.value = new Object();
	objectToPut.value.id = 354;
	objectToPut.value.dsc ="Nova revista";
	objectToPut.value.human ="C";
	objectToPut.value.codi_human ="000765";
	objectToPut.value.id =354;
	
	$.ajax({
        type: "PUT",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes-upd.json",
        data: $.toJSON(objectToPut),
        dataType: "json",
        success: function(data){
		alert(data);
			$('#test22-1').text('OK');
			$('#test22-1ObjPosat').text($.toJSON(objectToPut));
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test22-1ErrorCode').text(xhr.status);
	  		if(xhr.status != '204'){ //error
            	$('#test22-1').text('FAILED');
          	}else{ //success - no content
            	$('#test22-1').text('OK - no content');
          	}
        }
	});
	
	//Test 23 - GET per veure el valor insertat
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes-upd/354.json",
        success: function(data){
			$('#test23').text('OK');
			$('#test23ObjPosat').text($.toJSON(data));
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
          	if(xhr.status != '204'){ //error
            	$('#test23').text('FAILED');
          	}else{ //success - no content
            	$('#test23').text('OK');
          	}
        },
        dataType: 'json'
     });
	
	//Test 24 - PUT per inserir una nova fila a un datasource d'una section que no tenim permís
	var objectToPut = new Object();
	objectToPut.value = new Object();
	objectToPut.value.dsc = "Nova revista";
	objectToPut.value.human = "C";
	objectToPut.value.codi_human = "000765";
	objectToPut.value.id = 365;
		
	$('#test24ObjPosat').text($.toJSON(objectToPut));
	$.ajax({
        type: "PUT",
        cache: false,
        url: "/direct/section/sec-forbidden/datasource/revistes-upd.json",
        data: $.toJSON(objectToPut),
        dataType: "json",
        success: function(data){
		alert(data);
			$('#test24').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test24ErrorCode').text(xhr.status);
	  		if(xhr.status != '403'){ //error
            	$('#test24').text('FAILED');
          	}else{ //success - no content
            	$('#test24').text('OK');
          	}
        }
	});
	
	//Test 25 - PUT per inserir una nova fila a un datasource que té el camp updatable a false
	var objectToPut = new Object();
	objectToPut.value = new Object();
	objectToPut.value.dsc ="Nova revista";
	objectToPut.value.human ="C";
	objectToPut.value.codi_human ="000765";
	objectToPut.value.id =365;

	$('#test25ObjPosat').text($.toJSON(objectToPut));
	$.ajax({
        type: "PUT",
        cache: false,
        url: "/direct/section/sec-forbidden/datasource/revistes-upd.json",
        data: $.toJSON(objectToPut),
        dataType: "json",
        success: function(){
			$('#test25').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test25ErrorCode').text(xhr.status);
	  		if(xhr.status != '403'){ //error
            	$('#test25').text('FAILED');
          	}else{ //success - no content
            	$('#test25').text('OK');
          	}
        }
	});
	
	//Test 26 - PUT per modificar la fila anterior
	var objectToPut = new Object();
	objectToPut.id = 354;
	objectToPut.value = new Object();
	objectToPut.value.id = 354;
	objectToPut.value.dsc ="Nova revista";
	objectToPut.value.human ="D";
	objectToPut.value.codi_human ="000765";

	$('#test26ObjPosat').text($.toJSON(objectToPut));
	$.ajax({
        type: "PUT",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes-upd.json",
        data: $.toJSON(objectToPut),
        dataType: "json",
        success: function(data){
			$('#test26').text('OK');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test26ErrorCode').text(xhr.status);
          	if(xhr.status != '204'){ //error
            	$('#test26').text('FAILED');
          	}else{ //success - no content
            	$('#test26').text('OK');
          	}
        },
        dataType: 'json'
     });
	
	//Test 27 - GET per comprovar els canvis
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes-upd/354.json",
        success: function(data){
			$('#test27').text('OK');
			$('#test27ObjPosat').text($.toJSON(data));
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
          	if(xhr.status != '204'){ //error
            	$('#test27').text('FAILED');
          	}else{ //success - no content
            	$('#test27').text('OK');
          	}
        },
        dataType: 'json'
     });
	
	
	//Test 28 - DELETE de la fila del valor inserit
	$.ajax({
        type: "DELETE",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes-upd/354.json",
        dataType: "json",
        success: function(){
			$('#test28').text('OK');
  		},
  		async: false,
  		error:function (xhr, ajaxOptions, thrownError){
  			$('#test28ErrorCode').text(xhr.status);
  			if(xhr.status != '204'){ //error
  				$('#test28').text('FAILED');
  			}else{ //success - no content
  				$('#test28').text('OK - No content');
  			}
  		}
	});
	
	
	//Test 29 - GET d'una fila que no existeix
	$.ajax({
        type: "GET",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes/3asd2423.json",
        success: function(){
			$('#test29').text('FAILED');
	  	},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
	  		$('#test29ErrorCode').text(xhr.status);
	  		if(xhr.status != '204'){ //error
            	$('#test29').text('OK');
          	}else{ //success - no content
            	$('#test29').text('FAILED');
          	}
        },
        dataType: 'json'
     });
	
	//Test 30 - DELETE d'una fila a un datasource que té el camp updatable a false
	$.ajax({
        type: "DELETE",
        cache: false,
        url: "/direct/section/sec-test/datasource/revistes/3.json",
        dataType: "json",
        success: function(data){
			$('#test30').text('FAILED');
  		},
  		async: false,
  		error:function (xhr, ajaxOptions, thrownError){
  			$('#test30ErrorCode').text(xhr.status);
  			if(xhr.status != '403'){ //error
  				$('#test30').text('FAILED');
  			}else{ //success - no content
  				$('#test30').text('OK');
  			}
  		}
	});
}


function getBlocks(type, funcio, funcioError){
	var ftype = "";
	if(type == "Edit")
		ftype = "?updateOnly=true&sid=" + Math.random();
	else
		ftype = "?sid=" + Math.random(); 
    $.getJSON_("/direct/blockdata.json" + ftype , funcio, funcioError);
}

function getSectionDefinition (sectionId, funcio, funcioError){
	 $.getJSON_("/direct/section/definition:"+sectionId+".json?sid=" + Math.random(), funcio, funcioError);
}

function getSectionInfo(sectionId, funcio, funcioError){
	 $.getJSON_("/direct/section/"+sectionId+".json?sid=" + Math.random(), funcio, funcioError);
}
/*
function getHours(id,callbackfunction){
    $.getJSON("/direct/section/section-hours/datasource/hours-" + id + ".json?sid=" + Math.random(), function (data){		
	    	  var dataStructure = new Object ();
    		  dataStructure.id = id;
    		  dataStructure.value= data.value['simple.value'];
		$(callbackfunction(dataStructure));
    });	
}


function getTemplate(template, container, funcio){

	$(container).load(template,{},function (){
		$(funcio());
	});
}


function getRequiredData(section, paramJson, funcio){
	
	var url = "/direct/section/" +  section + "/datasource/" + paramJson + ".json?sid="+ Math.random();
	$.ajax({
		type: "GET",
		url: url,
		dataType: "json",
		cache: false,
		async: false,
		success: function (dades){

			$(funcio(dades));
		}
	});
}

function setData(section, data, objectToPut, funcio, funcioError){
	
	var url = "/direct/section/" +  section + "/datasource/" + objectToPut + ".json";

//	log("Inside setData: " + window.dataTemplateLoader[objectToPut].value.id_activitat + ' ' + window.dataTemplateLoader[objectToPut].value.id_revista);
//	log("Serialitzat: " + $.toJSON(window.dataTemplateLoader[objectToPut]));
	
	$.put(url, $.toJSON(window.dataTemplateLoader[objectToPut]), funcio, funcioError, "json");
}

function deleteData(section, data, objectToPut, funcio, funcioError){
	
	var url = "/direct/section/" +  section + "/datasource/" + objectToPut + "/" + window.dataTemplateLoader[objectToPut].id + ".json";

	log('[deleteData]' + url);
	
	$.delete_(url, {}, funcio,"json");
}
*/
/* Extend jQuery with functions for PUT and DELETE requests. */
function _ajax_request(url, data, callback, callbackError, type, method) {

	 $.ajaxSetup ({  
         cache: false,
         timeout: 4000,
     });  

    return jQuery.ajax({
        type: method,
        url: url,
        data: data,
        success: function(){callback();},
        async: false,
		error:function (xhr, ajaxOptions, thrownError){
          	if(xhr.status != '204'){ //error
          		callbackError();
          	}else{ //success - no content
          		callback();
          	}
        },
        dataType: type
        });
}

jQuery.extend({

	put: function(url, data, callback, callbackError, type) {
        return _ajax_request(url, data, callback, callbackError, type, 'PUT');
    },

    delete_: function(url, data, callback, callbackError, type) {
        return _ajax_request(url, data, callback, callbackError, type, 'DELETE');
    },
    
    getJSON_: function(url, callback, callbackError){
        return _ajax_request(url, {}, callback, callbackError, 'json', 'GET' );    	
    }
 
});
