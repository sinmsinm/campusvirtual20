<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>

<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolactiva}">
		<h:form>
		<h2><h:outputText value="#{msgs.activaerror}" /> </h2><br/>
				<h:outputText value="#{ActivaTool.errorMsg}" /> <br /><br />
			<h:commandButton value="#{msgs.dacord}" action="#{ActivaTool.onOkError}" />
		</h:form>
	</sakai:view_container>
</f:view>