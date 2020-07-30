<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolactiva}">
		<h:form>
			<h2><h:outputText value="#{msgs.titolactiva}" /></h2>
			
			<table><tr>
				<td valign="center">
						<jsp:include page="introactiva.html" />
			
				</td></tr>
				<tr><td>
				<table cellspacing ="2px">
					<tr>
						<td align="right"><h:outputText value="#{msgs.activalogin}" /></td>
						<td align="left"><h:inputText value="#{ActivaTool.login}" /></td>
						<td width="12px"></td>						
						<td align="right"><h:outputText value="#{msgs.activacodi}" /></td>
						<td align="left"><h:inputSecret value="#{ActivaTool.codi}" /></td>
					</tr>
					<tr>
						<td align="right"><h:outputText value="#{msgs.activapassword}" /></td>
						<td align="left"><h:inputSecret value="#{ActivaTool.password}" /></td>
						<td width="12px"></td>						
						<td align="right"><h:outputText value="#{msgs.activapasswordveri}" /></td>
						<td align="left"><h:inputSecret value="#{ActivaTool.passwordVerificacio}" /></td>
					</tr>
				</table>
			</td></tr></table>
			<br /><br />
			
			
			<h:commandButton action="#{ActivaTool.activa}" value="#{msgs.activaactiva}"/>
			<h:commandButton action="#{ActivaTool.cancela}" value="#{msgs.activacancela}"/>
			
		</h:form>
	</sakai:view_container>
</f:view>