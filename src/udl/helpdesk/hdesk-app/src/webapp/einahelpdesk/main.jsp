<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<link REL="stylesheet" HREF="stil/stil.css" TYPE="text/css">
<f:loadBundle basename="es.udl.asic.sakaiproject.tool.einahelpdesk.Messages" var="msgs" />


<f:view>
<sakai:view_container title="#{msgs.sample_title}">
	
	<h:form>
	<f:attribute name="prof" value="#{HDesk.perfil}" />
	<c:choose>
		<c:when test="${HDesk.perfil=='operador'}">
				<jsp:include page="operador/toolbar.jsp" />
		</c:when>
		<c:when test="${HDesk.perfil=='pas_pdi'}">
				<jsp:include page="pas_pdi/toolbar.jsp" />
		</c:when>
		<c:when test="${HDesk.perfil=='responsable'}">
				<jsp:include page="responsable/toolbar.jsp" />
		</c:when>
		<c:when test="${HDesk.perfil=='support'}">
				<jsp:include page="support/toolbar.jsp" />
		</c:when>
	</c:choose>
	
	<center><br><br>
		
		<table width="60% "bgcolor="#f0f0f0" border=0>            
			<tr><td><p align="justify">		
			
				<table cellspacing=0 cellpadding=0 border=0 align='center'>
				<tr><td>  
					<p valign="middle" align="center">
						<c:choose>
						<c:when test="${HDesk.perfil=='support'}">
							<h:outputText value="#{msgs.welcome_support0}" style="font-weight: bold; font-size: 15;"/> <br><br>
							<h:outputText value="#{msgs.welcome}" style="font-weight: bold; font-size: 20;"/> <br><br><br><br>
							<h:outputText value="#{msgs.welcome_support1}" style="font-weight: bold;"/> <br><br>
							<h:outputText value="#{msgs.welcome1}" style="font-weight: bold;"/> <br><br>
						</c:when>
						<c:when test="${HDesk.perfil=='operador'}">
						<h:outputText value="#{msgs.welcome_operador0}" style="font-weight: bold; font-size: 15;" /><br><br>
							<h:outputText value="#{msgs.welcome}" style="font-weight: bold; font-size: 20;"/> <br><br><br><br>
							<h:outputText value="#{msgs.welcome_operador1}" style="font-weight: bold;" /><br><br>
							<h:outputText value="#{msgs.welcome1}" style="font-weight: bold;"/> <br><br>
						</c:when>
						<c:when test="${HDesk.perfil=='responsable'}">
							<h:outputText value="#{msgs.welcome_responsable0}" style="font-weight: bold; font-size: 15;"/><br><br>
							<h:outputText value="#{msgs.welcome}" style="font-weight: bold; font-size: 20;"/> <br><br><br><br>
							<h:outputText value="#{msgs.welcome_responsable1}" style="font-weight: bold;"/><br><br>
							<h:outputText value="#{msgs.welcome1}" style="font-weight: bold;"/> <br><br>
							
						</c:when>
						<c:when test="${HDesk.perfil=='pas_pdi'}">
							<h:outputText value="#{msgs.welcome_paspdi0}" style="font-weight: bold; font-size: 15;"/><br><br>
							<h:outputText value="#{msgs.welcome}" style="font-weight: bold; font-size: 20;"/> <br><br><br><br>
							<h:outputText value="#{msgs.welcome_paspdi1}" style="font-weight: bold;"/><br><br>
							<h:outputText value="#{msgs.welcome1}" style="font-weight: bold;"/> <br><br><br>
							<h:outputLink value="#{HDesk.anuncisServeiInformatica}">
								<h:outputText value="#{msgs.info}" style="color: red;"/>
							</h:outputLink>
							<br>
							<h:outputText value="#{msgs.contact}" /><br><br>
						</c:when>
						<c:otherwise>
							<center><br><br>
							<font color="red"><b>Error:</b></font> <h:outputText value="#{msgs.error}" /><br>
							</center>
						</c:otherwise>
						</c:choose>
					</p>
				</td></tr>
				</table>	
			
			</p></td></tr>
			</table>
	</center>
	
</h:form>

</sakai:view_container>
</f:view>
