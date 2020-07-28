<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msg" />

	<sakai:view_content>
		<sakai:tool_bar>
			<sakai:tool_bar_item action="#{HDesk.processActionNovaAssistencia}" value="#{msg.paspdi_tool1}" />
			<sakai:tool_bar_item action="#{HDesk_support.processActionConsultaAssis}" value="#{msg.support_tool1}" />
			<sakai:tool_bar_item action="#{HDesk_support.processActionConsultaHistoric}" value="#{msg.support_tool2}" />
			<sakai:tool_bar_item action="#{HDesk_support.processGestioTipus}" value="#{msg.support_tool3}" />
			<sakai:tool_bar_item action="#{HDesk_support.processGestioResponsables}" value="#{msg.support_tool4}" />
			<sakai:tool_bar_item action="#{HDesk_support.processActionGoCreaOperador}" value="#{msg.support_tool5}" />
			<sakai:tool_bar_item action="#{HDesk_support.processActionGoPregFrequents}" value="#{msg.support_tool6}" />
			</sakai:tool_bar>
	</sakai:view_content>
