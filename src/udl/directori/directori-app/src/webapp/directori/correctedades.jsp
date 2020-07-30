<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>

<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolcanvidades}">
		<h:outputText value="#{msgs.correctedades}" /> <br /><br />
		<h:form>
			<h2><h:commandButton value="#{msgs.dacord}" action="#{CanviDadesTool.onOkConfirmar}" /></h2>
		</h:form>
	</sakai:view_container>
</f:view>
