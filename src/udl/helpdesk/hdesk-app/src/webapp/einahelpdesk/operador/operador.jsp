<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>


<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />


<f:view>
<sakai:view_container title="#{msgs.sample_title}">
	<sakai:view_content>
	<h:form>
<!--	<jsp:include page="toolbar.jsp" />-->
	<center>
	<h:outputText value="#{msgs.welcome}" /><br><br>
	<h:outputText value="#{msgs.welcome1}" /><br><br>
		
<sakai:button_bar>
                <sakai:button_bar_item value="#{msgs.boto_continuar}" action="#{HDesk.processAction}"/>
</sakai:button_bar>
	</center>
</h:form>
</sakai:view_content>
</sakai:view_container>
</f:view>
