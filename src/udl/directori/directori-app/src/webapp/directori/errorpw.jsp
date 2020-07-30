<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>

<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.smstitle_name}">
		<h:form>
			<h2><h:outputText value="#{CanviPwTool.errorMsg}" /></h2> <br /><br />
			<h:commandButton value="#{msgs.dacord}" action="#{CanviPwTool.onOkError}" />
		</h:form>
	</sakai:view_container>
</f:view>
