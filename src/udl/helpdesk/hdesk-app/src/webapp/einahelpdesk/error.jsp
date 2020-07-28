<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<link REL="stylesheet" HREF="stil/stil.css" TYPE="text/css">
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs"/>

<f:view>
	<sakai:view_container title="#{msgs.sample_title}">
		<h:form>
			<sakai:tool_bar>
				<sakai:tool_bar_item action="#{HDesk.RetornaNovaAssist}"	value="#{msgs.text_error2}" />
			</sakai:tool_bar>			
		</h:form>
		<sakai:view_content>
			<p valign="middle" align="center">
				<h:outputText value="#{msgs.text_err_pas_pdi}" style="font-weight: bold;"/> <br><br>
				<h:outputText value="#{msgs.text_err_pas_pdi1}" style="font-weight: bold;"/> <br>
				<h:outputText value="#{msgs.text_err_pas_pdi2}" style="font-weight: bold;"/> <br>
			</p>
		</sakai:view_content>

</sakai:view_container>
</f:view>
