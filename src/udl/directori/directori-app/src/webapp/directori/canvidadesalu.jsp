<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolredireccio}">
		<h:form>
			<jsp:include page="toolbar.jsp" />
			<h2><h:outputText value="#{msgs.titolredireccio}" /> </h2>
			
			<table>
				<tr>
					<td width="40%">
						<table>
							<tr>	
								<td><h:outputText value="#{msgs.nom}" /></td>
								<td><h:outputText value="#{CanviDadesTool.novesDades.cognoms}" />, <h:outputText value="#{CanviDadesTool.novesDades.nom}" /></td>
							</tr>
							<tr>
								<td><h:outputText value="#{msgs.correuprincipal}" /></td>
								<td><h:outputText value="#{CanviDadesTool.novesDades.correuprincipal}" /></td>
							</tr>
							<tr>
								<td><h:outputText value="#{msgs.correualternatiu}" /></td>
								<td><h:inputText value="#{CanviDadesTool.novesDades.correuAlternatiu}" /></td>
							</tr>    
							<tr>
								<td><h:outputText value="#{msgs.reenviamentcorreu}" /></td>
								<td><h:inputText value="#{CanviDadesTool.novesDades.reenviament}" /></td>
							</tr>
						</table>
					</td>
					<td width = "5%"></td>
					<td width = "50%"  valign="top">
						<h:outputText escape = "false" value="#{msgs.infodadesredireccio}" />
					</td>
					<td width = "5%"></td>
				</tr>
				<tr>
					<td colspan="4">
						<table width = "100%"><tr><td align="left">
								<h:commandButton value="#{msgs.netejar}" action="#{CanviDadesTool.onReiniciarAlu}" /> 
								<h:commandButton value="#{msgs.desar}" action="#{CanviDadesTool.onDesarAlu}" />
						</td></tr></table>
					</td>
				</tr>
			</table>
		</h:form>
	</sakai:view_container>
</f:view>
