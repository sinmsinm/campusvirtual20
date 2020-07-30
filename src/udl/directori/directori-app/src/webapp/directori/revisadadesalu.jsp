<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolredireccio}">
		<h:form>
			<h2><h:outputText value="#{msgs.esteusegurs}" /></h2>

		<table>
		<tr>	
			<td><h:outputText value="#{msgs.nom}" /></td>
			<td><h:outputText value="#{CanviDadesTool.novesDades.nom}" /></td>
		</tr>
		<tr>	
			<td><h:outputText value="#{msgs.cognoms}" /></td>
			<td><h:outputText value="#{CanviDadesTool.novesDades.cognoms}" /></td>
		</tr>
		<tr>	
			<td><h:outputText value="#{msgs.correuprincipal}" /></td>
			<td><h:outputText value="#{CanviDadesTool.novesDades.correuprincipal}" /></td>
		</tr>
				<tr>	
			<td><h:outputText value="#{msgs.correualternatiu}" /></td>
			<td><h:outputText value="#{CanviDadesTool.novesDades.correuAlternatiu}" /></td>
		</tr>
		<tr>	
			<td><h:outputText value="#{msgs.reenviamentcorreu}" /></td>
			<td>
				<h:outputText value="#{CanviDadesTool.novesDades.reenviament}" rendered="#{CanviDadesTool.novesDades.reenviament ne ''}" />
				<h:outputText value="#{msgs.sensecorreu}" rendered="#{CanviDadesTool.novesDades.reenviament eq ''}" />
			</td>
		</tr>
		<tr>
		<td></td>
		<td>
			<h:commandButton value="#{msgs.cancelar}" action="#{CanviDadesTool.onCancelarAlu}" />
			<h:commandButton value="#{msgs.finalitzar}" action="#{CanviDadesTool.onFinalitzarAlu}" />
		</td>
		</tr>
		</table>
		</h:form>
	</sakai:view_container>
</f:view>