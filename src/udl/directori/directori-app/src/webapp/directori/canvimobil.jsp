<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolcanvimobil}">
		<h:form>
		<jsp:include page="toolbar.jsp" />
		<h2><h:outputText value="#{msgs.titolcanvimobil}" /> </h2>
		
		<table>
		<tr>
		<td width="100%" colspan="4">
			<table>
		
			<tr>	
				<td><h:outputText value="#{msgs.mobil}" /></td>
				<td><h:inputText value="#{InscripcioMobil.novesDades.mobile}" maxlength="9"/><i> <h:outputText value="#{msgs.patromobil}" /></i></td>
			</tr>
			<tr>
							<td colspan="2"> <br/><br/><h:outputText value="#{msgs.textAcceptacio}" /></td>
			</tr>
			</table>
		</td>
		
		<tr>
		<td colspan="4">
			
			
		

			<table width = "100%"><tr><td align="left">
					<h:commandButton value="#{msgs.netejar}" action="#{InscripcioMobil.onReiniciarMobile}" />
					<h:commandButton value="#{msgs.desaMobil}" action="#{InscripcioMobil.onDesarMobil}" />
			</td></tr></table>
		</td>
			</tr>
			</table>
		</h:form>
	</sakai:view_container>
</f:view>