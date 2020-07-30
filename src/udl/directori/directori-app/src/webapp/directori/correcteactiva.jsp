<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>

<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolactiva}">

		<h2><h:outputText value="#{msgs.activacorrecte}" /> <br /></h2><br />
		
		<h:form>
			<h:outputText value="#{msgs.activaResum}" /> 
				<table>
			<tr>	
				<td><b><h:outputText value="#{msgs.nom}" /></b></td>
				<td><h:outputText value="#{ActivaTool.nom}" /></td>
			</tr>
			
			<tr>	
				<td><b><h:outputText value="#{msgs.cognoms}" /></b></td>
				<td><h:outputText value="#{ActivaTool.cognoms}" /></td>
			</tr>
			<tr>	
					<td><b><h:outputText value="#{msgs.activalogin}" /></b></td>
					<td><h:outputText value="#{ActivaTool.login}" /></td>
				</tr>		
			<tr>	
					<td><b><h:outputText value="#{msgs.dni}" /></b></td>
					<td><h:outputText value="#{ActivaTool.dni}" /></td>
				</tr>		
			
			<tr>	
				<td><b><h:outputText value="#{msgs.correuprincipal}" /></b></td>
				<td><h:outputText value="#{ActivaTool.emailComplet}" /></td>
			</tr>
			</table>
			<br />
			
			<jsp:include page="infocorrecte.html" />

			
			<h:commandButton value="#{msgs.dacord}" action="#{ActivaTool.onOkConfirmar}" />
			
		</h:form>
	</sakai:view_container>
</f:view>