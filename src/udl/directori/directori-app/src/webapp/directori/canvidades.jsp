<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>


<f:loadBundle basename="es.udl.asic.tool.directori.bundle.Messages" var="msgs"/>
<f:view>
	<sakai:view_container title="#{msgs.titolcanvidades}">
		<h:form>
		<jsp:include page="toolbar.jsp" />
		<h2><h:outputText value="#{msgs.titolcanvidades}" /> </h2>
		
		<table>
		<tr>
		<td width="40%">
			<table>
			<tr>	
				<td><h:outputText value="#{msgs.nom}" /></td>
				<td><h:inputText value="#{CanviDadesTool.novesDades.nom}" /></td>
			</tr>
			<tr>	
				<td><h:outputText value="#{msgs.cognoms}" /></td>
				<td><h:inputText value="#{CanviDadesTool.novesDades.cognoms}" /></td>
			</tr>
			<tr>	
				<td><h:outputText value="#{msgs.ubicacio}" /></td>
				<td><h:inputText value="#{CanviDadesTool.novesDades.ubicacio}" /></td>
			</tr>		
			<tr>	
				<td><h:outputText value="#{msgs.telefon}" /></td>
				<td><h:inputText value="#{CanviDadesTool.novesDades.tlf}" /></td>
			</tr>
			<tr>	
				<td><h:outputText value="#{msgs.fax}" /></td>
				<td><h:inputText value="#{CanviDadesTool.novesDades.fax}" /></td>
			</tr>		
			<tr>	
				<td><h:outputText value="#{msgs.webpersonal}" /></td>
				<td><h:inputText value="#{CanviDadesTool.novesDades.webpersonal}" /></td>
			</tr>
			<tr>
				<td><h:outputText value="#{msgs.correualternatiu}" /></td>
				<td><h:inputText  value="#{CanviDadesTool.novesDades.correuAlternatiu}" /></td>
			</tr> 
			<tr>	
				<td><h:outputText value="#{msgs.missatgeria}" /></td>
				<td><h:inputText value="#{CanviDadesTool.novesDades.missatgeria}" rendered="#{CanviDadesTool.estatMissatgeria==0}"/>
				<h:inputText value="#{CanviDadesTool.novesDades.missatgeria}" style="background:#f0f0f0" readonly="true" rendered="#{CanviDadesTool.estatMissatgeria==1}"/>
				<h:outputText value="#{msgs.missatgeria1}" /></td>
			</tr>	
			<tr>
				<td><h:outputText value="#{msgs.reenviamentcorreu}" /></td>
				<td><h:inputText value="#{CanviDadesTool.novesDades.reenviament}" /></td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox  value="#{CanviDadesTool.novesDades.guardacorreu}" /></td>
				<td><h:outputText value="#{msgs.guardacorreu}" /></td>
			</tr> 
			</table>
		</td>
		<td width = "5%"></td>
		<td width = "50%"  valign="top">
			<h:outputText value="#{msgs.infodades}" /><br><br><br>
			<h:outputText value="#{msgs.infodades1}" />
			<b><h:outputText value="#{msgs.infodades11}" /></b>
			<h:outputText value="#{msgs.infodades111}" />
			<h:outputText value="#{msgs.infodades1111}" style="color:red"/>
			<h:outputText value="#{msgs.infodades11111}" /><br>
			<h:outputText value="#{msgs.infodades2}" /><br><br>
			<h:outputText escape = "false" value="#{msgs.infodadesredireccio}" />
		</td>
		<td width = "5%"></td>
		</tr>
		<tr>
		<td colspan="4">
			
			
		<!-- 	<table width="100%">
			<tr>
				<td align="left">
					<h3>
						<h:commandLink action="#{CanviDadesTool.desplega}"> 
							<h:graphicImage url="image/collapse.gif" rendered="#{CanviDadesTool.desplegat}" />
							<h:graphicImage url="image/expand.gif" rendered="#{!CanviDadesTool.desplegat}" />
							<h:outputText value="#{msgs.configcorreu}" />
						</h:commandLink>
					</h3>
				</td>
				<td align="right"><h:outputText value="#{msgs.configcorreuavis}" rendered="#{!CanviDadesTool.desplegat}"/></td>
			</tr>
			</table>		
			
			<h:panelGrid columns="2" rendered="#{CanviDadesTool.desplegat}" >
			
				<h:outputText value="#{msgs.correuprincipal}" />
				
				<h:selectOneMenu value="#{CanviDadesTool.novesDades.correuprincipal}" >	       	
					<f:selectItems value="#{CanviDadesTool.llistaCorreus}" />
		       	</h:selectOneMenu>
				
	
			</h:panelGrid> -->

			<table width = "100%"><tr><td align="left">
					<h:commandButton value="#{msgs.netejar}" action="#{CanviDadesTool.onReiniciar}" />
					<h:commandButton value="#{msgs.desar}" action="#{CanviDadesTool.onDesar}" />
			</td></tr></table>
		</td>
			</tr>
			</table>
		</h:form>
	</sakai:view_container>
</f:view>