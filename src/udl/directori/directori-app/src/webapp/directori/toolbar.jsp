<%@ page import ="javax.faces.application.*,javax.faces.context.*" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 

<%
	FacesContext fc = FacesContext.getCurrentInstance();
 	String value = (String)fc.getApplication().createValueBinding("#{DirectoriTool.userIsInDirectory}").getValue(fc);

%>

<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs" />

<sakai:view_content>
		<sakai:tool_bar>
			<sakai:tool_bar_item action="#{DirectoriTool.goCerca}" value="#{msgs.gocerca}" rendered="#{DirectoriTool.userIsInDirectory=='pdi/pas' || DirectoriTool.userIsInDirectory=='student'}"  />
			<sakai:tool_bar_item action="#{DirectoriTool.goCanviDadesAlu}" value="#{msgs.gocanvidadesalu}" rendered="#{DirectoriTool.userIsInDirectory=='student'}" />
			<sakai:tool_bar_item action="#{DirectoriTool.goCanviDades}" value="#{msgs.gocanvidades}" rendered="#{DirectoriTool.userIsInDirectory=='pdi/pas'}" />
			<sakai:tool_bar_item action="#{DirectoriTool.goCanviPasswd}" value="#{msgs.gocanvipw}" rendered="#{DirectoriTool.userIsInDirectory=='pdi/pas' || DirectoriTool.userIsInDirectory=='student'}"/>
		</sakai:tool_bar>
</sakai:view_content>