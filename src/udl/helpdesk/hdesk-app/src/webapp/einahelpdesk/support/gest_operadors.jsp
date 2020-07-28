<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>


<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs"/>

<f:view>
	
	<sakai:view_container title="#{msgs.sample_title2}" rendered="#{HDesk.perfil=='support'}">
		
	<h:form>
		  <jsp:include page="toolbar.jsp" />
		
		  <br>
		  		  
		  <table width="50%" align="center" cellpadding=0 cellspacing=0 border=0>
                <tr><td align="center"><h3><h:outputText value="#{msgs.text_gest_operadors}"/></h3></td></tr>

                <tr><td>
                <p valign="middle" align="center">
					<h:outputText value="#{msgs.text_gest_operadors1}" style="font-weight: bold;"/>
				</p>
												
				<table width="100%" bgcolor="#f0f0f0" border=0>                
                <tr><td>
                	<h:outputText value="#{msgs.usuari}" /> 
                	<h:outputText value="#{msgs.administrador}" style="font-weight: bold; font-size: 15;"/><br><br> 
                	
                </td></tr>
                
                <tr><td><p align="justify">
                
                <table width="100%" bgcolor="#dddfe4" border=0>
                <tr><td>
                          <h:outputText value="#{msgs.text_gest_operadors2}" />    
                </td></tr>   
                <tr><td>
                	<table width="100%" bgcolor="#f0f0f0" border=0>
                	<tr><td>
                	<br>
                	<h:outputText value="#{msgs.text_gest_operadors4}" />  
                	<h:selectOneMenu value="#{HDesk_support.tecnic_sel}">
				<f:selectItems value="#{HDesk_support.operadors}" /><br>
  			</h:selectOneMenu> 			
			
			</td>
			
			<td aling="left" width="80%">		
			<br><br>
			<sakai:button_bar>
			<sakai:button_bar_item value="#{msgs.text_gest_operadors6}" action="#{HDesk_support.EsborraOperador}" disabled="true" /> 
			<sakai:button_bar_item value="#{msgs.text_gest_operadors8}" action="#{HDesk_support.ActivaOperador}" disabled="false" />
			<sakai:button_bar_item value="#{msgs.text_gest_operadors9}" action="#{HDesk_support.DesactivaOperador}" disabled="false" />
			</sakai:button_bar>
			</td>
			</tr>
			<tr><td>	<h:outputText value="#{msgs.avis}"/>	<br>	</td></tr>
                	</table>
                </td></tr>
                </table>
         		
         		</p></td></tr>
         		
         		
         		<tr><td>	<br>	</td></tr>
         		
         		
         		<tr><td><p align="justify">
                
                <table width="100%" bgcolor="#dddfe4" border=0>
                <tr><td>
                          <h:outputText value="#{msgs.text_gest_operadors3}" />  
                </td></tr>   
                <tr><td>
                	<table width="100%" bgcolor="#f0f0f0" border=0>
                	<tr><td>
                		<br>
                		<h:outputText value="#{msgs.text_gest_operadors5}" /> <br>   
                		<h:inputText value="#{HDesk_support.id_operador}" required="false" size="20" />
						
						</td>
						<td aling="left" width="80%">		
						<br><br>
						<sakai:button_bar>
							<sakai:button_bar_item value="#{msgs.text_gest_operadors7}" action="#{HDesk_support.CreaOperador}"/>
						</sakai:button_bar>
						</td>
						</tr>  
                	<tr><td>	<br>	</td></tr>
                	</table>
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
