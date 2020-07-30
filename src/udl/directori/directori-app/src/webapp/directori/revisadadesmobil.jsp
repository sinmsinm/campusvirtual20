<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolcanvidadesmobil}">
		<h:form>
			<h2><h:outputText value="#{msgs.esteusegurs}" /></h2>

		<table>
		<tr>	
			<td><h:outputText value="#{msgs.mobil}" /></td>
			<td><h:outputText value="#{InscripcioMobil.novesDades.mobile}" /></td>
		</tr>
		<tr><td colspan="2" height="10px"></td></tr>
		<tr>
		<td></td>
		<td>
			<h:commandButton value="#{msgs.cancelar}" action="#{InscripcioMobil.onCancelarMobile}" />
			<h:commandButton value="#{msgs.finalitzar}" action="#{InscripcioMobil.onFinalitzarMobil}" />
		</td>
		</tr>
		</table>
		</h:form>
	</sakai:view_container>
</f:view>