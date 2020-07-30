<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolcanviapw}">
		<h:form>
	

		<jsp:include page="toolbar.jsp" />
		<h2><h:outputText value="#{msgs.titolcanvidades}" /> </h2>
		<table>
			<tr>
			<td width = "40%">
			<table>
				<tr>	
					<td><h:outputText value="#{msgs.clauantiga}" /></td>
					<td><h:inputSecret value="#{CanviPwTool.pwOld}" /></td>
				</tr>
				<tr>	
					<td><h:outputText value="#{msgs.novaclau}" /></td>
					<td><h:inputSecret value="#{CanviPwTool.pwNew}" /></td>
				</tr>
				<tr>	
					
					<td><h:outputText value="#{msgs.novaclau}" /><br /> <h:outputText value="#{msgs.repeticio}" /></td>
					<td><h:inputSecret value="#{CanviPwTool.pwNew2}" /></td>
					
				</tr>
				<tr>
				<td></td>
				<td>
					<h:commandButton value="#{msgs.netejar}" action="#{CanviPwTool.onReiniciar}" />
					<h:commandButton value="#{msgs.desar}" action="#{CanviPwTool.onDesar}" />
					
				</td>
				</tr>
				</table>
				</td>
				<td width = "5%"></td>
				<td width ="50%" valign="top">
						<h:outputText value="#{msgs.infopw}" />	
				</td>
				<td width="5%" ></td>
				</tr>
			</table>
		</h:form>
	</sakai:view_container>
</f:view>