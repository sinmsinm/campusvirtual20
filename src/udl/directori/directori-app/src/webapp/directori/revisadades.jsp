<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolcanvidades}">
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
				<td><h:outputText value="#{msgs.ubicacio}" /></td>
				<td><h:outputText value="#{CanviDadesTool.novesDades.ubicacio}" /></td>
			</tr>		
		
		<tr>	
			<td><h:outputText value="#{msgs.telefon}" /></td>
			<td><h:outputText value="#{CanviDadesTool.novesDades.tlf}" /></td>
		</tr>
		<tr>	
			<td><h:outputText value="#{msgs.fax}" /></td>
			<td><h:outputText value="#{CanviDadesTool.novesDades.fax}" /></td>
		</tr>		
		<tr>	
			<td><h:outputText value="#{msgs.webpersonal}" /></td>
			<td><h:outputText value="#{CanviDadesTool.novesDades.webpersonal}" /></td>
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
			<td><h:outputText value="#{msgs.reenviamentcorreu}" rendered="#{CanviDadesTool.novesDades.reenviament ne ''}" /></td>
			<td><h:outputText value="#{CanviDadesTool.novesDades.reenviament}" rendered="#{CanviDadesTool.novesDades.reenviament ne ''}" /></td>
		</tr>
		<tr>	
				<td><h:outputText value="#{msgs.missatgeria}" /></td>
				<td><h:outputText value="#{CanviDadesTool.novesDades.missatgeria}"/></td>
		</tr>	
		<tr>	
			<td></td>
			<td><h:outputText value="#{msgs.guardacorreu}" rendered="#{CanviDadesTool.novesDades.reenviament ne '' && CanviDadesTool.novesDades.guardacorreu}" /></td>
		</tr>
		<tr>
		<td></td>
		<td>
			<h:commandButton value="#{msgs.cancelar}" action="#{CanviDadesTool.onCancelar}" />
			<h:commandButton value="#{msgs.finalitzar}" action="#{CanviDadesTool.onFinalitzar}" />
		</td>
		</tr>
		</table>
		</h:form>
	</sakai:view_container>
</f:view>