<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>


<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />


<f:view>
<sakai:view_container title="#{msgs.sample_title}" rendered="#{HDesk.perfil=='support'}">
	
	<h:form>
	<jsp:include page="toolbar.jsp" />
	<br>
	
	
	<table width="50%" align="center" cellpadding=0 cellspacing=0 border=0>
    	<tr><td align="center"><h3><h:outputText value="#{msgs.text_gestio_respon}"/></h3></td></tr>

        <tr><td>
        <p valign="middle" align="center">
			<h:outputText value="#{msgs.text_gestio_respon1}" style="font-weight: bold;"/>
		</p>
												
		<table width="100%" bgcolor="#f0f0f0" border=0>                
        <tr><td>
        	<h:outputText value="#{msgs.usuari}" /> 
            <h:outputText value="#{msgs.administrador}" style="font-weight: bold; font-size: 15;"/><br><br> 
                	
		</td></tr>
           
        <tr><td><p align="justify">
                
                <table width="100%" bgcolor="#dddfe4" border=0>
                <tr><td>
                          <h:outputText value="#{msgs.text_gestio_respon5}" />    
                </td></tr>   
                <tr><td>
                	<table width="100%" bgcolor="#f0f0f0" border=0> 
         				<tr><td>
        				<br>
        				<h:outputText value="#{msgs.text_gestio_respon2}"/>
   						<h:selectOneListbox value="#{HDesk_support.campus_sel}">
					  		<f:selectItems value="#{HDesk_support.responsables}" />
  						</h:selectOneListbox>   
					</td><td valing="middle" width="20%">
						
						<sakai:button_bar>
							<sakai:button_bar_item value="#{msgs.text_gestio_respon3}" action="#{HDesk_support.processActionSubstituir}"/>
						</sakai:button_bar>
						
						</td><td valign="top">
  							<br>
  							<h:outputText value="#{msgs.text_gestio_respon4}"/>
  							<h:selectOneMenu value="#{HDesk_support.tecnic_sel}">
					  			<f:selectItems value="#{HDesk_support.operadors}" /><br>
  							</h:selectOneMenu>   
  							<h:outputText value="#{msgs.avis}"/>
							</td></tr>
						<tr><td>	<br>	</td></tr>
					</table>
 				</table>
 		</td></tr>
 
		</table>

	</table>
	
	</h:form>
	
</sakai:view_container>
<sakai:view_container rendered="#{HDesk.perfil!='support'}">
		<center><br><br>
				  <h:outputText style="font-weight:bold;color:red;" value="Error:"/> <h:outputText value="#{msgs.error}" /><br>
		</center> 
	</sakai:view_container>
</f:view>
