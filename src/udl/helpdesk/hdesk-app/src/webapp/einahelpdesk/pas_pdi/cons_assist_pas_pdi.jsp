<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs"/>

<f:view>
	
	<sakai:view_container title="#{msgs.sample_title2}" rendered="#{HDesk.perfil=='pas_pdi'}">
	<h:form>
		  <jsp:include page="toolbar.jsp" />

		  <br>
		  		  
		<table width="95%" align="center" cellpadding=0 cellspacing=0 border=0>
                <tr><td align="center">
			<h3>
			<f:attribute name="res" value="#{HDesk_pas_pdi.isHistoric}" />
			<h:outputText value="#{msgs.text_assist_pas_pdi1}" rendered="#{HDesk_pas_pdi.isHistoric==0}"/>
			<h:outputText value="#{msgs.text_assist_pas_pdi3}" rendered="#{HDesk_pas_pdi.isHistoric==2}"/>
			</h3>
		</td></tr>
		
                <tr><td>
								
				<table width="100%" bgcolor="#f0f0f0" border=0>                
                <tr><td valign="bottom">
                	<h:outputText value="#{msgs.usuari}" /> 
                	<h:outputText value="#{HDesk_pas_pdi.nom}" style="font-weight: bold; font-size: 15;"/><br><br>  
                	<h:outputText value="#{msgs.cerca}" />     
                	<h:selectOneMenu value="#{HDesk_pas_pdi.columna_sel}" required="false" >
			 	<f:selectItems value="#{HDesk_pas_pdi.columna_cerca}"/>
			</h:selectOneMenu>
                	<h:outputText value="#{msgs.clau}" />
                	<h:inputText value="#{HDesk_pas_pdi.paraula_clau}" size="15" /></td><td valign="bottom">
                	<sakai:button_bar>
				<sakai:button_bar_item value="#{msgs.boto_cercar}" action="#{HDesk_pas_pdi.processActionCercar}"/>
				<sakai:button_bar_item value="#{msgs.boto_netejar}" action="#{HDesk_pas_pdi.processActionNeteja}"/>
			</sakai:button_bar>
                </td></tr><tr>
                <td valign="bottom">
               	<sakai:button_bar>
					<sakai:button_bar_item value="|<" action="#{HDesk_pas_pdi.processActionPrimeraPag}" disabled="#{HDesk_pas_pdi.noTePrevia}"/>
					<sakai:button_bar_item value="<" action="#{HDesk_pas_pdi.processActionPagAnt}" disabled="#{HDesk_pas_pdi.noTePrevia}"/>
	           	<h:selectOneMenu valueChangeListener="#{HDesk_pas_pdi.setReg_per_pag}"  value="#{HDesk_pas_pdi.reg_per_pag}" onchange="submit()"  immediate="true" style="background:#f8ffbe">
							<f:selectItem itemLabel="mostrar 5 per pagina" itemValue="5"/>
							<f:selectItem itemLabel="mostrar 10 per pagina" itemValue="10"/>
							<f:selectItem itemLabel="mostrar 20 ..." itemValue="20"/>
							<f:selectItem itemLabel="mostrar 50 ..." itemValue="50"/>
					</h:selectOneMenu>
                     		<sakai:button_bar_item value=">" action="#{HDesk_pas_pdi.processActionPagSeg}" disabled="#{HDesk_pas_pdi.noTeSeg}"/>
						<sakai:button_bar_item value=">|" action="#{HDesk_pas_pdi.processActionUltimaPag}" disabled="#{HDesk_pas_pdi.noTeSeg}"/>
					</sakai:button_bar>
                </td><td align="right">
                <h:outputText value="#{HDesk_pas_pdi.pagina} de #{HDesk_pas_pdi.totalPag}"/>
                </td></tr></table></td></tr></table>
                
		  <table width="95%" align="center" cellpadding=0 cellspacing=-1 border=0>
                <tr><td><p align="justify">
                
                <table width="100%" >
                                
                <tr><td>
                
                  <%--<sakai:flat_list value="#{HDesk_pas_pdi.assistencies}" var="registre">--%>
				  <h:dataTable styleClass="table table-striped" value="#{HDesk_pas_pdi.assistencies}" bgcolor="#ffffff" border="0" cellpadding="4" cellspacing="1" var="registre" headerClass="presenceList" rowClasses="borderGrayBackground,borderWhite" >
         			<h:column>
		  				<f:facet name="header">
						<h:commandLink action="#{HDesk_pas_pdi.mouSentit1}">
		  					<h:outputText value="#{msgs.text_cons_assist1}" style="font-weight:bold;font-size:8pt"/>                                       
		  					<h:graphicImage value="/image/#{HDesk_pas_pdi.amuntavallres1}" />
			  			</h:commandLink> 
					
 						</f:facet>
			  		
			  		<h:commandLink action="#{HDesk_pas_pdi.ConsultaPasPdi}" actionListener="#{HDesk_pas_pdi.processActionViewTicket}" value="#{registre.ticket}" immediate="true" >
							<f:param name="ticket" value="#{registre.ticket}" />
     				</h:commandLink>
		  			
		  			
		  			
		  			</h:column>

          			<h:column>
		  				<f:facet name="header">
						<h:commandLink action="#{HDesk_pas_pdi.mouSentit2}"> 
		  					<h:outputText value="#{msgs.text_cons_assist2}" style="font-weight:bold;font-size:8pt"/>                                       
		  					<h:graphicImage value="/image/#{HDesk_pas_pdi.amuntavallres2}" />
			  			</h:commandLink> 
		
		  				</f:facet>
				 <h:outputText value="#{registre.strData_inici}"  style="color:black" rendered="#{registre.estat==0 || registre.estat==2}"/>
  				 <h:outputText value="#{registre.strData_inici}"  style="font-weight:bold; color:green" rendered="#{registre.estat==1}"/>
                     </h:column>
		  	       	        	
         			<h:column>
		  				<f:facet name="header">
							<h:commandLink action="#{HDesk_pas_pdi.mouSentit4}"> 
		  					<h:outputText value="#{msgs.text_cons_assist4}" style="font-weight:bold;font-size:8pt"/>
		  					<h:graphicImage value="/image/#{HDesk_pas_pdi.amuntavallres4}" />
			  			</h:commandLink> 
		
		  				</f:facet>
				 <h:outputText value="#{registre.nom_campus}"  style="color:black" rendered="#{registre.estat==0 || registre.estat==2}"/>
                                 <h:outputText value="#{registre.nom_campus}"  style="font-weight:bold; color:green" rendered="#{registre.estat==1}"/>
		  			</h:column>
		  			
	  				<h:column>
		  				<f:facet name="header">
							<h:commandLink action="#{HDesk_pas_pdi.mouSentit5}"> 
		  					<h:outputText value="#{msgs.text_cons_assist5}" style="font-weight:bold;font-size:8pt"/>
		  					<h:graphicImage value="/image/#{HDesk_pas_pdi.amuntavallres5}" />
			  			</h:commandLink> 
		
		  				</f:facet>
				 <h:outputText value="#{registre.nom_edifici}"  style="color:black" rendered="#{registre.estat==0 || registre.estat==2}"/>
                                 <h:outputText value="#{registre.nom_edifici}"  style="font-weight:bold; color:green" rendered="#{registre.estat==1}"/>
		  			</h:column>

		  			<h:column>
		  				<f:facet name="header">
							<h:commandLink action="#{HDesk_pas_pdi.mouSentit6}"> 
		  					<h:outputText value="#{msgs.text_cons_assist6}" style="font-weight:bold;font-size:8pt"/>                                       
		  					<h:graphicImage value="/image/#{HDesk_pas_pdi.amuntavallres6}" />
			  			</h:commandLink> 
		
		  				</f:facet>
				 <h:outputText value="#{registre.nom_tecnic}"  style="color:black" rendered="#{registre.estat==0 || registre.estat==2}"/>
                                 <h:outputText value="#{registre.nom_tecnic}"  style="font-weight:bold; color:green" rendered="#{registre.estat==1}"/>
		  			</h:column>
		  	
		  			<h:column>
		  				<f:facet name="header">
        					<h:commandLink action="#{HDesk_pas_pdi.mouSentit7}"> 
          					<h:outputText value="#{msgs.text_cons_assist7}" style="font-weight:bold;font-size:8pt"/>
		  					<h:graphicImage value="/image/#{HDesk_pas_pdi.amuntavallres7}" />
			  			</h:commandLink> 
		
		  				</f:facet>
				 <h:outputText value="#{registre.nom_categoria}"  style="color:black" rendered="#{registre.estat==0 || registre.estat==2}"/>
                                 <h:outputText value="#{registre.nom_categoria}"  style="font-weight:bold; color:green" rendered="#{registre.estat==1}"/>
		   			</h:column>
		  	
		  			<h:column>
		  				<f:facet name="header">
        					<h:commandLink action="#{HDesk_pas_pdi.mouSentit8}"> 
           					<h:outputText value="#{msgs.text_cons_assist8}" style="font-weight:bold;font-size:8pt"/>
		  					<h:graphicImage value="/image/#{HDesk_pas_pdi.amuntavallres8}" />
			  			</h:commandLink> 
		
				  		</f:facet>
				 <h:outputText value="#{registre.nom_prioritat}"  style="color:black" rendered="#{registre.estat==0 || registre.estat==2}"/>
                                 <h:outputText value="#{registre.nom_prioritat}"  style="font-weight:bold; color:green" rendered="#{registre.estat==1}"/>
				  	</h:column>

					<h:column>
		  				<f:facet name="header">
        					<h:commandLink action="#{HDesk_pas_pdi.mouSentit9}"> 
           					<h:outputText value="#{msgs.text_cons_assist9}" style="font-weight:bold;font-size:8pt"/>
		  					<h:graphicImage value="/image/#{HDesk_pas_pdi.amuntavallres9}" />
			  			</h:commandLink> 
		
				  		</f:facet>
				 <h:outputText value="#{registre.nom_estat}"  style="color:black" rendered="#{registre.estat==0 || registre.estat==2}"/>
                                 <h:outputText value="#{registre.nom_estat}"  style="font-weight:bold; color:green" rendered="#{registre.estat==1}"/>
				  	</h:column>
		  		
 		  		 <%--</sakai:flat_list>--%>
                 </h:dataTable>    
                     </td></tr>
		        		       		        
		        </table>
         		
         		</p></td></tr>
         		</table>
             </td></tr>
          </table>
		  	 
		
		 		
		  	  
	</h:form>	
	
	</sakai:view_container>
	<sakai:view_container rendered="#{HDesk.perfil!='pas_pdi'}">
		<center><br><br>
				  <h:outputText style="font-weight:bold;color:red;" value="Error:"/> <h:outputText value="#{msgs.error}" /><br>
		</center> 
	</sakai:view_container>

</f:view>
