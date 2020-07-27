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
* Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat), Xavier Noguero <xnoguero@asic.udl.cat> and usuaris-cvirtual@llistes.udl.cat
* Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
*
**/ 
	function initializeValidator(){

			$.validator.addMethod("mystringvalue", function(value, element) {
					var num = $(element).val().match(/\mystringvalue/g);
					if(num && num.length >=3)
						return this.optional(element) || false;
					else
						return this.optional(element) || true;
			}, "Ouput message for this validator!");
				jQuery.validator.addMethod(
					"dateESP",
					function(value, element) {
						var check = false;
						var re = /^\d{1,2}\/\d{1,2}\/\d{4}$/
						if( re.test(value)){
							var adata = value.split('/');
							var gg = parseInt(adata[0],10);
							var mm = parseInt(adata[1],10);
							var aaaa = parseInt(adata[2],10);
							var xdata = new Date(aaaa,mm-1,gg);
							if ( ( xdata.getFullYear() == aaaa ) && ( xdata.getMonth () == mm - 1 ) && ( xdata.getDate() == gg ) )
								check = true;
							else
								check = false;
						} else
							check = false;
						return this.optional(element) || check;
					}, 
					"Si us plau, escriviu una data v\u00e0lida."
			);
	}

	function isFormValid(){
			return $("form").valid();
	}
