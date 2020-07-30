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
						<jsp:include page="introactivaMail.html" />
			
				</td></tr>
				<tr><td>
				<table cellspacing ="2px">
				 	<tr>
						<td align="right""><h:outputText value="#{msgs.activaemail}" /></td>

						<td  align="left" colspan="4">
							<h:inputText  value="#{ActivaTool.email}" />
							<h:outputText value="#{ActivaTool.domini}" />
						</td>
						
					</tr> 
				</table>
			</td></tr></table>
			<br /><br />
			
			
			<h:commandButton action="#{ActivaTool.activaCorreu}" value="#{msgs.activaactiva}"/>
			<h:commandButton action="#{ActivaTool.cancela}" value="#{msgs.activacancela}"/>
			
		</h:form>
	</sakai:view_container>
</f:view>
		