<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<link REL="stylesheet" HREF="stil/stil.css" TYPE="text/css">
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs"/>

<f:view>
	
	<sakai:view_container title="#{msgs.sample_title2}" rendered="#{HDesk.perfil=='support'}">
		
	<h:form>
		  <jsp:include page="toolbar.jsp" />
		
		  <br>
		  		  
		  <table width="80%" align="center" cellpadding=0 cellspacing=0 border=0>
                <tr><td align="center"><h3><h:outputText value="#{msgs.text_cons_historic}"/></h3></td></tr>

                <tr><td>
				<p valign="middle" align="center">
					<h:outputText value="#{msgs.text_cons_historic1}" style="font-weight: bold;"/>
				</p>
								
				<table width="100% "bgcolor="#f0f0f0" border=0>                
                <tr><td>
                	<h:outputText value="#{msgs.cerca}" />     
                	<h:selectOneMenu id="menuTipus" value="empty" required="false" >
							<f:selectItem itemLabel="Data" itemValue="Q/D"/>
							<f:selectItem itemLabel="Introduida per" itemValue="V/I"/>
							<f:selectItem itemLabel="Campus" itemValue="V/I"/>
							<f:selectItem itemLabel="Edifici" itemValue="V/I"/>
							<f:selectItem itemLabel="..." itemValue="V/I"/>
					</h:selectOneMenu>
                	<h:outputText value="#{msgs.clau}" />
                	<h:inputText value="" required="false" size="15" />
                	<h:commandButton action="empty" value="#{msgs.boto_cercar}"/><br><br><br>
                </td></tr>
                
                <tr><td><p align="justify">
                
                <table width="100%" border=2>
                                
                <tr><td>
                
                <sakai:flat_list value="empty" var="prova">
         			<h:column>
		  				<f:facet name="header">
		  					<h:outputText value="#{msgs.text_cons_historic1}" />                                       
		  				</f:facet>
		  			</h:column>
		  	
		  			<h:column>
		  				<f:facet name="header">
		  					<h:outputText value="#{msgs.text_cons_assist2}" />                                       
		  				</f:facet>
		  				
		  			</h:column>
		  			
		  			<h:column>
		  				<f:facet name="header">
		  					<h:outputText value="#{msgs.text_cons_assist1}" />                                       
		  				</f:facet>
		  				  			
		  			</h:column>		  			  
		  			        	        	
         			<h:column>
		  				<f:facet name="header">
		  					<h:outputText value="#{msgs.text_cons_assist4}" />                                       
		  				</f:facet>
		  				
		  			</h:column>
		  			
		  			<h:column>
		  				<f:facet name="header">
		  					<h:outputText value="#{msgs.text_cons_assist6}" />                                       
		  				</f:facet>
				  		
		  			</h:column>
		  	
		  			<h:column>
		  				<f:facet name="header">
          					<h:outputText value="#{msgs.text_cons_historic3}"/>
		  				</f:facet>
				  		
				  		
		   			</h:column>
		  	
		  			<h:column>
		  				<f:facet name="header">
          					<h:outputText value="#{msgs.text_cons_assist8}"/>
				  		</f:facet>
				  				
				  	</h:column>
							  			
		  			<h:column>
		  				<f:facet name="header">
          					<h:outputText value=""/>
		  				</f:facet>
		  				<h:commandButton action="#{HDesk.veureprova}" value="#{msgs.text_cons_historic3}"/>
		  			</h:column>
		  	
 		  		 </sakai:flat_list>
		        </td></tr>
		       
		       		        
		        </table>
         		
         		</p></td></tr>
         		</table>
             </td></tr>
          </table>
	</h:form>	
	
	</sakai:view_container>
	<sakai:view_container rendered="#{HDesk.perfil!='support'}">
		<center><br><br>
				  <h:outputText style="font-weight:bold;color:red;" value="Error:"/> <h:outputText value="#{msgs.error}" /><br>
		</center> 
	</sakai:view_container>

</f:view>
